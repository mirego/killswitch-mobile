import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.native.cocoapods)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Killswitch sample"
        homepage = "www.mirego.com"
        license = "BSD-3"
        version = "1.0"
        ios.deploymentTarget = "15.0"
        podfile = project.file("../ios/Podfile")
        framework {
            baseName = "common"
            @Suppress("OPT_IN_USAGE")
            transitiveExport = true
            export(project(":killswitch"))
        }
    }
    
    sourceSets {
        commonMain {
            dependencies {
                api(project(":killswitch"))
            }
        }
    }
}

android {
    namespace = "com.mirego.killswitch.sample"
    compileSdk = 36
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
