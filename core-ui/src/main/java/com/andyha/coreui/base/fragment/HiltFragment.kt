package com.andyha.coreui.base.fragment

import androidx.fragment.app.Fragment
import com.andyha.coredata.manager.ConfigurationManager
import com.andyha.coredata.manager.NetworkConnectionManager
import com.andyha.coredata.storage.preference.AppSharedPreference
import com.andyha.coreui.base.navigation.AppNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
abstract class HiltFragment : Fragment() {
    @Inject
    lateinit var configurationManager: ConfigurationManager

    @Inject
    lateinit var appSharedPreference: AppSharedPreference

    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager

    @Inject
    lateinit var appNavigator: AppNavigator
}