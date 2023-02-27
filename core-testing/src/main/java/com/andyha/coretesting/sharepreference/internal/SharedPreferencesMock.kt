package com.andyha.coretesting.sharepreference.internal

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import java.util.*


internal open class SharedPreferencesMock : SharedPreferences {
    private val preferencesMap: MutableMap<String?, Any> = HashMap()
    private val listeners: MutableSet<OnSharedPreferenceChangeListener> = HashSet()
    override fun getAll(): Map<String?, *> {
        return HashMap(preferencesMap)
    }

    override fun getString(key: String, defValue: String?): String? {
        val string = preferencesMap[key] as String?
        return string ?: defValue
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        val stringSet = preferencesMap[key] as Set<String>?
        return stringSet ?: defValues
    }

    override fun getInt(key: String, defValue: Int): Int {
        val integer = preferencesMap[key] as Int?
        return integer ?: defValue
    }

    override fun getLong(key: String, defValue: Long): Long {
        val longValue = preferencesMap[key] as Long?
        return longValue ?: defValue
    }

    override fun getFloat(key: String, defValue: Float): Float {
        val floatValue = preferencesMap[key] as Float?
        return floatValue ?: defValue
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        val booleanValue = preferencesMap[key] as Boolean?
        return booleanValue ?: defValue
    }

    override fun contains(key: String): Boolean {
        return preferencesMap.containsKey(key)
    }

    override fun edit(): SharedPreferences.Editor {
        return EditorImpl()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        listeners.add(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        listeners.remove(listener)
    }

    open inner class EditorImpl : SharedPreferences.Editor {
        private val newValuesMap: MutableMap<String, Any?> = HashMap()
        private var shouldClear = false
        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            newValuesMap[key] = value
            return this
        }

        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor {
            newValuesMap[key] = if (values != null) HashSet(values) else null
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            newValuesMap[key] = value
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            newValuesMap[key] = value
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            newValuesMap[key] = value
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            newValuesMap[key] = value
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            // 'this' is marker for remove operation
            newValuesMap[key] = this
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            shouldClear = true
            return this
        }

        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun apply() {
            clearIfNeeded()
            val changedKeys = applyNewValues()
            notifyListeners(changedKeys)
        }

        private fun clearIfNeeded() {
            if (shouldClear) {
                shouldClear = false
                preferencesMap.clear()
            }
        }

        /** @return changed keys list
         */
        private fun applyNewValues(): MutableList<String> {
            val changedKeys: MutableList<String> = ArrayList()
            for ((key, value1) in newValuesMap) {
                val value = value1!!
                val isSomethingChanged: Boolean
                isSomethingChanged = if (isRemoveValue(value)) {
                    removeIfNeeded(key)
                } else {
                    putValueIfNeeded(key, value)
                }
                if (isSomethingChanged) {
                    changedKeys.add(key)
                }
            }
            newValuesMap.clear()
            return changedKeys
        }

        private fun isRemoveValue(value: Any?): Boolean {
            // 'this' is marker for remove operation
            return value === this || value == null
        }

        /** @return true if element was removed
         */
        private fun removeIfNeeded(key: String?): Boolean {
            return if (preferencesMap.containsKey(key)) {
                preferencesMap.remove(key)
                true
            } else {
                false
            }
        }

        /** @return true if element was changed
         */
        private fun putValueIfNeeded(key: String?, value: Any): Boolean {
            if (preferencesMap.containsKey(key)) {
                val oldValue = preferencesMap[key]
                if (value == oldValue) {
                    return false
                }
            }
            preferencesMap[key] = value
            return true
        }

        private fun notifyListeners(changedKeys: MutableList<String>) {
            for (i in changedKeys.indices.reversed()) {
                for (listener in listeners) {
                    listener.onSharedPreferenceChanged(this@SharedPreferencesMock, changedKeys[i])
                }
            }
            changedKeys.clear()
        }
    }
}