plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}

version = "0.1.0"

val moduleId = "oplus_auto_dc"
val moduleName = "OPlus Auto DC"
val moduleVersionName = "v${project.version}"
val moduleVersionCode = providers.gradleProperty("module.versionCode").map(String::toInt).get()
val moduleAuthor = "yangFenTuoZi"
val moduleDescription = "daemon 等待启动；屏幕状态未知；调光：未知；刷新率：未知"
val updateZipName = "OplusAutoDC-v${project.version}.zip"
val updateRawBaseUrl = "https://raw.githubusercontent.com/yangFenTuoZi/OPlus-AutoDC-Module/main/update"
val updateJsonUrl = "$updateRawBaseUrl/update.json"
val updateZipUrl = "$updateRawBaseUrl/$updateZipName"
val updateChangelogUrl = "$updateRawBaseUrl/changelog.md"

val magiskWorkDir = layout.buildDirectory.dir("magiskModule")
val generatedMagiskDir = layout.buildDirectory.dir("generated/magisk")
val updateDir = layout.projectDirectory.dir("update")

val generateMagiskModuleProp = tasks.register("generateMagiskModuleProp") {
    group = "distribution"
    description = "Generate module.prop for the Magisk module."

    val moduleProp = generatedMagiskDir.map { it.file("module.prop") }
    outputs.file(moduleProp)
    inputs.property("moduleId", moduleId)
    inputs.property("moduleName", moduleName)
    inputs.property("moduleVersionName", moduleVersionName)
    inputs.property("moduleVersionCode", moduleVersionCode)
    inputs.property("moduleAuthor", moduleAuthor)
    inputs.property("moduleDescription", moduleDescription)
    inputs.property("updateJsonUrl", updateJsonUrl)

    doLast {
        moduleProp.get().asFile.writeText(
            """
            id=$moduleId
            name=$moduleName
            version=$moduleVersionName
            versionCode=$moduleVersionCode
            author=$moduleAuthor
            description=$moduleDescription
            updateJson=$updateJsonUrl
            """.trimIndent() + "\n"
        )
    }
}

val generateMagiskUpdateInfo = tasks.register("generateMagiskUpdateInfo") {
    group = "distribution"
    description = "Generate Magisk update metadata in the repository update directory."

    val updateJson = updateDir.file("update.json")
    outputs.file(updateJson)
    inputs.property("moduleVersionName", moduleVersionName)
    inputs.property("moduleVersionCode", moduleVersionCode)
    inputs.property("updateZipUrl", updateZipUrl)
    inputs.property("updateChangelogUrl", updateChangelogUrl)

    doLast {
        updateJson.asFile.writeText(
            """
            {
              "version": "$moduleVersionName",
              "versionCode": $moduleVersionCode,
              "zipUrl": "$updateZipUrl",
              "changelog": "$updateChangelogUrl"
            }
            """.trimIndent() + "\n"
        )
    }
}

val prepareMagiskModule = tasks.register<Sync>("prepareMagiskModule") {
    group = "distribution"
    description = "Prepare the Magisk module directory."

    dependsOn(":app:assembleRelease", generateMagiskModuleProp)

    into(magiskWorkDir)
    from(layout.projectDirectory.dir("magisk")) {
        exclude("*.apk", "module.prop")
    }
    from(generatedMagiskDir) {
        include("module.prop")
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
    finalizedBy("syncMagiskModuleUpdate")
    archiveFileName.set(updateZipName)
    destinationDirectory.set(layout.projectDirectory.dir("out"))
    from(magiskWorkDir)
}

tasks.register<Copy>("syncMagiskModuleUpdate") {
    group = "distribution"
    description = "Copy the Magisk module zip and update metadata into the repository update directory."

    dependsOn("packageMagiskModule", generateMagiskUpdateInfo)
    from(layout.projectDirectory.dir("out")) {
        include(updateZipName)
    }
    into(updateDir)
}
