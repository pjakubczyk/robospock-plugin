package org.robospock

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.testing.Test

class RobospockAction implements Action<Project> {

    public static final String ROBOSPOCK_TASK_NAME = "robospock"

    @Override
    void execute(Project project) {
        def androidProject = project.project(project.ext.robospock)

        // collect and extract compiled classes in library project
        def subprojects = getSubprojects(androidProject)

        // collect and forward all maven dependencies
        def allprojects = subprojects + androidProject
        def mavenDependencies = collectMavenDependencies(allprojects)

        mavenDependencies.each { dep ->
            project.dependencies {
                compile group: dep.group, name: dep.name, version: dep.version
            }
        }

        def allSourceSets = ['src/main/java']
        def allResourcesSets = ['src/main/res']

        allprojects.each { proj ->
            def rDir
            if (proj.plugins.hasPlugin("android"))
                rDir = (proj.android.applicationVariants as List)[0].dirName
            else
                rDir = (proj.android.libraryVariants as List)[0].dirName

            allSourceSets.addAll(proj.android.sourceSets.main.java.srcDirs)
            allSourceSets.add(proj.buildDir.path + "/source/r/" + rDir)

            allResourcesSets.add(proj.android.sourceSets.main.res)
        }

        project.sourceSets.main{
            java.srcDirs = allSourceSets
            resources.srcDirs = allResourcesSets
        }

        Test test = project.tasks.create(ROBOSPOCK_TASK_NAME, Test.class);
        project.getTasks().getByName(JavaBasePlugin.CHECK_TASK_NAME).dependsOn(test);
        test.setDescription("Runs the unit tests using Robospock.")
        test.setGroup(JavaBasePlugin.VERIFICATION_GROUP)

        test.workingDir = project.getRootProject().projectDir

        test.dependsOn(androidProject.getTasks().findByName('assembleDebug'))
    }

    def findAndroidProjects(String name, Project project) {
        project.rootProject.allprojects.findAll {
            it.name == name && (it.plugins.hasPlugin("android") || it.plugins.hasPlugin("android-library"))
        }
    }

    def findAndroidProjects(Project project) {
        project.allprojects.findAll {
            it.plugins.hasPlugin("android") || it.plugins.hasPlugin("android-library")
        }
    }

    def findProjectByDependency(Project project, DefaultProjectDependency dependency) {
        project.rootProject.allprojects.find { it.name == dependency.dependencyProject.name }
    }

    def findCompileDependencies(Project androidProject) {
        def androidProjectDependenciesList = new ArrayList()

        androidProject.dependencies.each {
            androidProjectDependenciesList = it.configurationContainer.all.find {
                it.name == 'compile'
            }.getAllDependencies()
        }

        return androidProjectDependenciesList
    }

    def findMavenDependencies(Project androidProject) {
        findCompileDependencies(androidProject).findAll {
            it instanceof DefaultExternalModuleDependency
        }
    }

    def findLibraryDependencies(Project androidProject) {
        findCompileDependencies(androidProject).findAll { it instanceof DefaultProjectDependency }
    }

    // ----------- extract all libraries --------------
    def getSubprojects(Project androidProject) {
        def projects = []

        extractSubprojects(androidProject, projects)

        projects
    }

    def extractSubprojects(Project libraryProject, List<Project> projects) {
        def projectLibraryDependencies = findLibraryDependencies(libraryProject)

        def collect = projectLibraryDependencies.collect {
            findProjectByDependency(libraryProject, it)
        }

        collect.each { extractSubprojects(it, projects) }

        projects.addAll(collect)
    }
    // ----------- end of section --------------

    def collectMavenDependencies(List<Project> projects) {
        def collection = []

        projects.each { collection.addAll(findMavenDependencies(it)) }

        collection
    }

    def getAndroidPlugin(Project project) {
        project.plugins.hasPlugin("android") ? project.plugins["android"] : project.plugins["android-library"]
    }
}