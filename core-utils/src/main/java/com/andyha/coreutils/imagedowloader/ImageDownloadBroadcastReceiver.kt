package com.andyha.coreutils.imagedowloader

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber


class ImageDownloadBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            return
        }
        Timber.d("Download completed")
    }
}