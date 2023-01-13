pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://s3.amazonaws.com/mirego-maven/public")
        google()
        mavenCentral()
    }
}

rootProject.name = "killswitch-mobile"
include(":killswitch", ":sample-android")
