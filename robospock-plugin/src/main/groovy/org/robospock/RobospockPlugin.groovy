package org.robospock

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.testing.Test

class RobospockPlugin implements Plugin<Project> {



    void apply(Project project) {
        new RobospockAction().execute(project)
    }




}
