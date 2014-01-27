package org.robospock

import com.android.SdkConstants
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Przemek Jakubczyk on 1/25/14.
 */
class RobospockSpecification extends Specification {


    def "check number of dependencies"() {

        when:
        Project rootProject = ProjectBuilder.builder().build()

        Project androidProject = createAndroidProject(rootProject)

         createAndroidLibraryProject(rootProject, "aaa")


        androidProject.dependencies {
            compile 'com.jakewharton:butterknife:4.0.1'
            compile rootProject.project("aaa")
        }


        RobospockAction robospockAction = new RobospockAction()


        def dependencies = robospockAction.getProjectDependencies(androidProject)

        then:
        dependencies.size() == 2
    }

    def "check number of maven dependencies"(){
        when:
        Project rootProject = ProjectBuilder.builder().build()

        Project androidProject = createAndroidProject(rootProject)

        createAndroidLibraryProject(rootProject, "aaa")


        androidProject.dependencies {
            compile 'com.jakewharton:butterknife:4.0.1'
            compile rootProject.project("aaa")
        }


        RobospockAction robospockAction = new RobospockAction()


        def dependencies = robospockAction.getProjectDependencies(androidProject)

        def maven= robospockAction.getProjectMavenDependencies(dependencies)

        then:
        maven.size() == 1
    }

    def "check number of project dependencies"(){
        when:
        Project rootProject = ProjectBuilder.builder().build()

        Project androidProject = createAndroidProject(rootProject)

        createAndroidLibraryProject(rootProject, "aaa")


        androidProject.dependencies {
            compile 'com.jakewharton:butterknife:4.0.1'
            compile rootProject.project("aaa")
        }


        RobospockAction robospockAction = new RobospockAction()


        def dependencies = robospockAction.getProjectDependencies(androidProject)

        def libraries = robospockAction.getProjectLibraryDependencies(dependencies)

        then:
        libraries.size() == 1
    }


    Project createAndroidProject(Project rootProject) {
        Project androidProject = ProjectBuilder.builder().withParent(rootProject).build()
        def file = new File(androidProject.rootDir, SdkConstants.FN_LOCAL_PROPERTIES);
        file.write("sdk.dir=/home")

        androidProject.buildscript {
            repositories {
                mavenCentral()
            }
            dependencies {
                classpath 'com.android.tools.build:gradle:0.7.+'
            }
        }

        androidProject.apply plugin: 'android'

        androidProject.repositories {
            mavenCentral()
        }

        return androidProject
    }

    Project createAndroidLibraryProject(Project rootProject, String name) {
        Project androidProject = ProjectBuilder.builder().withParent(rootProject).withName(name).build()
        def file = new File(androidProject.rootDir, SdkConstants.FN_LOCAL_PROPERTIES);
        file.write("sdk.dir=/home")

        androidProject.buildscript {
            repositories {
                mavenCentral()
            }
            dependencies {
                classpath 'com.android.tools.build:gradle:0.7.+'
            }
        }

        androidProject.apply plugin: 'android-library'

        androidProject.repositories {
            mavenCentral()
        }

        return androidProject
    }
}
