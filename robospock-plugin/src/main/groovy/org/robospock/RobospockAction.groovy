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

        // collect android project dependencies
        def androidProjectDependenciesList = getProjectDependencies(androidProject)



        def mavenDependencies = getProjectMavenDependencies(androidProjectDependenciesList)

        def androidLibraryDependencies = getProjectLibraryDependencies(androidProjectDependenciesList)


        androidLibraryDependencies.each {
            addMavenDependencies(mavenDependencies, it)
        }


        println "deeeps"

        mavenDependencies.each { println it}

        androidLibraryDependencies.each {

            findAndroidProjects(findProjectByDependency(project, it)).each {


                getProjectDependencies(it)

            }
        }

        mavenDependencies.each { dep ->
            project.dependencies {
                compile group: dep.group, name: dep.name, version: dep.version
            }
        }

        // add support for library

        def appPlugin = androidProject.plugins["android"]
        // take first output dir on found variant
        def defaultOutputDir = appPlugin.variantDataList[0].variantConfiguration.mDirName

        def classesDir = androidProject.buildDir.path + '/classes/' + defaultOutputDir
        def resDir = androidProject.buildDir.path + '/res/all/' + defaultOutputDir

        project.dependencies {
            compile project.files(classesDir)
            compile project.files(resDir)
        }



        Test test = project.getTasks().create(ROBOSPOCK_TASK_NAME, Test.class);
        project.getTasks().getByName(JavaBasePlugin.CHECK_TASK_NAME).dependsOn(test);
        test.setDescription("Runs the unit tests using Robospock.")
        test.setGroup(JavaBasePlugin.VERIFICATION_GROUP)

        test.workingDir = project.getRootProject().projectDir

        test.dependsOn(androidProject.getTasks().findByName('assemble'))
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

    def findProjectByDependency(Project project, DefaultProjectDependency dependency){
        project.rootProject.allprojects.find { it.name == dependency.dependencyProject.name}
    }

    def getProjectDependencies(Project androidProject) {
        def androidProjectDependenciesList = new ArrayList()

        androidProject.dependencies.each {
            androidProjectDependenciesList = it.configurationContainer.all.find {
                it.name == 'compile'
            }.getAllDependencies()
        }

        return androidProjectDependenciesList
    }

    def getProjectMavenDependencies(def dependencies) {
        dependencies.findAll { it instanceof DefaultExternalModuleDependency }
    }

    def getProjectLibraryDependencies(def dependencies) {
        dependencies.findAll { it instanceof DefaultProjectDependency }
    }

    void addMavenDependencies(def collection, def dependencies){
        collection.add( getProjectMavenDependencies (dependencies) )
    }
}