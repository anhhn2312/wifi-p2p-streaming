package com.andyha.coredata.manager

import kotlinx.coroutines.flow.SharedFlow


interface NetworkConnectionManager {
    val networkState: SharedFlow<NetworkState>
    fun getNetworkState(): NetworkState
}

enum class NetworkState {
    AVAILABLE,
    UNAVAILABLE
}