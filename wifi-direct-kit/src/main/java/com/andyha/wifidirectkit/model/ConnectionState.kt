package com.andyha.wifidirectkit.model

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo


sealed class ConnectionState {
    data class Connected(
        val group: WifiP2pInfo,
        val peer: WifiP2pDevice
    ) : ConnectionState()

    object Disconnected : ConnectionState()
}