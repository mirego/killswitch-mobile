plugins {
    id("com.android.library") version Versions.ANDROID_GRADLE_PLUGIN apply false
    id("com.android.application") version Versions.ANDROID_GRADLE_PLUGIN apply false

    kotlin("android") version Versions.KOTLIN apply false
    kotlin("multiplatform") version Versions.KOTLIN apply false

    id("org.jlleitschuh.gradle.ktlint") version Versions.KTLINT apply false

    id("mirego.release") version "2.0"
    id("mirego.publish") version "1.0"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

release {
    checkTasks = listOf(
        ":killswitch:check"
    )
    buildTasks = listOf(
        ":killswitch:publish"
    )
    updateVersionPart = 2
}

tasks {
    val writeDevVersion by registering(WriteProperties::class) {
        outputFile = file("${rootDir}/gradle.properties")
        properties(java.util.Properties().apply { load(outputFile.reader()) }.mapKeys { it.key.toString() })
        val gitCommits = "git rev-list --count HEAD".runCommand(workingDir = rootDir)
        val originalVersion = project.version.toString().replace("-dev\\w+".toRegex(), "")
        property("version", "$originalVersion-dev$gitCommits")
    }
}
