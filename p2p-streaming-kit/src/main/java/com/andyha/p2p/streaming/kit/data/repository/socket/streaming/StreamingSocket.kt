package com.andyha.p2p.streaming.kit.data.repository.socket.streaming

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow


interface StreamingSocket {
    suspend fun receiveStreaming(): Flow<Bitmap>
    suspend fun sendBitmap(ipAddress: String, bitmap: Bitmap)
}