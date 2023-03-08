package projectDependencies

import projectDependencies.DependencyVersion.ACTIVITY_KTX_VERSION
import projectDependencies.DependencyVersion.ANNOTATION_VERSION
import projectDependencies.DependencyVersion.APPCOMPAT_VERSION
import projectDependencies.DependencyVersion.BROWSER_VERSION
import projectDependencies.DependencyVersion.CAMERAX_VERSION
import projectDependencies.DependencyVersion.CARDVIEW_VERSION
import projectDependencies.DependencyVersion.CONCURRENT_FUTURES_KTX_VERSION
import projectDependencies.DependencyVersion.CONSTRAINT_LAYOUT_VERSION
import projectDependencies.DependencyVersion.COORDINATOR_LAYOUT_VERSION
import projectDependencies.DependencyVersion.CORE_KTX_VERSION
import projectDependencies.DependencyVersion.CORE_TESTING_VERSION
import projectDependencies.DependencyVersion.CORE_VERSION
import projectDependencies.DependencyVersion.COROUTINES_VERSION
import projectDependencies.DependencyVersion.ESPRESSO_VERSION
import projectDependencies.DependencyVersion.FINGERPRINT_VERSION
import projectDependencies.DependencyVersion.FIREBASE_ANALYTICS_VERSION
import projectDependencies.DependencyVersion.FIREBASE_CRASHLYTICS_VERSION
import projectDependencies.DependencyVersion.FRAGMENT_KTX_VERSION
import projectDependencies.DependencyVersion.GLIDE_VERSION
import projectDependencies.DependencyVersion.GSON_VERSION
import projectDependencies.DependencyVersion.HILT_COMPILER_VERSION
import projectDependencies.DependencyVersion.HILT_VERSION
import projectDependencies.DependencyVersion.HILT_WORKER_VERSION
import projectDependencies.DependencyVersion.JUNIT_VERSION
import projectDependencies.DependencyVersion.KEYBOARD_LISTENER_VERSION
import projectDependencies.DependencyVersion.KOTEST_VERSION
import projectDependencies.DependencyVersion.KOTLIN_VERSION
import projectDependencies.DependencyVersion.LEAK_CANARY_VERSION
import projectDependencies.DependencyVersion.LEGACY_SUPPORT_VERSION
import projectDependencies.DependencyVersion.LIFECYCLE_EXTENSIONS_VERSION
import projectDependencies.DependencyVersion.LIFECYCLE_KTX_VERSION
import projectDependencies.DependencyVersion.MATERIAL_VERSION
import projectDependencies.DependencyVersion.MOCKITO_KOTLIN_VERSION
import projectDependencies.DependencyVersion.MOCKITO_VERSION
import projectDependencies.DependencyVersion.MOCKK_VERSION
import projectDependencies.DependencyVersion.NAVIGATION_VERSION
import projectDependencies.DependencyVersion.OKHTTP
import projectDependencies.DependencyVersion.OVERSCROLL_LIBS_VERSION
import projectDependencies.DependencyVersion.PAGING_VERSION
import projectDependencies.DependencyVersion.PLAY_CORE_VERSION
import projectDependencies.DependencyVersion.PLAY_LOCATION_VERSION
import projectDependencies.DependencyVersion.POWER_MOCK_VERSION
import projectDependencies.DependencyVersion.REACTIVESTREAMS_KTX_VERSION
import projectDependencies.DependencyVersion.RECYCLER_VIEW_VERSION
import projectDependencies.DependencyVersion.REFRESH_LAYOUT_VERSION
import projectDependencies.DependencyVersion.RETROFIT2_VERSION
import projectDependencies.DependencyVersion.ROOM_VERSION
import projectDependencies.DependencyVersion.RX_ANDROID_VERSION
import projectDependencies.DependencyVersion.RX_BINDING_VERSION
import projectDependencies.DependencyVersion.RX_JAVA_VERSION
import projectDependencies.DependencyVersion.RX_KOTLIN_VERSION
import projectDependencies.DependencyVersion.RX_LIFECYCLE_VERSION
import projectDependencies.DependencyVersion.SHIMMER_VERSION
import projectDependencies.DependencyVersion.SIMPLE_CROP_VIEW_VERSION
import projectDependencies.DependencyVersion.SPLASH_SCREEN_VERSION
import projectDependencies.DependencyVersion.TEST_RULES_VERSION
import projectDependencies.DependencyVersion.TEST_RUNNER_VERSION
import projectDependencies.DependencyVersion.TIMBER_VERSION
import projectDependencies.DependencyVersion.VIEWMODEL_KTX_VERSION
import projectDependencies.DependencyVersion.VIEWPAGER2_VERSION
import projectDependencies.DependencyVersion.WINDOW_VERSION
import projectDependencies.DependencyVersion.WORK_MANAGER_VERSION


object ProjectDependencies {
    val coreUI = arrayOf(
        "androidx.appcompat:appcompat:$APPCOMPAT_VERSION",
        "androidx.activity:activity-ktx:$ACTIVITY_KTX_VERSION",
        "androidx.fragment:fragment:$FRAGMENT_KTX_VERSION",
        "androidx.fragment:fragment-ktx:$FRAGMENT_KTX_VERSION",
        "com.google.android.material:material:$MATERIAL_VERSION",
        "androidx.cardview:cardview:$CARDVIEW_VERSION",
        "androidx.recyclerview:recyclerview:$RECYCLER_VIEW_VERSION",
        "androidx.core:core:$CORE_VERSION",
        "androidx.core:core-ktx:$CORE_KTX_VERSION",
        "androidx.swiperefreshlayout:swiperefreshlayout:$REFRESH_LAYOUT_VERSION",
        "androidx.constraintlayout:constraintlayout:$CONSTRAINT_LAYOUT_VERSION",
        "androidx.constraintlayout:constraintlayout-solver:$CONSTRAINT_LAYOUT_VERSION",
        "androidx.viewpager2:viewpager2:$VIEWPAGER2_VERSION",
        "androidx.browser:browser:$BROWSER_VERSION",
        "androidx.core:core-splashscreen:$SPLASH_SCREEN_VERSION",
        "androidx.window:window:$WINDOW_VERSION"
    )

    val supportLibs = arrayOf(
        "androidx.annotation:annotation:$ANNOTATION_VERSION",
        "androidx.biometric:biometric:$FINGERPRINT_VERSION",
        "androidx.legacy:legacy-support-v4:$LEGACY_SUPPORT_VERSION",
        "androidx.concurrent:concurrent-futures-ktx:$CONCURRENT_FUTURES_KTX_VERSION",
    )

    val extrasLibs = arrayOf(
        "com.github.ravindu1024:android-keyboardlistener:$KEYBOARD_LISTENER_VERSION",
        "com.facebook.shimmer:shimmer:$SHIMMER_VERSION",
        "io.github.everythingme:overscroll-decor-android:$OVERSCROLL_LIBS_VERSION",
        "com.github.bumptech.glide:glide:$GLIDE_VERSION",
    )

    val networking = arrayOf(
        "com.squareup.okhttp3:okhttp:$OKHTTP",
        "com.squareup.okhttp3:logging-interceptor:$OKHTTP",
        "com.squareup.retrofit2:retrofit:$RETROFIT2_VERSION",
        "com.squareup.retrofit2:converter-gson:$RETROFIT2_VERSION",
        "com.google.code.gson:gson:$GSON_VERSION",
    )

    val rx = arrayOf(
        "io.reactivex.rxjava3:rxjava:$RX_JAVA_VERSION",
        "io.reactivex.rxjava3:rxkotlin:$RX_KOTLIN_VERSION",
        "io.reactivex.rxjava3:rxandroid:$RX_ANDROID_VERSION",
        "com.jakewharton.rxbinding4:rxbinding:$RX_BINDING_VERSION",
    )

    val di = arrayOf(
        "com.google.dagger:hilt-android:$HILT_VERSION",
        "androidx.hilt:hilt-work:$HILT_WORKER_VERSION"
    )

    val lifecycle = arrayOf(
        "androidx.lifecycle:lifecycle-extensions:$LIFECYCLE_EXTENSIONS_VERSION",
        "androidx.lifecycle:lifecycle-livedata:$LIFECYCLE_KTX_VERSION",
        "androidx.lifecycle:lifecycle-runtime-ktx:$LIFECYCLE_KTX_VERSION",
        "androidx.lifecycle:lifecycle-reactivestreams:$LIFECYCLE_KTX_VERSION",
        "androidx.lifecycle:lifecycle-viewmodel-ktx:$VIEWMODEL_KTX_VERSION",
        "androidx.lifecycle:lifecycle-reactivestreams-ktx:$REACTIVESTREAMS_KTX_VERSION",
    )

    val paging = arrayOf(
        "androidx.paging:paging-common:$PAGING_VERSION",
        "androidx.paging:paging-runtime:$PAGING_VERSION",
    )

    val database = arrayOf(
        "androidx.room:room-runtime:$ROOM_VERSION",
        "androidx.room:room-ktx:$ROOM_VERSION",
    )

    val work = arrayOf(
        "androidx.work:work-runtime-ktx:$WORK_MANAGER_VERSION",
        "androidx.work:work-runtime:$WORK_MANAGER_VERSION"
    )

    val navigation = arrayOf(
        "androidx.navigation:navigation-ui-ktx:$NAVIGATION_VERSION",
        "androidx.navigation:navigation-fragment-ktx:$NAVIGATION_VERSION",
    )

    val playServices = arrayOf(
        "com.google.android.play:core:$PLAY_CORE_VERSION",
        "com.google.android.gms:play-services-location:$PLAY_LOCATION_VERSION",
    )

    val analytics = arrayOf(
        "com.google.firebase:firebase-crashlytics-ktx:$FIREBASE_CRASHLYTICS_VERSION",
        "com.google.firebase:firebase-analytics-ktx:$FIREBASE_ANALYTICS_VERSION",
    )

    val cameraX = arrayOf(
        "androidx.camera:camera-core:$CAMERAX_VERSION",
        "androidx.camera:camera-camera2:$CAMERAX_VERSION",
        "androidx.camera:camera-lifecycle:$CAMERAX_VERSION",
        "androidx.camera:camera-view:$CAMERAX_VERSION",
    )

    val processing = arrayOf(
        "com.google.dagger:hilt-android-compiler:$HILT_VERSION",
        "com.google.dagger:hilt-compiler:$HILT_COMPILER_VERSION",
        "com.github.bumptech.glide:compiler:$GLIDE_VERSION",
        "androidx.room:room-compiler:$ROOM_VERSION",
    )

    val logging = arrayOf(
        "com.jakewharton.timber:timber:$TIMBER_VERSION"
    )

    val unitTesting = arrayOf(
        "junit:junit:$JUNIT_VERSION",
        "androidx.arch.core:core-testing:$CORE_TESTING_VERSION",
        "org.jetbrains.kotlin:kotlin-test-junit:$KOTLIN_VERSION",
        "androidx.test:runner:$TEST_RUNNER_VERSION",
        "androidx.test:rules:$TEST_RULES_VERSION",
        "androidx.annotation:annotation:$ANNOTATION_VERSION         ",
        "org.mockito:mockito-core:$MOCKITO_VERSION",
        "org.mockito.kotlin:mockito-kotlin:$MOCKITO_KOTLIN_VERSION",
        "org.mockito:mockito-inline:$MOCKITO_VERSION",
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:$COROUTINES_VERSION",
        "io.mockk:mockk:$MOCKK_VERSION",
        "io.kotest:kotest-assertions-core:$KOTEST_VERSION",
        "org.powermock:powermock-module-junit4:$POWER_MOCK_VERSION"
    )

    val androidTesting = arrayOf(
        "com.google.dagger:hilt-android-testing:$HILT_VERSION",
        "org.mockito:mockito-android:$MOCKITO_VERSION",
        "androidx.test.espresso:espresso-core:$ESPRESSO_VERSION",
        "androidx.test.espresso:espresso-contrib:$ESPRESSO_VERSION",
        "androidx.test.espresso:espresso-intents:$ESPRESSO_VERSION",
    )

    val devDependencies = arrayOf(
        "com.squareup.leakcanary:leakcanary-android:$LEAK_CANARY_VERSION",
    )
}