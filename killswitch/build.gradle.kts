plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization") version Versions.KOTLIN
    id("org.jlleitschuh.gradle.ktlint")
    id("mirego.publish")
}

group = "com.mirego.killswitch-mobile"

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "killswitch"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:${Versions.KTOR}")
                implementation("io.ktor:ktor-client-content-negotiation:${Versions.KTOR}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.KTOR}")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

                implementation("co.touchlab:kermit:1.2.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:${Versions.KTOR}")
                implementation("androidx.annotation:annotation:1.5.0")
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:${Versions.KTOR}")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
}

android {
    namespace = "com.mirego.killswitch"
    compileSdk = 33
    defaultConfig {
        minSdk = 14
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                artifactId = "killswitch"
            }
        }
    }
}
