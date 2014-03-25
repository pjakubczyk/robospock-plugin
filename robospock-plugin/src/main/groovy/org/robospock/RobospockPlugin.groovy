package org.robospock

import org.gradle.api.Plugin
import org.gradle.api.Project

class RobospockPlugin implements Plugin<Project> {

    void apply(Project project) {
        new RobospockAction().execute(project)
    }

}
