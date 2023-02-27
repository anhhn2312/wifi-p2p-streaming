package com.andyha.corenetwork.qualifier

import java.util.concurrent.TimeUnit


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ConnectTimeout (val duration: Int, val unit: TimeUnit)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ReadTimeout (val duration: Int, val unit: TimeUnit)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class WriteTimeout (val duration: Int, val unit: TimeUnit)