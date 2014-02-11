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
    static final LIB_PROJECT_NAME_2 = "sample_name2"
    static final SAMPLE_MAVEN_DEP = "com.jakewharton:butterknife:4.0.1"
    static final SAMPLE_MAVEN_DEP_2 = "com.fasterxml.jackson.core:jackson-core:2.3.0"
    static final SAMPLE_MAVEN_DEP_3 = "com.fasterxml.jackson.core:jackson-databind:2.3.0"

    static final ANDROID_PLUGIN_PATH = 'com.android.tools.build:gradle:0.8.+'

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
        def dependencies = robospockAction.findCompileDependencies(androidProject)

        then:
        dependencies.size() == 2
    }

    def "check number of maven dependencies"() {
        setup: "add dependencies to android project"
        androidProject.dependencies {
            compile SAMPLE_MAVEN_DEP
        }

        when: "extract maven dependencies"
        def maven = robospockAction.findMavenDependencies(androidProject)

        then:
        maven.size() == 1
    }

    def "check number of project dependencies"() {
        setup: "create library project"
        createAndroidLibraryProject(rootProject, LIB_PROJECT_NAME)

        and: "add dependencies to android project"
        androidProject.dependencies {
            compile rootProject.project(LIB_PROJECT_NAME)
        }

        when: "extract library dependencies"
        def libraries = robospockAction.findLibraryDependencies(androidProject)

        then:
        libraries.size() == 1
    }

    def "check number of maven dependencies in chained library relation"() {
        setup: "create library project"
        def libraryProject = createAndroidLibraryProject(rootProject, LIB_PROJECT_NAME)

        and: "add dependencies to library project"
        libraryProject.dependencies {
            compile SAMPLE_MAVEN_DEP
        }

        and: "create mid library project"
        def midLibraryProject = createAndroidLibraryProject(rootProject, LIB_PROJECT_NAME_2)

        and: "add dependencies to mid library"
        midLibraryProject.dependencies {
            compile SAMPLE_MAVEN_DEP_2
            compile SAMPLE_MAVEN_DEP_3
            compile rootProject.project(LIB_PROJECT_NAME)
        }

        and: "add dependencies to android project"
        androidProject.dependencies {
            compile rootProject.project(LIB_PROJECT_NAME_2)
        }

        when: "extract library dependencies"
        def libraries = robospockAction.collectMavenDependencies(robospockAction.getSubprojects(androidProject))

        then:
        libraries.size() == 3
    }

    def "check number of library dependencies in chained library relation"() {
        setup: "create library project"
        createAndroidLibraryProject(rootProject, LIB_PROJECT_NAME)

        and: "create mid library project"
        def midLibraryProject = createAndroidLibraryProject(rootProject, LIB_PROJECT_NAME_2)

        and: "add dependencies to mid library"
        midLibraryProject.dependencies {
            compile rootProject.project(LIB_PROJECT_NAME)
        }

        and: "add dependencies to android project"
        androidProject.dependencies {
            compile rootProject.project(LIB_PROJECT_NAME_2)
        }

        when: "extract library dependencies"
        def subprojects = robospockAction.getSubprojects(androidProject)

        then:
        subprojects.size() == 2
    }

    def "check number of library dependencies"() {
        setup: "create library project"
        createAndroidLibraryProject(rootProject, LIB_PROJECT_NAME)

        and: "add dependencies to android project"
        androidProject.dependencies {
            compile rootProject.project(LIB_PROJECT_NAME)
        }

        when: "extract library dependencies"
        def subprojects = robospockAction.getSubprojects(androidProject)

        then:
        subprojects.size() == 1
    }

    def "check number of library dependencies in zero related project"() {
        when: "extract library dependencies"
        def subprojects = robospockAction.getSubprojects(androidProject)

        then:
        subprojects.size() == 0
    }

    def "check if library project can be found"() {
        setup: "create library project"
        createAndroidLibraryProject(rootProject, LIB_PROJECT_NAME)

        when: "find projects"
        def result = robospockAction.findAndroidProjects(LIB_PROJECT_NAME, rootProject)

        then:
        result.size() == 1
        result.first().plugins.hasPlugin('android-library')
    }

    def "should find android plugin"() {
        when:
        def plugin = robospockAction.getAndroidPlugin(androidProject)

        then:
        plugin
    }

    def "should find android library plugin"() {
        setup: "create library project"
        def libraryProject = createAndroidLibraryProject(rootProject, LIB_PROJECT_NAME)

        when:
        def plugin = robospockAction.getAndroidPlugin(libraryProject)

        then:
        plugin
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
