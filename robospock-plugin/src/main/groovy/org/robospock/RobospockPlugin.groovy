package org.robospock

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.testing.Test

class RobospockPlugin implements Plugin<Project> {

    public static final String ROBOSPOCK_TASK_NAME = "robospock"


    void apply(Project project) {

        project.getPlugins().apply(JavaBasePlugin.class);

        Project androidProject

        def appPlugin

        project.getRootProject().getAllprojects().each {
            if (it.plugins.hasPlugin("android")) {
                androidProject = it
                appPlugin = it.plugins["android"]
            }
        }

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

}
