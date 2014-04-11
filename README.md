robospock-plugin
================

Gradle plugin for RoboSpock allows you to integrate unit test support (robolectric/robospock) with your Android project.

Check out more on [http://robospock.org/]

Important note for build.gradle:

```sh
evaluationDependsOn(":android-sample")

apply plugin: 'groovy'

dependencies {
  compile "org.codehaus.groovy:groovy-all:1.8.6"
  compile 'org.robospock:robospock:0.4'
}


project.ext {
    robospock = ":android-sample" // project to test
}

apply plugin: 'robospock'
```

The *robospock* plugin **must** be applied after the *android* plugin. Gradle provides the *evaluationDependsOn* configuration to accomplish this.  See example above.


Changelog
=========

## 0.4.0

* Robospock plugin extends java source sets instead of overriding classpath
* Better integration with IntelliJ

## 0.3.1

* Enabling unit testing android libraries
* Gradle wrapper updated to 1.10 
* Android plugin updated to 0.8.+

## 0.3.0

* Maven dependencies from subprojects are linked to main project

## 0.2.2

* Changing project structure
* Added unit tests

## 0.2.1

* Adding support for maven dependencies and project dependencies
* Plugin can be place anywhere in script

## 0.0.1

* Initial release
* Plugin supports testing simple Android projects
