package com.andyha.coreextension.localehelper

import android.app.Activity
import android.content.res.Configuration
import java.util.*

interface LocaleManager{
    fun isLanguageChangeable(): Boolean
    fun getLocaleFromLanguage(lang: String): Locale
    fun setAppLanguage(activity: Activity, language: String): Configuration?
    fun checkAndUpdateLanguageToServer(activity: Activity, lang: String?)
    fun getDefaultLocaleForNewUser(activity: Activity): Locale
}