plugins {
    id("com.android.library") version "7.3.1" apply false
    kotlin("multiplatform") version Versions.KOTLIN apply false
    id("org.jlleitschuh.gradle.ktlint") version Versions.KTLINT apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
