pluginManagement {
    resolutionStrategy {
        repositories {
            google()
            gradlePluginPortal()
            mavenCentral()
            maven("https://s3.amazonaws.com/mirego-maven/public")
        }

        eachPlugin {
            if (requested.id.namespace == "mirego") {
                useModule("mirego:${requested.id.name}-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://s3.amazonaws.com/mirego-maven/public")
        mavenLocal()
    }
}

rootProject.name = "killswitch-mobile"
include(":killswitch", ":sample-android")
