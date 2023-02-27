package com.andyha.p2p.streaming.server.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.andyha.coredata.manager.NetworkConnectionManager
import com.andyha.coredata.manager.NetworkState
import com.andyha.coredata.manager.NetworkState.AVAILABLE
import com.andyha.coredata.manager.NetworkState.UNAVAILABLE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule


class NetworkConnectionManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : NetworkConnectionManager {
    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private var networkRequest: NetworkRequest

    private val _networkState =
        MutableSharedFlow<NetworkState>(replay = 1).apply { tryEmit(getNetworkState()) }
    override val networkState: SharedFlow<NetworkState> = _networkState.asSharedFlow()

    private val networkCallBack = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Timber.d("NetworkDebug: Network available")
            _networkState.tryEmit(AVAILABLE)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Timber.d("NetworkDebug: onLost")
            Timer().schedule(1000){
                _networkState.tryEmit(getNetworkState())
            }
        }

        override fun onUnavailable() {
            super.onUnavailable()
            Timber.d("NetworkDebug: onUnavailable")
            Timer().schedule(1000){
                _networkState.tryEmit(getNetworkState())
            }
        }
    }

    init {
        val builder = NetworkRequest.Builder()
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        networkRequest = builder.build()
        this.connectivityManager.registerNetworkCallback(networkRequest, networkCallBack)
    }

    private fun ConnectivityManager.isConnected(): Boolean {
        val wifiNetwork = getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val cellularNetwork = getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        Timber.d("NetworkDebug: wifi: ${wifiNetwork?.isConnected}")
        Timber.d("NetworkDebug: mobile: ${cellularNetwork?.isConnected}")
        return wifiNetwork?.isConnected == true || cellularNetwork?.isConnected == true
    }

    override fun getNetworkState(): NetworkState {
        return if (connectivityManager.isConnected()) AVAILABLE else UNAVAILABLE
    }
}