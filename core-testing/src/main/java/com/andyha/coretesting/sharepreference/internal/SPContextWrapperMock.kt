package com.andyha.coretesting.sharepreference.internal

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import java.util.*


class SPContextWrapperMock(base: Context, private val factory: SharedPreferencesFactory) :
    ContextWrapper(base) {
    private val preferencesMap: MutableMap<String, SharedPreferences> = HashMap()
    override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
        return if (preferencesMap.containsKey(name)) {
            preferencesMap[name]!!
        } else {
            val sharedPreferences = factory.create()
            preferencesMap[name] = sharedPreferences
            sharedPreferences
        }
    }

    override fun deleteSharedPreferences(name: String): Boolean {
        preferencesMap.remove(name)
        return true
    }
}