package com.andyha.camerakit.di

import com.andyha.camerakit.manager.CameraManager
import com.andyha.camerakit.manager.CameraManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped


@InstallIn(FragmentComponent::class)
@Module
abstract class CameraModule {

    @Binds
    @FragmentScoped
    abstract fun provideCameraManager(impl: CameraManagerImpl): CameraManager
}