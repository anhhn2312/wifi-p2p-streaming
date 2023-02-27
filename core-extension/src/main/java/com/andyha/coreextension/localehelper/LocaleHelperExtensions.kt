package com.andyha.coreextension.localehelper

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*


val Context.config: Configuration
    get() = resources.configuration

val Context.currentLocale: Locale
    get() = config.currentLocale

val Configuration.currentLocale: Locale
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locales.get(0)
    } else {
        locale
    }

fun Configuration.setCurrentLocale(locale: Locale) {
    this.setLocale(locale)
}