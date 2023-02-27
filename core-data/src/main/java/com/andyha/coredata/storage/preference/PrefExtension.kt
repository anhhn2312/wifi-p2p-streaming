package com.andyha.coredata.storage.preference


inline var AppSharedPreference.token: String
    get() = this.getString(PreferenceKeys.TOKEN, "") ?: ""
    set(value) = this.set(PreferenceKeys.TOKEN, value)

inline var AppSharedPreference.refreshToken: String
    get() = this.getString(PreferenceKeys.REFRESH_TOKEN, "") ?: ""
    set(value) = this.set(PreferenceKeys.REFRESH_TOKEN, value)

inline var AppSharedPreference.idToken: String
    get() = this.getString(PreferenceKeys.ID_TOKEN, "") ?: ""
    set(value) = this.set(PreferenceKeys.ID_TOKEN, value)

inline var AppSharedPreference.fcmToken: String
    get() = this.getString(PreferenceKeys.FCM_TOKEN) ?: ""
    set(value) = this.set(PreferenceKeys.FCM_TOKEN, value)

inline var AppSharedPreference.imei: String?
    get() = this.getString(PreferenceKeys.IMEI)
    set(value) = this.set(PreferenceKeys.IMEI, value)

/**
 * Is locale user chosen language in setting for device
 */
inline var AppSharedPreference.deviceLocale: String?
    get() = this.getString(PreferenceKeys.DEVICE_LOCALE_SETTING_OF_DEVICE)
    set(value) = this.set(PreferenceKeys.DEVICE_LOCALE_SETTING_OF_DEVICE, value)

inline var AppSharedPreference.currentUsername: String?
    get() = this.getString(PreferenceKeys.LOGGED_IN_USER_EMAIL)
    set(value) = this.set(PreferenceKeys.LOGGED_IN_USER_EMAIL, value)

inline var AppSharedPreference.currentUserEmail: String?
    get() = this.getString(PreferenceKeys.LOGGED_IN_USER_NAME)
    set(value) = this.set(PreferenceKeys.LOGGED_IN_USER_NAME, value)

inline var AppSharedPreference.languageTag: String
    get() = this.getString(PreferenceKeys.LANGUAGE) ?: ""
    set(value) = this.set(PreferenceKeys.LANGUAGE, value)

//---Extension functions---

fun AppSharedPreference.didShowRationale(permission: String): Boolean {
    return this.getBoolean(PreferenceKeys.PERMISSION_RATIONALE + permission)
}

fun AppSharedPreference.setShowRationale(permission: String, didShow: Boolean) {
    this.putBoolean(PreferenceKeys.PERMISSION_RATIONALE + permission, didShow)
}