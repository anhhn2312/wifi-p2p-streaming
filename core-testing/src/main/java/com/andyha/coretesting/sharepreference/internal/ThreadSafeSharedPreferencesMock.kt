package com.andyha.coretesting.sharepreference.internal

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener


internal class ThreadSafeSharedPreferencesMock : SharedPreferencesMock() {
    private val lock = Any()
    override fun getAll(): Map<String?, *> {
        synchronized(lock) { return super.getAll() }
    }

    override fun getString(key: String, defValue: String?): String? {
        synchronized(lock) { return super.getString(key, defValue) }
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        synchronized(lock) { return super.getStringSet(key, defValues) }
    }

    override fun getInt(key: String, defValue: Int): Int {
        synchronized(lock) { return super.getInt(key, defValue) }
    }

    override fun getLong(key: String, defValue: Long): Long {
        synchronized(lock) { return super.getLong(key, defValue) }
    }

    override fun getFloat(key: String, defValue: Float): Float {
        synchronized(lock) { return super.getFloat(key, defValue) }
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        synchronized(lock) { return super.getBoolean(key, defValue) }
    }

    override fun contains(key: String): Boolean {
        synchronized(lock) { return super.contains(key) }
    }

    override fun edit(): SharedPreferences.Editor {
        return ThreadSafeEditor()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        synchronized(lock) { super.registerOnSharedPreferenceChangeListener(listener) }
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        synchronized(lock) { super.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    inner class ThreadSafeEditor : EditorImpl() {
        private val editorLock = Any()
        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            synchronized(editorLock) {
                super.putString(key, value)
                return this
            }
        }

        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor {
            synchronized(editorLock) {
                super.putStringSet(key, values)
                return this
            }
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            synchronized(editorLock) {
                super.putInt(key, value)
                return this
            }
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            synchronized(editorLock) {
                super.putLong(key, value)
                return this
            }
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            synchronized(editorLock) {
                super.putFloat(key, value)
                return this
            }
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            synchronized(editorLock) {
                super.putBoolean(key, value)
                return this
            }
        }

        override fun remove(key: String): SharedPreferences.Editor {
            synchronized(editorLock) {
                super.remove(key)
                return this
            }
        }

        override fun clear(): SharedPreferences.Editor {
            synchronized(editorLock) {
                super.clear()
                return this
            }
        }

        override fun commit(): Boolean {
            synchronized(editorLock) { synchronized(lock) { return super.commit() } }
        }

        override fun apply() {
            synchronized(editorLock) { synchronized(lock) { super.apply() } }
        }
    }
}