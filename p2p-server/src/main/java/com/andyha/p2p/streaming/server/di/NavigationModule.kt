package com.andyha.p2p.streaming.server.di

import com.andyha.coreui.base.navigation.AppNavigator
import com.andyha.p2p.streaming.server.navigation.StubAppNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped


@InstallIn(ActivityComponent::class)
@Module
abstract class NavigationModule {

    @Binds
    @ActivityScoped
    abstract fun provideAppNavigator(impl: StubAppNavigator): AppNavigator
}