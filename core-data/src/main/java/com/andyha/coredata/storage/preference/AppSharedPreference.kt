package com.andyha.coredata.storage.preference

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class AppSharedPreference @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val preference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val editor = preference.edit()

    /**
     * @deprecated Use  getCurrentLimiterReplyClkSpeedStatus() method instead
     */
    fun <T> set(key: String, value: T? = null) {
        value?.let {
            when (it) {
                is String -> putString(key, it)
                is Boolean -> putBoolean(key, it)
                is Int -> putInt(key, it)
                is Float -> putFloat(key, it)
                is Long -> putLong(key, it)
                else -> {
                    throw IllegalArgumentException("the type $it is not support")
                }
            }
        } ?: clearValue(key)
    }

    fun getString(key: String, default: String? = null): String? =
        runCatching { preference.getString(key, default) }.getOrDefault(default)

    fun getInt(key: String, default: Int = 0): Int =
        runCatching { preference.getInt(key, default) }.getOrDefault(default)

    fun getFloat(key: String, default: Float = 0f): Float =
        runCatching { preference.getFloat(key, default) }.getOrDefault(default)

    fun getLong(key: String, default: Long = 0L): Long =
        runCatching { preference.getLong(key, default) }.getOrDefault(default)

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        runCatching { preference.getBoolean(key, default) }.getOrDefault(default)

    fun clearValue(key: String) = editor.remove(key).apply()
    fun clear() = editor.clear().apply()

    fun putBoolean(key: String, value: Boolean) = editor.putBoolean(key, value).commit()
    fun putInt(key: String, value: Int) = editor.putInt(key, value).commit()
    fun putLong(key: String, value: Long) = editor.putLong(key, value).commit()
    fun putFloat(key: String, value: Float) = editor.putFloat(key, value).commit()
    fun putString(key: String, value: String) = editor.putString(key, value).commit()

    private fun String.toBooleanOrNull(): Boolean? = when {
        this.equals("true", true) -> true
        this.equals("false", true) -> false
        else -> null
    }

    fun addChangeListener(listener: OnSharedPreferenceChangeListener) {
        preference.registerOnSharedPreferenceChangeListener(listener)
    }

    fun removeChangeListener(listener: OnSharedPreferenceChangeListener) {
        preference.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {

        // For singleton instantiation
        @Volatile
        var instance: AppSharedPreference? = null

        fun getInstance(
            context: Context,
        ): AppSharedPreference {
            if (instance == null) {
                synchronized(this) {
                    AppSharedPreference(context).also { instance = it }
                }
            }
            return instance!!
        }

        @JvmName("getInstance1")
        fun getInstance(): AppSharedPreference {
            return instance!!
        }
    }
}