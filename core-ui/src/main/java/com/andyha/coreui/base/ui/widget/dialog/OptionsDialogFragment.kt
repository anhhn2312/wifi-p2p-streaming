package com.andyha.coreui.base.ui.widget.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.andyha.coreextension.TAG
import com.andyha.coreextension.expandClickArea
import com.andyha.coreextension.hide
import com.andyha.coreextension.show
import com.andyha.coreui.base.fragment.BaseDialogFragment
import com.andyha.coreui.databinding.FragmentOptionsDialogBinding

class OptionsDialogFragment : BaseDialogFragment<FragmentOptionsDialogBinding>({
    FragmentOptionsDialogBinding.inflate(it)
}) {
    companion object {
        fun showDialog(
            fm: FragmentManager,
            cancelable: Boolean = false,
            title: String? = null,
            @StringRes titleResId: Int? = null,
            message: String? = null,
            @StringRes messageResId: Int? = null,
            messageSpannable: SpannableString? = null,
            optionText: String? = null,
            @StringRes optionResId: Int? = null,
            option1Text: String? = null,
            @StringRes option1ResId: Int? = null,
            option2Text: String? = null,
            @StringRes option2ResId: Int? = null,
            @DrawableRes iconTitle: Int? = null,
            isShowBtnClose: Boolean = true,
            onOptionCloseClicked: (() -> Unit)? = null,
            onOptionClicked: (() -> Unit)? = null,
            onOption1Clicked: (() -> Unit)? = null,
            onOption2Clicked: (() -> Unit)? = null,
            onDismissListener: (() -> Unit)? = null,
            onCancel: (() -> Unit)? = null
        ): OptionsDialogFragment {
            val dialog = OptionsDialogFragment()
            dialog.mCancelable = cancelable

            dialog.title = title
            dialog.titleResId = titleResId

            dialog.message = message
            dialog.messageResId = messageResId
            dialog.messageSpannable = messageSpannable

            dialog.optionText = optionText
            dialog.optionResId = optionResId

            dialog.option1Text = option1Text
            dialog.option1ResId = option1ResId

            dialog.option2Text = option2Text
            dialog.option2ResId = option2ResId

            dialog.isShowBtnClose = isShowBtnClose
            dialog.onOptionCloseClicked = onOptionCloseClicked

            dialog.onOptionClicked = onOptionClicked
            dialog.onOption1Clicked = onOption1Clicked
            dialog.onOption2Clicked = onOption2Clicked
            dialog.onDismissListener = onDismissListener
            dialog.onCancel = onCancel
            dialog.iconTitle = iconTitle

            dialog.show(fm, TAG)
            return dialog
        }
    }

    var title: String? = null
    var titleResId: Int? = null
    var message: String? = null
    var messageResId: Int? = null
    var messageSpannable: SpannableString? = null
    var optionText: String? = null
    var optionResId: Int? = null
    var option1Text: String? = null
    var option1ResId: Int? = null
    var option2Text: String? = null
    var option2ResId: Int? = null
    var isShowBtnClose: Boolean = true
    var onOptionClicked: (() -> Unit)? = null
    var onOption1Clicked: (() -> Unit)? = null
    var onOption2Clicked: (() -> Unit)? = null
    var onOptionCloseClicked: (() -> Unit)? = null
    var onDismissListener: (() -> Unit)? = null
    var mCancelable: Boolean = false
    var type: Int? = null
    var iconTitle: Int? = null
    var onCancel: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateView()
    }

    private fun updateView() {
        isCancelable = mCancelable

        title?.let {
            viewBinding.tvTitle.text = it
            viewBinding.tvTitle.show()
        }
        titleResId?.let {
            viewBinding.tvTitle.setText(it)
            viewBinding.tvTitle.show()
        }

        message?.let {
            viewBinding.tvMessage.text = it
            viewBinding.tvMessage.show()
        }

        messageResId?.let {
            viewBinding.tvMessage.setText(it)
            viewBinding.tvMessage.show()
        }

        messageSpannable?.let {
            viewBinding.tvMessage.setText(it, TextView.BufferType.EDITABLE)
            viewBinding.tvMessage.show()
        }

        if (optionResId != null || optionText != null) {
            viewBinding.btnOption.show()
        } else {
            viewBinding.btnOption.hide()
        }

        if (option1Text != null || option1ResId != null) {
            showOption1()
        }

        if (option2Text != null || option2ResId != null) {
            showOption2()
        }

        if (isBothOptionShown()) {
            viewBinding.viewOptionDivider.show()
        }

        optionText?.let {
            viewBinding.btnOption.text = it
        }

        optionResId?.let {
            viewBinding.btnOption.setText(it)
        }

        option1Text?.let {
            viewBinding.btnOption1.text = it
        }

        option1ResId?.let {
            viewBinding.btnOption1.setText(it)
        }

        option2Text?.let {
            viewBinding.btnOption2.text = it
        }

        option2ResId?.let {
            viewBinding.btnOption2.setText(it)
        }

        if (!title.isNullOrEmpty() || titleResId != null) {
            if (isShowBtnClose) {
                viewBinding.ivClose.show()
            } else {
                viewBinding.ivClose.hide()
            }
        } else {
            if (isShowBtnClose) {
                viewBinding.tvTitle.text = ""
                viewBinding.tvTitle.show()
                viewBinding.ivClose.show()
                viewBinding.ivClose.expandClickArea()
            } else {
                viewBinding.ivClose.hide()
            }
        }

        if (iconTitle != null) {
            viewBinding.ivIconTitle.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    iconTitle!!
                )
            )
            viewBinding.ivIconTitle.show()
        } else {
            viewBinding.ivIconTitle.hide()
        }

        viewBinding.btnOption.setOnClickListener { onOptionClicked?.invoke();dismiss() }
        viewBinding.btnOption1.setOnClickListener { onOption1Clicked?.invoke();dismiss() }
        viewBinding.btnOption2.setOnClickListener { onOption2Clicked?.invoke();dismiss() }
        viewBinding.ivClose.setOnClickListener { onOptionCloseClicked?.invoke(); onCancel?.invoke(); dismiss() }
    }

    private fun showOption2() {
        viewBinding.llOption.show()
        viewBinding.btnOption2.show()
    }

    private fun showOption1() {
        viewBinding.llOption.show()
        viewBinding.btnOption1.show()
    }

    private fun isBothOptionShown(): Boolean {
        return viewBinding.btnOption1.isVisible && viewBinding.btnOption2.isVisible
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancel?.invoke()
    }
}