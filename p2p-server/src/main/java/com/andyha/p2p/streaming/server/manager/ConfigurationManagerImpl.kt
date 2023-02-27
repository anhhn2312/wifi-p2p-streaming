package com.andyha.p2p.streaming.server.manager

import android.content.Context
import android.content.res.Configuration
import com.andyha.coredata.manager.ConfigurationManager
import com.andyha.coredata.storage.preference.AppSharedPreference
import com.andyha.coredata.storage.preference.deviceLocale
import com.andyha.coredata.storage.preference.languageTag
import com.andyha.coreextension.getValueOrNull
import com.andyha.coreextension.localehelper.currentLocale
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject


class ConfigurationManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: AppSharedPreference
) : ConfigurationManager {

    init {
        // setup for api request headers
        prefs.deviceLocale =
            if (prefs.deviceLocale?.isNotEmpty() == true) prefs.deviceLocale
            else context.currentLocale.toString()
    }

    private val _currentLanguageTag = MutableSharedFlow<String>(replay = 1)
    override val currentLanguageTag = _currentLanguageTag.asSharedFlow()

    private val _currentLocale = MutableSharedFlow<String>(replay = 1)
    override val currentLocale = _currentLocale.asSharedFlow()

    private val _currentOrientation = MutableSharedFlow<Int>(replay = 1)
    override val currentOrientation = _currentOrientation.asSharedFlow()

    private val _currentFontScale = MutableSharedFlow<Float>(replay = 1)
    override val currentFontScale = _currentFontScale.asSharedFlow()


    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.orientation != currentOrientation.getValueOrNull()) {
            _currentOrientation.tryEmit(newConfig.orientation)
        }
        val newLocale = newConfig.currentLocale.toString()

        if (newLocale != currentLocale.getValueOrNull() || newLocale != prefs.deviceLocale) {
            _currentLocale.tryEmit(newLocale)
            prefs.deviceLocale = newLocale
        }

        val newLanguage = newConfig.currentLocale.toString()

        if (newLanguage != currentLanguageTag.getValueOrNull() || newLanguage != prefs.languageTag) {
            _currentLanguageTag.tryEmit(newLanguage)
            prefs.languageTag = newLanguage
        }

        if (newConfig.fontScale != currentFontScale.getValueOrNull()) {
            _currentFontScale.tryEmit(newConfig.fontScale)
        }
    }
}