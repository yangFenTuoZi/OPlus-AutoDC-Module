import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
}

extensions.configure<LibraryExtension>("android") {
    namespace = "hiddenapi.stub"
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

    buildFeatures {
        aidl = true
    }
}

dependencies {
    implementation(libs.androidx.annotation)
}
