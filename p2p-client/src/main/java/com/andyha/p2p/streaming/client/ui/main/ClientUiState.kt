package com.andyha.p2p.streaming.client.ui.main

import com.andyha.wifidirectkit.model.ConnectionState

data class ClientUiState(
    val connectionState: ConnectionState,
    val recordingState: RecordingState,
)

sealed class RecordingState {
    object None: RecordingState()
    data class Recording(val duration: String): RecordingState()
    object SavingVideo: RecordingState()
    object SaveSuccessfully: RecordingState()
    object SaveFailed: RecordingState()
}