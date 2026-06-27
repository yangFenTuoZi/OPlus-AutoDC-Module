plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}

version = "0.1.0"

val magiskWorkDir = layout.buildDirectory.dir("magiskModule")

val prepareMagiskModule = tasks.register<Sync>("prepareMagiskModule") {
    group = "distribution"
    description = "Prepare the Magisk module directory."

    dependsOn(":app:assembleRelease")

    into(magiskWorkDir)
    from(layout.projectDirectory.dir("magisk")) {
        exclude("*.apk")
    }
    from(layout.projectDirectory.dir("app/build/outputs/apk/release")) {
        include("*.apk")
        into("")
        rename { "daemon.apk" }
    }
}

tasks.register<Zip>("packageMagiskModule") {
    group = "distribution"
    description = "Build the flashable Magisk module zip."

    dependsOn(prepareMagiskModule)
    archiveFileName.set("OplusAutoDC-v${project.version}.zip")
    destinationDirectory.set(layout.projectDirectory.dir("out"))
    from(magiskWorkDir)
}
