package com.example.p2psharedresources.ui.devices

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.andyha.coreui.base.adapter.BaseListAdapter
import com.andyha.coreui.base.adapter.BaseViewHolder
import com.andyha.p2psharedresources.databinding.ViewHolderDeviceBinding


class DeviceAdapter(
    private val isServerSide: Boolean,
    private val onConnect: (WifiP2pDevice) -> Unit
) : BaseListAdapter<WifiP2pDevice>(DIFF_CALLBACK) {

    override fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup): ViewBinding {
        return ViewHolderDeviceBinding.inflate(layoutInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding): BaseViewHolder<WifiP2pDevice> {
        return DeviceViewHolder(viewBinding as ViewHolderDeviceBinding, isServerSide, onConnect)
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WifiP2pDevice>() {
            override fun areItemsTheSame(
                oldItem: WifiP2pDevice,
                newItem: WifiP2pDevice
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: WifiP2pDevice,
                newItem: WifiP2pDevice
            ): Boolean = oldItem.deviceName == newItem.deviceName &&
                    oldItem.status == newItem.status
        }
    }
}