plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.thatmarcel.apps.suresim"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.thatmarcel.apps.suresim"
        minSdk = 31
        targetSdk = 36
        versionCode = 2
        versionName = "0.0.2"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    compileOnly(libs.xposed.api)
}