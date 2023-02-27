package com.andyha.p2p.streaming.client.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaFormat
import android.os.Environment
import androidx.lifecycle.viewModelScope
import com.andyha.coreui.base.viewModel.BaseViewModel
import com.andyha.coreutils.FileUtils
import com.andyha.coreutils.time.TimeFormatter
import com.andyha.p2p.streaming.kit.data.repository.client.ClientRepository
import com.andyha.p2p.streaming.kit.encoder.Muxer
import com.andyha.p2p.streaming.kit.encoder.MuxerConfig
import com.andyha.p2p.streaming.kit.encoder.MuxingError
import com.andyha.p2p.streaming.kit.encoder.MuxingSuccess
import com.andyha.wifidirectkit.manager.WifiDirectManager
import com.andyha.wifidirectkit.model.ConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val wifiDirectManager: WifiDirectManager
) : BaseViewModel() {

    private var connectionState: ConnectionState = ConnectionState.Disconnected
    private var recordingState: RecordingState = RecordingState.None

    private val _uiState = MutableStateFlow(ClientUiState(connectionState, recordingState))
    val uiState = _uiState.asStateFlow()

    private val _bitmap = MutableSharedFlow<Bitmap>(replay = 1)
    val bitmap = _bitmap.asSharedFlow()

    private val recordingBitmapList = mutableListOf<Bitmap>()
    private var savingJob: Job? = null
    private var timerJob: Job? = null

    private var mimeType = MediaFormat.MIMETYPE_VIDEO_AVC

    fun initWifiDirect() {
        wifiDirectManager.init()
        listenToConnectionState()
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
                    if (connectionState is ConnectionState.Connected) {
                        (connectionState as ConnectionState.Connected).group.groupOwnerAddress.hostAddress?.let { toServer ->
                            sendSelfIpAddress(toServer)
                            listenToBitmap()
                        }
                    }
                }
        }
    }

    private fun listenToBitmap() {
        viewModelScope.launch(Dispatchers.IO) {
            clientRepository
                .receiveStreaming()
                .catch { Timber.e(it) }
                .collect {
//                    Timber.d("collectBitmap: $it")
                    _bitmap.tryEmit(it)
                    if (recordingState is RecordingState.Recording) {
                        recordingBitmapList.add(it)
                    }
                }
        }
    }

    private fun emitUiState() {
        _uiState.tryEmit(ClientUiState(connectionState, recordingState))
    }

    private fun sendSelfIpAddress(serverAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            clientRepository.sendSelfIpAddress(serverAddress)
        }
    }

    fun toggleRecording(context: Context) {
        if (recordingState == RecordingState.None) {
            if (savingJob?.isActive == true || timerJob?.isActive == true) return
            var duration = 0
            timerJob = flow {
                while (true) {
                    emit(duration)
                    duration += 1000
                    delay(1000L)
                }
            }
                .map { TimeFormatter.formatDurations(it / 1000) }
                .onEach {
                    recordingState = RecordingState.Recording(it)
                    emitUiState()
                }
                .launchIn(viewModelScope)

        } else if (recordingState is RecordingState.Recording) {
            if (recordingBitmapList.isEmpty()) return

            timerJob?.cancel()
            recordingState = RecordingState.SavingVideo
            emitUiState()

            createVideo(context)
        }
    }

    private fun createVideo(context: Context) {
        savingJob?.cancel()
        savingJob = null
        savingJob = viewModelScope.launch(Dispatchers.Default) {
            val videoFile =
                FileUtils.getFileInPublicExternalStorage(
                    Environment.DIRECTORY_DOWNLOADS,
                    P2P_STREAMING_FOLDER,
                    "p2p-recording-${System.currentTimeMillis() * 1000}.mp4"
                )
            videoFile.run {
                val muxerConfig = MuxerConfig(
                    this,
                    recordingBitmapList[0].width,
                    recordingBitmapList[0].height,
                    mimeType
                )
                val muxer = Muxer(context, muxerConfig)

                when (val result = muxer.muxAsync(recordingBitmapList)) {
                    is MuxingSuccess -> {
                        Timber.d("Video muxed - file path: ${result.file.absolutePath}")
                        recordingState = RecordingState.SaveSuccessfully
                        emitUiState()
                    }
                    is MuxingError -> {
                        Timber.d("There was an error muxing the video")
                        recordingState = RecordingState.SaveFailed
                        emitUiState()
                    }
                }
            }
            savingJob?.invokeOnCompletion {
                recordingState = RecordingState.None
                recordingBitmapList.clear()
                emitUiState()
            }
        }
    }

    override fun onCleared() {
        Timber.e("ClientViewModel on cleared")
        wifiDirectManager.close()
        super.onCleared()
    }

    companion object {
        const val P2P_STREAMING_FOLDER = "P2P-Streaming"
    }
}