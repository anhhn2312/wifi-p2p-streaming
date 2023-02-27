package com.andyha.p2p.streaming.kit.data.repository.socket.streaming

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.andyha.coreutils.FileUtils.bitmapToByteArray
import com.andyha.p2p.streaming.kit.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.IOException
import java.net.*


class UdpStreamingSocket(context: Context) : StreamingSocket {

    private val wm by lazy { context.getSystemService(Context.WIFI_SERVICE) as WifiManager }

    private var receivedBitmapCount = 0
    private var sentBitmapCount = 0

    override suspend fun sendBitmap(ipAddress: String, bitmap: Bitmap) {
        val data: ByteArray = bitmapToByteArray(bitmap, 1)
        Timber.d("bitmapToByteArray: $data")

        try {
            Timber.d("ipAddress: $ipAddress")
            val datagramSocket = DatagramSocket()
            val dp = DatagramPacket(
                data,
                data.size,
                InetAddress.getByName(ipAddress),
                Constants.STREAMING_PORT
            )
            sentBitmapCount++
            Timber.d("Bitmap sent: $sentBitmapCount")
            datagramSocket.send(dp)
        } catch (e: SocketException) {
            e.printStackTrace()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override suspend fun receiveStreaming(): Flow<Bitmap> {
        return flow {
            val selfIpAddress = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
            Timber.d("ipAddress: $selfIpAddress")
            val buf = ByteArray(1000000)
            try {
                val datagramSocket =
                    DatagramSocket(Constants.STREAMING_PORT, InetAddress.getByName(selfIpAddress))
                datagramSocket.reuseAddress = true
                datagramSocket.soTimeout = 30000
                while (true) {
                    val dp = DatagramPacket(buf, 1000000)
                    datagramSocket.receive(dp)
                    val bitmap = BitmapFactory.decodeByteArray(dp.data, 0, dp.data.size)
                    receivedBitmapCount++
                    Timber.d("Bitmap received: $receivedBitmapCount")
                    emit(bitmap)
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}