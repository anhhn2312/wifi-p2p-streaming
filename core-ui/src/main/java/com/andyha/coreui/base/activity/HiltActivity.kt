package com.andyha.coreui.base.activity

import androidx.appcompat.app.AppCompatActivity
import com.andyha.coredata.manager.SessionManager
import com.andyha.coredata.storage.preference.AppSharedPreference
import com.andyha.coreextension.localehelper.LocaleManager
import com.andyha.coreui.base.navigation.AppNavigator
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
open class HiltActivity : AppCompatActivity() {
    @Inject
    lateinit var preference: AppSharedPreference

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var sessionManager: Lazy<SessionManager>

    @Inject
    lateinit var localeManager: Lazy<LocaleManager>
}