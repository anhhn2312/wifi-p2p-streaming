package com.andyha.wifidirectkit.di

import com.andyha.wifidirectkit.manager.WifiDirectManager
import com.andyha.wifidirectkit.manager.WifiDirectManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {
    @Binds
    abstract fun bindWifiDirectManager(impl: WifiDirectManagerImpl): WifiDirectManager

}