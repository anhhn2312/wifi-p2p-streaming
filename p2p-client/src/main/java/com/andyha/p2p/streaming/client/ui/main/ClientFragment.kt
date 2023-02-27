package com.andyha.p2p.streaming.client.ui.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.andyha.coreui.base.fragment.BaseFragment
import com.andyha.coreui.base.fragment.ToolbarConfiguration
import com.andyha.coreui.base.ui.widget.showToast
import com.andyha.coreutils.permission.RxPermission
import com.andyha.coreutils.setOnDebounceClick
import com.andyha.p2p.streaming.client.R
import com.andyha.p2p.streaming.client.databinding.FragmentClientBinding
import com.andyha.p2p.streaming.client.ui.settings.ClientSettingsBottomSheetFragment
import com.andyha.wifidirectkit.model.ConnectionState
import com.example.p2psharedresources.ui.devices.DevicesBottomSheetFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File


class ClientFragment :
    BaseFragment<FragmentClientBinding, ClientViewModel>({ FragmentClientBinding.inflate(it) }) {

    override fun getToolbarConfiguration() =
        ToolbarConfiguration.Builder()
            .setDefaultToolbarEnabled(true)
            .setHasBackButton(false)
            .setHasRefreshLayout(false)
            .setTitle(R.string.app_name)
            .setMenuIcon(R.drawable.ic_settings)
            .setMenuIconVisibility(View.VISIBLE)
            .build()

    private val android13Permissions by lazy {
        arrayOf(
            Manifest.permission.NEARBY_WIFI_DEVICES,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    private val android12AndBelowPermissions by lazy {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        requestPermission()
        initUI()
        bindData()
    }

    private fun requestPermission() {
        RxPermission.getInstance(requireContext())
            .withPermissions(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    android13Permissions
                else
                    android12AndBelowPermissions
            )
            .requestForFullResult {
                if (it) {
                    viewModel.initWifiDirect()
                }
            }
    }

    private fun initUI(){
        viewBinding().apply {
            btnRecording.setOnDebounceClick {
                viewModel.toggleRecording(requireContext())
            }
            btnRecording.isEnabled = false

            btnDevice.setOnDebounceClick {
                DevicesBottomSheetFragment.show(childFragmentManager)
            }

            btnCollections.setOnDebounceClick {
                openSavedVideoFolder()
            }

            llNoConnection.setOnDebounceClick {
                DevicesBottomSheetFragment.show(childFragmentManager)
            }
        }
    }

    private fun bindData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bitmap.collect {
//                    Timber.d("collectBitmap: $it")
                    viewBinding().apply {
                        previewLayout.isVisible = true
                        progressbar.isVisible = false
                        ivPreview.setImageBitmap(it)
                        if (viewModel.uiState.value.recordingState != RecordingState.SavingVideo) {
                            btnRecording.isEnabled = true
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { bindRecordingState(it.recordingState) }
                    .distinctUntilChanged { old, new -> old.connectionState == new.connectionState }
                    .onEach { bindConnectionState(it.connectionState) }
                    .collect()
            }
        }
    }

    private fun bindConnectionState(connectionState: ConnectionState) {
        viewBinding().apply {
            when (connectionState) {
                is ConnectionState.Connected -> {
                    llNoConnection.isVisible = false
                    previewLayout.isVisible = true
                    btnDevice.setImageResource(R.drawable.ic_device)
                    if (connectionState.peer.deviceName.isNotEmpty()) {
                        tvDevice.isVisible = true
                        tvDevice.text = connectionState.peer.deviceName
                    }
                    progressbar.isVisible = true
                }
                ConnectionState.Disconnected -> {
                    llNoConnection.isVisible = true
                    previewLayout.isVisible = false
                    btnDevice.setImageResource(R.drawable.ic_add)
                    tvDevice.isVisible = false
                }
            }
        }
    }

    private fun bindRecordingState(recordingState: RecordingState) {
        viewBinding().apply {
            when (recordingState) {
                RecordingState.None -> {
                    btnRecording.isEnabled = previewLayout.isVisible
                    btnRecording.setImageResource(R.drawable.ic_start_recording_selector)
                    tvRecording.isVisible = false
                }
                is RecordingState.Recording -> {
                    btnRecording.isEnabled = previewLayout.isVisible
                    btnRecording.setImageResource(R.drawable.ic_stop_recording)
                    tvRecording.isVisible = true
                    tvRecording.text = recordingState.duration
                }
                RecordingState.SavingVideo -> {
                    btnRecording.isEnabled = false
                    btnRecording.setImageResource(R.drawable.ic_start_recording_selector)
                    tvRecording.isVisible = true
                    tvRecording.setText(R.string.saving_video)
                }
                RecordingState.SaveSuccessfully -> {
                    requireContext().showToast(R.string.video_saved_sucessfully)
                }
                RecordingState.SaveFailed -> {
                    requireContext().showToast(R.string.video_saved_failed)
                }
            }
        }
    }

    override fun onClickMenuButton() {
        ClientSettingsBottomSheetFragment.show(childFragmentManager)
    }

    private fun openSavedVideoFolder() {
        val location = "/storage/emulated/0/Download/P2P-Streaming"
        val intent = Intent(Intent.ACTION_VIEW)
        val myDir: Uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName + ".provider",
            File(location)
        )
//        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setDataAndType(myDir, DocumentsContract.Document.MIME_TYPE_DIR)

        if (intent.resolveActivityInfo(requireContext().packageManager, 0) != null) {
            requireActivity().startActivity(intent)
        } else {
            requireContext().showToast("No saved video found")
        }
    }
}