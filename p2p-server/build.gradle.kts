import extension.*
import projectConfig.*
import projectDependencies.DependencyVersion.WORK_MANAGER_VERSION
import projectDependencies.Modules
import projectDependencies.ProjectDependencies
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = AndroidConfig.compileSdkVersion

    defaultConfig {
        applicationId = AndroidConfig.serverAppId
        minSdk = AndroidConfig.minSdkVersion
        targetSdk = AndroidConfig.targetSdkVersion
        versionCode = getVersionCode()
        versionName = getVersionName()

        buildToolsVersion = AndroidConfig.buildToolVersion

        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            val signingConfig = rootProject.getDebugSigningConfig()
            storeFile = file(signingConfig.storePath)
            storePassword = signingConfig.storePassword
            keyAlias = signingConfig.alias
            keyPassword = signingConfig.aliasPassword
        }
        create("release") {
            val signingConfig = rootProject.getReleaseSigningConfig()
            storeFile = file(signingConfig.storePath)
            storePassword = signingConfig.storePassword
            keyAlias = signingConfig.alias
            keyPassword = signingConfig.aliasPassword
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "_dev"
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }

        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        applicationVariants.all {
            outputs.all {
                (this as? BaseVariantOutputImpl)?.outputFileName = "P2P_Server_${versionName}_${getBuildTime()}.apk"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lint {
        abortOnError = false
        htmlReport = true
    }

    dexOptions {
        jumboMode = true
        javaMaxHeapSize = "4g"
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = false
            isIncludeAndroidResources = true
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    configurations {
        all { exclude(module = "javax.annotation") }
    }

    configurations.all {
        resolutionStrategy {
            exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-debug")
            exclude(group = "org.mockito", module = "mockito-inline")
            exclude(group = "org.mockito", module = "mockito-android")
            eachDependency {
                if (requested.group == "androidx.work") {
                    useVersion("$WORK_MANAGER_VERSION")
                }
            }
        }
    }
}

dependencies {
    implementation(project(path = Modules.CORE_UI))
    implementation(project(path = Modules.CORE_RESOURCE))
    implementation(project(path = Modules.CORE_EXTENSION))
    implementation(project(path = Modules.CORE_UTILS))
    implementation(project(path = Modules.CORE_DATA))
    implementation(project(path = Modules.P2P_STREAMING_KIT))
    implementation(project(path = Modules.P2P_SHARED_RESOURCES))
    implementation(project(path = Modules.CAMERA_KIT))
    implementation(project(path = Modules.WIFI_DIRECT_KIT))

    implementations(ProjectDependencies.coreUI)
    implementations(ProjectDependencies.extrasLibs)
    implementations(ProjectDependencies.supportLibs)
    implementations(ProjectDependencies.cameraX)
    implementations(ProjectDependencies.networking)
    implementations(ProjectDependencies.rx)
    implementations(ProjectDependencies.di)
    implementations(ProjectDependencies.playServices)
    implementations(ProjectDependencies.logging)
    implementations(ProjectDependencies.di)
    implementations(ProjectDependencies.database)
    implementations(ProjectDependencies.analytics)

    kapts(ProjectDependencies.processing)
}