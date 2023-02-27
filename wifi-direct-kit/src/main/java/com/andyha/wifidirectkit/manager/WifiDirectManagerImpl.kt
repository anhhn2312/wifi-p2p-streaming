package com.andyha.wifidirectkit.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import com.andyha.wifidirectkit.model.ConnectionState
import com.andyha.wifidirectkit.utils.DirectActionListener
import com.andyha.wifidirectkit.utils.WifiDirectBroadcastReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class WifiDirectManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
): WifiDirectManager {
    private lateinit var wifiP2pManager: WifiP2pManager

    private lateinit var wifiP2pChannel: WifiP2pManager.Channel

    private var wifiP2pEnabled = false

    private val _groupFormed = MutableSharedFlow<WifiP2pInfo?>(replay = 1)
    override val groupFormed = _groupFormed.asSharedFlow()

    private val _availablePeers = MutableSharedFlow<List<WifiP2pDevice>>(replay = 1)
    override val availablePeers = _availablePeers.asSharedFlow()

    private val _connectedState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override var connectionState = _connectedState.asStateFlow()

    private var connectedPeer: WifiP2pDevice? = null

    private var broadcastReceiver: BroadcastReceiver? = null

    private val directActionListener = object : DirectActionListener {

        override fun wifiP2pEnabled(enabled: Boolean) {
            Timber.d("wifiP2pEnabled: $enabled")
            wifiP2pEnabled = enabled
        }

        override fun onConnectionInfoAvailable(wifiP2pInfo: WifiP2pInfo) {
            Timber.d("onConnectionInfoAvailable: $wifiP2pInfo")
            if (wifiP2pInfo.groupFormed) {
                _groupFormed.tryEmit(wifiP2pInfo)
                connectedPeer?.let {
                    _connectedState.tryEmit(ConnectionState.Connected(wifiP2pInfo, it))
                }
            }
        }

        override fun onDisconnection() {
            Timber.d("onDisconnection")
            _connectedState.tryEmit(ConnectionState.Disconnected)
        }

        override fun onSelfDeviceAvailable(wifiP2pDevice: WifiP2pDevice) {
            Timber.d("onSelfDeviceAvailable: $wifiP2pDevice")
        }

        override fun onPeersAvailable(wifiP2pDeviceList: Collection<WifiP2pDevice>) {
            Timber.d("onPeersAvailable: $wifiP2pDeviceList")
            connectedPeer = wifiP2pDeviceList.firstOrNull { it.status == WifiP2pDevice.CONNECTED }
            _availablePeers.tryEmit(wifiP2pDeviceList.toList())
        }

        override fun onChannelDisconnected() {
            Timber.d("onChannelDisconnected")
        }
    }

    override fun init() {
        wifiP2pManager =
            context.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager ?: return
        wifiP2pChannel =
            wifiP2pManager.initialize(context, context.mainLooper, directActionListener)
        broadcastReceiver = WifiDirectBroadcastReceiver(
            wifiP2pManager = wifiP2pManager,
            wifiP2pChannel = wifiP2pChannel,
            directActionListener = directActionListener
        )
        context.registerReceiver(broadcastReceiver, WifiDirectBroadcastReceiver.getIntentFilter())
        discoverPeers()
    }

    override fun discoverPeers() {
        wifiP2pManager.discoverPeers(wifiP2pChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Timber.d("discoverPeers successfully")
            }

            override fun onFailure(reasonCode: Int) {
                Timber.d("discoverPeers failed：$reasonCode")
            }
        })
    }

    override fun connect(wifiP2pDevice: WifiP2pDevice) {
        Timber.d("Try to connect: $wifiP2pDevice")
        val wifiP2pConfig = WifiP2pConfig()
        wifiP2pConfig.deviceAddress = wifiP2pDevice.deviceAddress
        wifiP2pConfig.wps.setup = WpsInfo.PBC
        wifiP2pManager.connect(wifiP2pChannel, wifiP2pConfig,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Timber.d("connect successfully")
                }

                override fun onFailure(reason: Int) {
                    Timber.d("Connect failed: $reason")
                }
            })
    }

    override fun disconnect() {
        wifiP2pManager.cancelConnect(wifiP2pChannel, object : WifiP2pManager.ActionListener {
            override fun onFailure(reasonCode: Int) {
                Timber.d("cancelConnect failed: $reasonCode")
            }

            override fun onSuccess() {
                Timber.d("cancelConnect successfully")
            }
        })
        wifiP2pManager.removeGroup(wifiP2pChannel, null)
    }

    override suspend fun createGroup() {
        removeGroup()
        createGroupInternal()
    }

    private fun createGroupInternal() {
        wifiP2pManager.createGroup(wifiP2pChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Timber.d("createGroup successfully")
            }

            override fun onFailure(reason: Int) {
                Timber.d("createGroup failed: $reason")
            }
        })
    }

    override suspend fun removeGroup() {
        return suspendCancellableCoroutine { continuation ->
            wifiP2pManager.requestGroupInfo(wifiP2pChannel) { group ->
                if (group == null) {
                    continuation.resume(value = Unit)
                } else {
                    removeGroupInternal(onComplete = { continuation.resume(value = Unit) })
                }
            }
        }
    }

    private fun removeGroupInternal(onComplete: (() -> Unit)? = null) {
        wifiP2pManager.removeGroup(wifiP2pChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Timber.d("removeGroup successfully")
                    onComplete?.invoke()
                    _groupFormed.tryEmit(null)
                }

                override fun onFailure(reason: Int) {
                    Timber.d("removeGroup failed: $reason")
                    onComplete?.invoke()
                }
            })
    }

    override suspend fun toggleGroup() {
        wifiP2pManager.requestGroupInfo(wifiP2pChannel) { group ->
            if (group == null) {
                createGroupInternal()
            } else {
                removeGroupInternal()
            }
        }
    }

    override fun close() {
        disconnect()
    }
}
