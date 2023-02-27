package com.andyha.p2p.streaming.server.app

import android.app.Application
import com.andyha.coreutils.timber.AppTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class ServerApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(AppTree())
    }
}