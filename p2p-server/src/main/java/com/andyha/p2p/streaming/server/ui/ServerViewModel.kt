package com.andyha.p2p.streaming.server.ui

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.andyha.coreui.base.viewModel.BaseViewModel
import com.andyha.p2p.streaming.kit.data.repository.server.ServerRepository
import com.andyha.wifidirectkit.manager.WifiDirectManager
import com.andyha.wifidirectkit.model.ConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ServerViewModel @Inject constructor(
    private val serverRepository: ServerRepository,
    private val wifiDirectManager: WifiDirectManager,
) : BaseViewModel() {

    //IP addresses of subscribers
    private val subscribers = mutableSetOf<String>()

    private var connectionState: ConnectionState = ConnectionState.Disconnected

    val groupFormed by lazy { wifiDirectManager.groupFormed }

    private val _uiState = MutableStateFlow(ServerUiState(connectionState))
    val uiState = _uiState.asStateFlow()

    fun initWifiDirect() {
        wifiDirectManager.init()
        viewModelScope.launch { wifiDirectManager.createGroup() }
        listenToGroupFormed()
        listenToConnectionState()
    }

    private fun listenToGroupFormed() {
        viewModelScope.launch(Dispatchers.IO) {
            wifiDirectManager
                .groupFormed
                .catch { Timber.e(it) }
                .collect {
                    Timber.d("collect GroupFormed: $it")
                    subscribers.clear()
                    it?.groupOwnerAddress?.hostAddress?.let { it1 ->
                        listenToSubscribers(it1)
                    }
                }
        }
    }

    private fun listenToConnectionState() {
        viewModelScope.launch(Dispatchers.IO) {
            wifiDirectManager
                .connectionState
                .catch { Timber.e(it) }
                .collect {
                    Timber.d("collect ConnectionState: $it")
                    connectionState = it
                    emitUiState()
                }
        }
    }

    private fun emitUiState() {
        _uiState.tryEmit(ServerUiState(connectionState))
    }

    fun toggleGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            wifiDirectManager.toggleGroup()
        }
    }

    fun sendCameraFrame(bitmap: Bitmap) {
        Timber.d("sendCameraFrame: $subscribers")
        for (subscriber in subscribers) {
            viewModelScope.launch(Dispatchers.IO) {
                serverRepository.sendBitmap(subscriber, bitmap)
            }
        }
    }

    private fun listenToSubscribers(serverAddress: String) {
        Timber.d("Start listening to subscribers: $serverAddress")
        viewModelScope.launch(Dispatchers.IO) {
            serverRepository
                .receiveSubscribers(serverAddress)
                .catch { Timber.e(it) }
                .collect {
                    Timber.d("collectSubscriber: $it")
                    subscribers.add(it)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        wifiDirectManager.close()
    }
}