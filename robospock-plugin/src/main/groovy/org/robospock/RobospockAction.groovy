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
        def projectDependencies

        project.dependencies.each {
            projectDependencies = it.configurationContainer.all.find {
                it.name == 'default'
            }.getAllDependencies()
        }

        def dependency = projectDependencies.find { it instanceof DefaultProjectDependency }

        def projectName = dependency.dependencyProject.name

        def androidProjects = findAndroidProjects(projectName, project)

        if (androidProjects.size() != 1) throw new GradleException("Found zero or more than two projects to test")

        def androidProject = androidProjects.first()

        // collect and extract compiled classes in library project
        def subprojects = getSubprojects(androidProject)

        // collect and forward all maven dependencies
        // TODO: refactor
        def allprojects = []
        allprojects.addAll(subprojects)
        allprojects.add(androidProject)
        def mavenDependencies = collectMavenDependencies(allprojects)

        mavenDependencies.each { dep ->
            project.dependencies {
                compile group: dep.group, name: dep.name, version: dep.version
            }
        }

        allprojects.each { proj ->

            def rDir = new File(proj.buildDir.path + "/source/r").list()[0]
            project.sourceSets {
                main {
                    java {
                        srcDir proj.android.sourceSets.main.java
                        srcDir proj.buildDir.path + "/source/r/" + rDir
                    }
                    resources {
                        srcDir proj.android.sourceSets.main.res
                    }
                }
            }
        }

        Test test = project.getTasks().create(ROBOSPOCK_TASK_NAME, Test.class);
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

    def getAndroidPlugin(Project project){
        if(project.plugins.hasPlugin("android"))
            project.plugins["android"]
        else
            project.plugins["android-library"]
    }
}