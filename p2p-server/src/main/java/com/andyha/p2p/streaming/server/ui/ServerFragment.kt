package com.andyha.p2p.streaming.server.ui

import android.Manifest
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.andyha.camerakit.manager.CameraManager
import com.andyha.coreui.base.fragment.BaseFragment
import com.andyha.coreui.base.fragment.ToolbarConfiguration
import com.andyha.coreui.base.ui.widget.showToast
import com.andyha.coreutils.permission.RxPermission
import com.andyha.coreutils.setOnDebounceClick
import com.andyha.p2p.streaming.server.R
import com.andyha.p2p.streaming.server.databinding.FragmentServerBinding
import com.andyha.wifidirectkit.model.ConnectionState
import com.example.p2psharedresources.ui.devices.DevicesBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@AndroidEntryPoint
class ServerFragment :
    BaseFragment<FragmentServerBinding, ServerViewModel>({ FragmentServerBinding.inflate(it) }) {

    override fun getToolbarConfiguration() =
        ToolbarConfiguration.Builder()
            .setDefaultToolbarEnabled(true)
            .setHasBackButton(false)
            .setHasRefreshLayout(false)
            .setTitle(R.string.app_name)
            .setMenuIcon(R.drawable.ic_flit_camera)
            .setMenuIconVisibility(View.VISIBLE)
            .build()

    private val android13Permissions by lazy {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.NEARBY_WIFI_DEVICES,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
        )
    }

    private val android12AndBelowPermissions by lazy {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
        )
    }

    @Inject
    lateinit var cameraManager: CameraManager

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        initUI()
        requestPermission()
        bindData()
    }

    private fun initUI() {
        viewBinding().apply {
            btnDevice.setOnDebounceClick {
                DevicesBottomSheetFragment.show(childFragmentManager, true)
            }

            btnGroup.setOnDebounceClick {
                viewModel.toggleGroup()
            }
        }
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
                    initCamera()
                    initWifiDirect()
                } else {

                }
            }
    }

    private fun bindData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    Timber.d("collect UI state: $it")
                    bindConnectionState(it.connectionState)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.groupFormed
                    .catch { Timber.e(it) }
                    .collect { bindGroupState(it) }
            }
        }
    }

    private fun bindGroupState(group: WifiP2pInfo?) {
        viewBinding().apply {
            group?.let { btnGroup.setImageResource(R.drawable.ic_wifi_tether_on) }
                ?: btnGroup.setImageResource(R.drawable.ic_wifi_tether_off)
            requireContext().showToast(if (group != null) R.string.group_created else R.string.group_removed)
        }
    }

    private fun bindConnectionState(connectionState: ConnectionState) {
        viewBinding().apply {
            when (connectionState) {
                is ConnectionState.Connected -> {
                    if (connectionState.peer.deviceName.isNotEmpty()) {
                        tvDevice.isVisible = true
                        tvDevice.text = connectionState.peer.deviceName
                    }
                    btnGroup.setImageResource(R.drawable.ic_wifi_tether_on)
                }
                ConnectionState.Disconnected -> {
                    tvDevice.isVisible = false
                    btnGroup.setImageResource(R.drawable.ic_wifi_tether_on)
                }
            }
        }
    }

    private fun initCamera() {
        viewBinding().previewView.post {
            cameraManager.lifecycleOwner = viewLifecycleOwner
            cameraManager.containerView = requireView()
            cameraManager.previewView = viewBinding().previewView
            lifecycleScope.launch {
                cameraManager.initCamera()
                bindPreview()
            }
        }
    }

    private fun bindPreview() {
        lifecycleScope.launch {
            cameraManager.previewFrameAnalyzer?.let { previewFrameAnalyzer ->
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    (previewFrameAnalyzer.output as SharedFlow).collect {
                        viewModel.sendCameraFrame(it)
                    }
                }
            }
        }
    }

    private fun initWifiDirect() {
        viewModel.initWifiDirect()
    }

    override fun onClickMenuButton() {
        cameraManager.switchCamera()
        bindPreview()
    }

    override fun onDestroyView() {
        cameraManager.onDestroy()
        super.onDestroyView()
    }
}