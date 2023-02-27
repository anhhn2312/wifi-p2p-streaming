package com.example.p2psharedresources.ui.devices

import android.net.wifi.p2p.WifiP2pDevice
import androidx.core.view.isVisible
import com.andyha.coreui.base.adapter.BaseViewHolder
import com.andyha.coreutils.setOnDebounceClick
import com.andyha.p2psharedresources.R
import com.andyha.p2psharedresources.databinding.ViewHolderDeviceBinding


class DeviceViewHolder(
    private val viewBinding: ViewHolderDeviceBinding,
    private val isServerSide: Boolean,
    private val onConnect: (WifiP2pDevice) -> Unit,
) : BaseViewHolder<WifiP2pDevice>(viewBinding.root) {

    override fun bind(item: WifiP2pDevice) {
        viewBinding.apply {
            tvName.text = item.deviceName
            ivStatus.setImageResource(
                when (item.status) {
                    WifiP2pDevice.UNAVAILABLE -> R.drawable.ic_unavailable_dot
                    else -> R.drawable.ic_available_dot
                }
            )
            tvConnect.isVisible = !isServerSide && item.status == WifiP2pDevice.AVAILABLE
            connectingProgress.isVisible = item.status == WifiP2pDevice.INVITED
            ivConnected.isVisible = item.status == WifiP2pDevice.CONNECTED
            tvConnect.setOnDebounceClick {
                onConnect(item)
            }

        }
    }
}