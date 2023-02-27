package com.andyha.p2p.streaming.client.app

import android.app.Application
import com.andyha.coreutils.timber.AppTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class ClientApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(AppTree())
    }
}