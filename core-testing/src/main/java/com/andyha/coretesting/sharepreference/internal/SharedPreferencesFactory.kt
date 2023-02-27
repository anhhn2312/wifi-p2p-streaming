package com.andyha.coretesting.sharepreference.internal

import android.content.SharedPreferences


class SharedPreferencesFactory(private val isThreadSafe: Boolean) {
    fun create(): SharedPreferences {
        return if (isThreadSafe) {
            ThreadSafeSharedPreferencesMock()
        } else {
            SharedPreferencesMock()
        }
    }
}