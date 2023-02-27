package com.andyha.coreui.base.ui.widget.progress

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.andyha.coreextension.getColorAttr
import com.andyha.coreui.R
import com.andyha.coreui.databinding.DialogProgressBinding


class ProgressDialog : DialogFragment() {

    private var viewBinding: DialogProgressBinding? = null

    private var shouldShow: Boolean? = null

    private var appearanceCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(ContextThemeWrapper(requireActivity(), R.style.DialogAnimation)).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBinding = DialogProgressBinding.inflate(layoutInflater)
        return viewBinding!!.root
    }

    override fun onStart() {
        super.onStart()
        setupDialogStyle()
    }

    override fun onResume() {
        super.onResume()
        // dismiss the dialog when back from a fragment to the previous one
        // if the dialog couldn't finish dismissing because it was detached from it's parent
        if (shouldShow == false) {
            this.hideProgress()
        }
    }

    override fun onDetach() {
        hideProgress()
        super.onDetach()
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }


    private fun showMessage(message: String) {
        viewBinding?.tvMessage?.text = message
    }

    private fun setupDialogStyle() {
        dialog?.window?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            setLayout(width, height)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            attributes?.apply {
                dimAmount = 0F
                flags = flags.or(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }

            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        viewBinding?.progressBar?.setIndicatorColor(requireActivity().getColorAttr(android.R.attr.colorControlNormal))
    }

    fun showProgress(fm: FragmentManager) {
        appearanceCount++
        if (appearanceCount > 1) return
        shouldShow = true
        if (!this.isAdded && this.dialog?.isShowing != true) {
            kotlin.runCatching { this.show(fm, null) }
        }
    }

    fun hideProgress(): Boolean {
        appearanceCount--
        if (appearanceCount > 0) return false
        appearanceCount = 0
        shouldShow = false
        kotlin.runCatching { this.dismissAllowingStateLoss() }
        return true
    }
}