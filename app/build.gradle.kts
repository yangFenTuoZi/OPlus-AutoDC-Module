import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
}

fun String.asBuildConfigString(): String = "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""

val moduleId = "oplus_auto_dc"
val moduleName = "OPlus Auto DC"
val moduleVersionCode = providers.gradleProperty("module.versionCode").map(String::toInt).get()
val moduleAuthor = "yangFenTuoZi"
val updateJsonUrl = "https://raw.githubusercontent.com/yangFenTuoZi/OPlus-AutoDC-Module/main/update/update.json"

extensions.configure<ApplicationExtension>("android") {
    namespace = "yangfentuozi.oplusautodc"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "yangfentuozi.oplusautodc"
        minSdk = 31
        targetSdk = 37
        versionCode = moduleVersionCode
        versionName = rootProject.version.toString()
        buildConfigField("String", "MAGISK_MODULE_ID", moduleId.asBuildConfigString())
        buildConfigField("String", "MAGISK_MODULE_NAME", moduleName.asBuildConfigString())
        buildConfigField("String", "MAGISK_MODULE_VERSION", "v${rootProject.version}".asBuildConfigString())
        buildConfigField("int", "MAGISK_MODULE_VERSION_CODE", moduleVersionCode.toString())
        buildConfigField("String", "MAGISK_MODULE_AUTHOR", moduleAuthor.asBuildConfigString())
        buildConfigField("String", "MAGISK_UPDATE_JSON_URL", updateJsonUrl.asBuildConfigString())
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(project(":hiddenapi:compat"))
    compileOnly(project(":hiddenapi:stub"))
}
