package com.andyha.wifidirectkit.manager

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import com.andyha.wifidirectkit.model.ConnectionState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow


interface WifiDirectManager {
    val groupFormed: SharedFlow<WifiP2pInfo?>
    val availablePeers: SharedFlow<List<WifiP2pDevice>>
    val connectionState: StateFlow<ConnectionState>

    fun init()
    fun discoverPeers()
    fun connect(wifiP2pDevice: WifiP2pDevice)
    fun disconnect()
    suspend fun createGroup()
    suspend fun removeGroup()
    suspend fun toggleGroup()
    fun close()
}