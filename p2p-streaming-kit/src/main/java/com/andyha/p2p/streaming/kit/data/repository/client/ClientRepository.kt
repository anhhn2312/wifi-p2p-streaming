package com.andyha.p2p.streaming.kit.data.repository.client

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow


interface ClientRepository {
    suspend fun sendSelfIpAddress(serverIpAdress: String)
    suspend fun receiveStreaming(): Flow<Bitmap>
}