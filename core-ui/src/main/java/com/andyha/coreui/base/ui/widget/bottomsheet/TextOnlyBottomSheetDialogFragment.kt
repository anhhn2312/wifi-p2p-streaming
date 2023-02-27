package com.andyha.coreui.base.ui.widget.bottomsheet

import androidx.fragment.app.FragmentManager
import com.andyha.coreextension.TAG
import com.andyha.coreui.base.viewModel.BaseViewModel
import com.andyha.coreui.databinding.BsTextOnlyBinding


class TextOnlyBottomSheetDialogFragment :
    BaseBottomSheetDialogFragment<BsTextOnlyBinding, BaseViewModel>({ BsTextOnlyBinding.inflate(it) }) {

    private var content: Int? = null

    override fun onFragmentCreated() {
        viewBinding.root.setCustomText(content)
    }

    fun show(manager: FragmentManager) {
        super.show(manager, TAG)
    }

    companion object {
        fun newInstance(title: Int, content: Int): TextOnlyBottomSheetDialogFragment {
            return TextOnlyBottomSheetDialogFragment().apply {
                this.titleRes = title
                this.content = content
            }
        }
    }
}