package com.andyha.p2p.streaming.client.manager

import android.app.Activity
import android.content.res.Configuration
import com.andyha.coredata.manager.ConfigurationManager
import com.andyha.coreextension.localehelper.LocaleManager
import com.andyha.coreextension.localehelper.Locales
import com.andyha.coreextension.updateLanguageResource
import java.util.*
import javax.inject.Inject


class LocaleManagerImpl @Inject constructor(
    private val configurationManager: ConfigurationManager
) : LocaleManager {

    override fun isLanguageChangeable(): Boolean {
        return false
    }

    override fun getLocaleFromLanguage(lang: String): Locale {
        return when {
            lang.lowercase().contains("vi") -> Locales.Vietnamese
            else -> Locales.English
        }
    }

    override fun setAppLanguage(activity: Activity, language: String): Configuration? {
        val configuration = activity.updateLanguageResource(language)
        configuration?.let { configurationManager.onConfigurationChanged(it) }
        return configuration
    }

    override fun checkAndUpdateLanguageToServer(activity: Activity, lang: String?) {
        if (shouldUpdateLanguage(lang)) {
            val defaultLocale = getDefaultLocaleForNewUser(activity)
            // Todo: Update language setting to BE
            setAppLanguage(activity, defaultLocale.language)
        } else {
            lang?.let { setAppLanguage(activity, it) }
        }
    }

    override fun getDefaultLocaleForNewUser(activity: Activity): Locale {
        return Locales.Vietnamese
    }

    private fun shouldUpdateLanguage(lang: String?): Boolean {
        return lang == null
    }
}