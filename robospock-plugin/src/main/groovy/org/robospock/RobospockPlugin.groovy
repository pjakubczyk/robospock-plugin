package org.robospock

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.testing.Test

class RobospockPlugin implements Plugin<Project> {

    public static final String ROBOSPOCK_TASK_NAME = "robospock"


    void apply(Project project) {

        project.getPlugins().apply(JavaBasePlugin.class);

        Project androidProject

        project.getRootProject().getAllprojects().each {
            if (it.plugins.hasPlugin("android")) {
                androidProject = it
            }
        }

        project.dependencies {
            compile project.files(androidProject.buildDir.path + '/classes/debug')
            compile project.files(androidProject.buildDir.path + '/res/all/debug')
        }

        Test test = project.getTasks().create(ROBOSPOCK_TASK_NAME, Test.class);
        project.getTasks().getByName(JavaBasePlugin.CHECK_TASK_NAME).dependsOn(test);
        test.setDescription("Runs the unit tests using Robospock.")
        test.setGroup(JavaBasePlugin.VERIFICATION_GROUP)

        test.workingDir = project.getRootProject().projectDir

        test.dependsOn(androidProject.getTasks().findByName('assemble'))
    }

}
