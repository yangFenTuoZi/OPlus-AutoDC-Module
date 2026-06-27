import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
}

extensions.configure<ApplicationExtension>("android") {
    namespace = "yangfentuozi.oplusautodc"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "yangfentuozi.oplusautodc"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = rootProject.version.toString()
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
