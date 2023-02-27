import extension.implementations
import extension.kapts
import projectDependencies.Modules
import projectDependencies.ProjectDependencies

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = projectConfig.AndroidConfig.compileSdkVersion
    buildToolsVersion = projectConfig.AndroidConfig.buildToolVersion

    defaultConfig {
        minSdk = projectConfig.AndroidConfig.minSdkVersion
        targetSdk = projectConfig.AndroidConfig.targetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    dexOptions {
        jumboMode = true
        javaMaxHeapSize = "4g"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(project(path = Modules.CORE_RESOURCE))
    implementation(project(path = Modules.CORE_EXTENSION))
    implementation(project(path = Modules.CORE_UTILS))
    implementation(project(path = Modules.CORE_DATA))
    
    implementations(ProjectDependencies.di)
    implementations(ProjectDependencies.database)
    implementations(ProjectDependencies.extrasLibs)
    implementations(ProjectDependencies.networking)
    implementations(ProjectDependencies.playServices)
    implementations(ProjectDependencies.logging)


    kapts(ProjectDependencies.processing)
}