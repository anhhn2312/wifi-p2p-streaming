package com.andyha.p2p.streaming.kit.data.repository.client

import android.content.Context
import android.graphics.Bitmap
import com.andyha.p2p.streaming.kit.data.repository.socket.clientInfo.ClientInfoSocket
import com.andyha.p2p.streaming.kit.data.repository.socket.clientInfo.UdpClientInfoSocket
import com.andyha.p2p.streaming.kit.data.repository.socket.streaming.StreamingSocket
import com.andyha.p2p.streaming.kit.data.repository.socket.streaming.UdpStreamingSocket
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.DataInputStream
import java.net.*
import javax.inject.Inject


class ClientRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
): ClientRepository {

    private val streamingSocket: StreamingSocket = UdpStreamingSocket(context)
    private val clientInfoSocket: ClientInfoSocket = UdpClientInfoSocket(context)

    override suspend fun sendSelfIpAddress(serverIpAdress: String) {
        return clientInfoSocket.sendSelfIpAddress(serverIpAdress)
    }

    override suspend fun receiveStreaming(): Flow<Bitmap> {
        return streamingSocket.receiveStreaming()
    }
}