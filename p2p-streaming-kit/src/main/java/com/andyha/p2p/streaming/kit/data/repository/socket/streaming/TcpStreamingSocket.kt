package com.andyha.p2p.streaming.kit.data.repository.socket.streaming

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.andyha.coreutils.FileUtils.bitmapToByteArray
import com.andyha.p2p.streaming.kit.util.Constants
import com.andyha.p2p.streaming.kit.util.Constants.STREAMING_PORT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.net.UnknownHostException


class TcpStreamingSocket : StreamingSocket {

    private var sentBitmapCount = 0
    private var senderSocket: Socket? = null
    private var dataOutputStream: DataOutputStream? = null

    private var serverSocket: ServerSocket? = null
    private var receiverSocket: Socket? = null
    private var dataInputStream: DataInputStream? = null
    private var bitmapAvgReceivedLength = 0
    private var receivedBitmapCount = 0

    override suspend fun sendBitmap(ipAddress: String, bitmap: Bitmap) {
        val data: ByteArray = bitmapToByteArray(bitmap, 1)
        Timber.d("bitmapToByteArray: $data")

        if (data.isEmpty()) return

        try {
            Timber.d("ipAddress: $ipAddress")

            if (senderSocket == null) {
                senderSocket = Socket(ipAddress, STREAMING_PORT)
                senderSocket?.reuseAddress = true
                senderSocket?.soTimeout = 30000
            }

            dataOutputStream = DataOutputStream(senderSocket!!.getOutputStream())
            dataOutputStream?.writeInt(data.size)
            Timber.d("Sent length: ${data.size}")
            dataOutputStream?.write(data)
            sentBitmapCount++
            Timber.d("Bitmap sent: $sentBitmapCount")
        } catch (e: SocketException) {
            e.printStackTrace()
            dataOutputStream?.close()
            senderSocket?.close()
            senderSocket = null
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override suspend fun receiveStreaming(): Flow<Bitmap> {
        return flow {
            try {
                if (serverSocket == null && receiverSocket == null) {
                    val serverSocket = ServerSocket(Constants.STREAMING_PORT)
                    serverSocket.reuseAddress = true
                    serverSocket.soTimeout = 30000
                    receiverSocket = serverSocket.accept()
                    receiverSocket?.reuseAddress = true
                    dataInputStream = DataInputStream(receiverSocket!!.getInputStream())
                }

                while (true) {
                    if (dataInputStream!!.available() > 0) {
                        val length = dataInputStream!!.readInt()
                        Timber.d("Length received: $length")
                        Timber.d("bitmapAvgReceivedLength: $bitmapAvgReceivedLength")
                        if (bitmapAvgReceivedLength > 0) {
                            Timber.d("avg ratio: ${length / bitmapAvgReceivedLength}")
                        }

                        if (bitmapAvgReceivedLength > 0 && length / bitmapAvgReceivedLength > UNUSUAL_BIG_BITMAP_FACTOR)
                            continue

                        if (length > 0) {
                            val data = ByteArray(length)
                            dataInputStream!!.safeReadFully(data, 0, length)
                            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                            if (bitmap != null) {
                                bitmapAvgReceivedLength =
                                    (bitmapAvgReceivedLength * receivedBitmapCount + length) / (receivedBitmapCount + 1)
                                receivedBitmapCount++
                                Timber.d("Bitmap received: $receivedBitmapCount")
                                emit(bitmap)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                dataInputStream?.close()
                receiverSocket?.close()
                receiverSocket = null
                serverSocket?.close()
                serverSocket = null
            }
        }
    }

    @Throws(IOException::class)
    fun InputStream.safeReadFully(b: ByteArray?, off: Int, len: Int): Int {
        if (len < 0) throw IndexOutOfBoundsException()
        var n = 0
        while (n < len) {
            val count: Int = read(b, off + n, len - n)
            if (count < 0) break
            n += count
        }
        return n
    }

    companion object {
        const val UNUSUAL_BIG_BITMAP_FACTOR = 5
    }
}