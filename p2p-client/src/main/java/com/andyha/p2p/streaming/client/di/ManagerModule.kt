package com.andyha.p2p.streaming.client.di

import com.andyha.coredata.manager.ConfigurationManager
import com.andyha.coredata.manager.NetworkConnectionManager
import com.andyha.coredata.manager.SessionManager
import com.andyha.coreextension.localehelper.LocaleManager
import com.andyha.coreui.manager.ApiErrorHandler
import com.andyha.p2p.streaming.client.manager.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {

    @Binds
    @Singleton
    abstract fun bindConfigurationManager(configurationManagerImpl: ConfigurationManagerImpl): ConfigurationManager

    @Binds
    @Singleton
    abstract fun bindNetworkManager(networkManagerImpl: NetworkConnectionManagerImpl): NetworkConnectionManager

    @Binds
    @Singleton
    abstract fun bindAppErrorManager(apiErrorManagerImpl: StubApiErrorHandler): ApiErrorHandler

    @Binds
    @Singleton
    abstract fun bindLocaleManager(localeManager: LocaleManagerImpl): LocaleManager

    @Binds
    @Singleton
    abstract fun bindSessionManager(sessionManager: StubSessionManager): SessionManager
}