import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
}

extensions.configure<LibraryExtension>("android") {
    namespace = "yangfentuozi.hiddenapi.compat"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 31
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(libs.androidx.annotation)
    compileOnly(project(":hiddenapi:stub"))
}
