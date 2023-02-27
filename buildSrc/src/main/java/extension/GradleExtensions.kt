package extension

import org.gradle.api.artifacts.dsl.DependencyHandler
import projectConfig.AndroidConfig


fun DependencyHandler.implementations(dependencies: Array<String>) {
    for (dependency in dependencies) {
        add("implementation", dependency)
    }
}

fun DependencyHandler.debugImplementations(dependencies: Array<String>) {
    for (dependency in dependencies) {
        add("debugImplementation", dependency)
    }
}

fun DependencyHandler.apis(dependencies: Array<String>) {
    for (dependency in dependencies) {
        add("api", dependency)
    }
}

fun DependencyHandler.testImplementations(dependencies: Array<String>) {
    for (dependency in dependencies) {
        add("testImplementation", dependency)
    }
}

fun DependencyHandler.androidTestImplementations(dependencies: Array<String>) {
    for (dependency in dependencies) {
        add("androidTestImplementation", dependency)
    }
}

fun DependencyHandler.kapts(dependencies: Array<String>) {
    for (dependency in dependencies) {
        add("kapt", dependency)
    }
}

fun getVersionName(): String {
    val major = String.format("%02d", AndroidConfig.major)
    val minor = String.format("%02d", AndroidConfig.minor)
    val patch = String.format("%02d", AndroidConfig.patch)
    val build = String.format("%02d", AndroidConfig.build)
    return "$major$minor$patch$build"
}

fun getVersionCode(): Int {
    return getVersionName().toInt()
}