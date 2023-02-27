package com.andyha.p2p.streaming.kit.data.repository.server

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow


interface ServerRepository {
    suspend fun sendBitmap(ipAddress: String, bitmap: Bitmap)
    suspend fun receiveSubscribers(localServerAddress: String): Flow<String>
}