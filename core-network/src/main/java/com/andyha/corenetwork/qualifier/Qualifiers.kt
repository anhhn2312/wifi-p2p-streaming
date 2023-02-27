package com.andyha.corenetwork.qualifier

import com.andyha.corenetwork.qualifier.Service.GENERAL
import javax.inject.Qualifier

enum class Service {
    GENERAL,
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ForRequestInterceptor(val service: Service = GENERAL)

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ForHostSelection(val service: Service = GENERAL)

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ForLogging

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ForTimeout