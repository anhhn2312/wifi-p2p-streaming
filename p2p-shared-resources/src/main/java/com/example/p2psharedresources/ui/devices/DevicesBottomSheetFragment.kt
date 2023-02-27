package com.example.p2psharedresources.ui.devices

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.andyha.coreextension.TAG
import com.andyha.coreextension.utils.DimensionUtils
import com.andyha.coreui.base.ui.widget.bottomsheet.BaseBottomSheetDialogFragment
import com.andyha.coreui.base.ui.widget.showToast
import com.andyha.coreutils.setOnDebounceClick
import com.andyha.p2psharedresources.R
import com.andyha.p2psharedresources.databinding.BottomSheetDevicesBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DevicesBottomSheetFragment :
    BaseBottomSheetDialogFragment<BottomSheetDevicesBinding, DeviceBottomSheetViewModel>({
        BottomSheetDevicesBinding.inflate(it)
    }) {

    private var isServerSide: Boolean = false

    private val adapter by lazy {
        DeviceAdapter(isServerSide) { viewModel.connectToPeer(it) }
    }

    override fun onFragmentCreated() {
        initUI()
        bindData()
    }

    private fun initUI() {
        viewBinding.apply {
            rvPeers.adapter = adapter
            btnRefresh.setOnDebounceClick {
                viewModel.discoverPeers()
                requireContext().showToast(R.string.discovering_peers)
            }
        }

        dialog?.setOnShowListener {
            initLayoutHeight()
        }

        setUpMaxHeightForPopup()
    }

    private fun bindData() {
        viewModel.discoverPeers()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.availablePeers.collect {
                    Timber.d("submitList: $it")
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun initLayoutHeight() {
        val d = dialog as BottomSheetDialog
        val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.skipCollapsed = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val params: CoordinatorLayout.LayoutParams =
            bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
        val maxHeight = getPopupHeight(MAX_HEIGHT_PERCENTAGE)
        params.height = maxHeight
        bottomSheet.layoutParams = params
        behavior.peekHeight = maxHeight
    }

    private fun setUpMaxHeightForPopup() {
        val maxHeight = getPopupHeight(MAX_HEIGHT_PERCENTAGE)
        viewBinding.root.maxHeight = maxHeight + 1
    }

    private fun getPopupHeight(percent: Float): Int {
        return (DimensionUtils.getScreenHeight() * percent).toInt()
    }

    companion object {
        private const val MAX_HEIGHT_PERCENTAGE = 0.5f

        fun show(
            fm: FragmentManager,
            isServerSide: Boolean = false
        ) {
            val dialog = DevicesBottomSheetFragment()
            dialog.titleRes = R.string.devices_bottom_sheet_header
            dialog.useCommonTitle = false
            dialog.isServerSide = isServerSide
            dialog.show(fm, TAG)
        }
    }
}