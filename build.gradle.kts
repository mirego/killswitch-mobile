plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.native.cocoapods) apply false

    alias(libs.plugins.ktlint) apply false

    alias(libs.plugins.mirego.publish)
    alias(libs.plugins.mirego.release)
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
