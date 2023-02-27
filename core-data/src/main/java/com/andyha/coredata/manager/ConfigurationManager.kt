package com.andyha.coredata.manager

import android.content.res.Configuration
import kotlinx.coroutines.flow.SharedFlow

interface ConfigurationManager {
    val currentLanguageTag: SharedFlow<String>
    val currentLocale: SharedFlow<String>
    val currentOrientation: SharedFlow<Int>
    val currentFontScale: SharedFlow<Float>

    fun onConfigurationChanged(newConfig: Configuration)
}