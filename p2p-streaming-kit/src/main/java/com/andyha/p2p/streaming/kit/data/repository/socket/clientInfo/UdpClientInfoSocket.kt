package com.andyha.p2p.streaming.kit.data.repository.socket.clientInfo

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.andyha.p2p.streaming.kit.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.IOException
import java.net.*


class UdpClientInfoSocket(private val context: Context) : ClientInfoSocket {

    private val wm by lazy { context.getSystemService(Context.WIFI_SERVICE) as WifiManager }

    override fun sendSelfIpAddress(serverIpAdress: String) {
        Timber.d("sendSelfIpAddress: to server: $serverIpAdress")
        val selfIpAddress = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
        val data: ByteArray = selfIpAddress.encodeToByteArray()
        val decodedString = data.decodeToString()
        Timber.d("decodedString: $decodedString")

        try {
            val datagramSocket = DatagramSocket()
            val dp = DatagramPacket(
                data,
                data.size,
                InetAddress.getByName(serverIpAdress),
                Constants.CLIENT_INFO_PORT
            )
            datagramSocket.send(dp)
        } catch (e: SocketException) {
            e.printStackTrace()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override suspend fun receiveSubscribers(localServerAddress: String): Flow<String> {
        return flow {
            Timber.d("ipAddress: $localServerAddress")
            val buf = ByteArray(1000000)
            try {
                val datagramSocket = DatagramSocket(
                    Constants.CLIENT_INFO_PORT,
                    InetAddress.getByName(localServerAddress)
                )
                while (true) {
                    val dp = DatagramPacket(buf, 1000000)
                    datagramSocket.receive(dp)
                    val ipAddress = String(dp.data, 0, dp.length)
                    Timber.d("receive subscriber IP: $ipAddress")
                    emit(ipAddress)
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