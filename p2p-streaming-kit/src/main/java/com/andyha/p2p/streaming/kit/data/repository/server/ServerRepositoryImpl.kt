package com.andyha.p2p.streaming.kit.data.repository.server

import android.content.Context
import android.graphics.Bitmap
import com.andyha.p2p.streaming.kit.data.repository.socket.clientInfo.ClientInfoSocket
import com.andyha.p2p.streaming.kit.data.repository.socket.clientInfo.UdpClientInfoSocket
import com.andyha.p2p.streaming.kit.data.repository.socket.streaming.StreamingSocket
import com.andyha.p2p.streaming.kit.data.repository.socket.streaming.UdpStreamingSocket
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ServerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ServerRepository {

    private val streamingSocket: StreamingSocket = UdpStreamingSocket(context)
    private val clientInfoSocket: ClientInfoSocket = UdpClientInfoSocket(context)

    override suspend fun sendBitmap(ipAddress: String, bitmap: Bitmap) {
        return streamingSocket.sendBitmap(ipAddress, bitmap)
    }

    override suspend fun receiveSubscribers(localServerAddress: String): Flow<String> {
        return clientInfoSocket.receiveSubscribers(localServerAddress)
    }
}