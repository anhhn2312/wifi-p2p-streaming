package com.example.p2psharedresources.ui.devices

import android.net.wifi.p2p.WifiP2pDevice
import com.andyha.coreui.base.viewModel.BaseViewModel
import com.andyha.wifidirectkit.manager.WifiDirectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject


@HiltViewModel
class DeviceBottomSheetViewModel @Inject constructor(
    private val wifiDirectManager: WifiDirectManager
) : BaseViewModel() {

    val availablePeers: SharedFlow<List<WifiP2pDevice>> by lazy {
        wifiDirectManager.availablePeers
    }

    fun discoverPeers(){
        wifiDirectManager.discoverPeers()
    }

    fun connectToPeer(peer: WifiP2pDevice){
        wifiDirectManager.connect(peer)
    }

//    private fun dummyData() {
//        val dummyPeers = MutableSharedFlow<List<WifiP2pDevice>>(replay = 1)
//        availablePeers = dummyPeers.asSharedFlow()
//        val list = mutableListOf<WifiP2pDevice>()
//        for (i in 0..10) {
//            list.add(WifiP2pDevice().apply {
//                deviceName = "Dummy device $i"
//                deviceAddress = "Dummy address $i"
//                status = i % 5
//            })
//        }
//        dummyPeers.tryEmit(list)
//    }
}