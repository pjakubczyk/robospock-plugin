package org.robospock

import com.android.SdkConstants
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Przemek Jakubczyk on 1/25/14.
 */
class RobospockSpecification extends Specification {

    static final LIB_PROJECT_NAME = "sample_name"
    static final SAMPLE_MAVEN_DEP = "com.jakewharton:butterknife:4.0.1"

    static final ANDROID_PLUGIN_PATH = 'com.android.tools.build:gradle:0.7.+'

    Project rootProject
    Project androidProject
    RobospockAction robospockAction

    def setup() {
        rootProject = ProjectBuilder.builder().build()
        androidProject = createAndroidProject(rootProject)
        robospockAction = new RobospockAction()
    }

    def "check number of dependencies"() {
        setup: "create library project"
        createAndroidLibraryProject(rootProject, LIB_PROJECT_NAME)

        and: "add dependencies to android project"
        androidProject.dependencies {
            compile SAMPLE_MAVEN_DEP
            compile rootProject.project(LIB_PROJECT_NAME)
        }

        when: "extract all dependencies"
        def dependencies = robospockAction.getProjectDependencies(androidProject)

        then:
        dependencies.size() == 2
    }

    def "check number of maven dependencies"() {
        setup: "create library project"
        createAndroidLibraryProject(rootProject, LIB_PROJECT_NAME)

        and: "add dependencies to android project"
        androidProject.dependencies {
            compile SAMPLE_MAVEN_DEP
            compile rootProject.project(LIB_PROJECT_NAME)
        }

        and: "extract all dependencies"
        def dependencies = robospockAction.getProjectDependencies(androidProject)

        when: "extract maven dependencies"
        def maven = robospockAction.getProjectMavenDependencies(dependencies)

        then:
        maven.size() == 1
    }

    def "check number of project dependencies"() {
        setup: "create library project"
        createAndroidLibraryProject(rootProject, LIB_PROJECT_NAME)

        and: "add dependencies to android project"
        androidProject.dependencies {
            compile SAMPLE_MAVEN_DEP
            compile rootProject.project(LIB_PROJECT_NAME)
        }

        and: "extract all dependencies"
        def dependencies = robospockAction.getProjectDependencies(androidProject)

        when: "extract library dependencies"
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
                classpath ANDROID_PLUGIN_PATH
            }
        }

        androidProject.apply plugin: 'android'

        androidProject.repositories {
            mavenCentral()
        }

        androidProject
    }

    Project createAndroidLibraryProject(Project rootProject, String name) {
        Project androidLibraryProject = ProjectBuilder.builder().withParent(rootProject).withName(name).build()
        def file = new File(androidLibraryProject.rootDir, SdkConstants.FN_LOCAL_PROPERTIES);
        file.write("sdk.dir=/home")

        androidLibraryProject.buildscript {
            repositories {
                mavenCentral()
            }
            dependencies {
                classpath ANDROID_PLUGIN_PATH
            }
        }

        androidLibraryProject.apply plugin: 'android-library'

        androidLibraryProject.repositories {
            mavenCentral()
        }

        androidLibraryProject
    }
}
