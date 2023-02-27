package com.andyha.coreui.base.ui.widget.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import com.andyha.coreextension.TAG
import com.andyha.coreextension.expandClickArea
import com.andyha.coreextension.hide
import com.andyha.coreextension.show
import com.andyha.coreui.base.fragment.BaseDialogFragment
import com.andyha.coreui.databinding.FragmentOptionsStateDialogBinding

class OptionsStateDialogFragment : BaseDialogFragment<FragmentOptionsStateDialogBinding>({
    FragmentOptionsStateDialogBinding.inflate(it)
}) {
    companion object {
        fun showDialog(
            fm: FragmentManager,
            cancelable: Boolean = false,
            title: String? = null,
            @DrawableRes iconResId: Int? = null,
            @StringRes titleResId: Int? = null,
            title2: String? = null,
            @StringRes titleResId2: Int? = null,
            message: SpannableString? = null,
            @StringRes messageResId: Int? = null,
            message2: String? = null,
            @StringRes messageResId2: Int? = null,
            message3: String? = null,
            @StringRes messageResId3: Int? = null,
            question: String? = null,
            @StringRes questionResId: Int? = null,
            optionText: String? = null,
            @StringRes optionResId: Int? = null,
            option1Text: String? = null,
            @StringRes option1ResId: Int? = null,
            option2Text: String? = null,
            @StringRes option2ResId: Int? = null,
            isShowBtnClose: Boolean = true,
            onOptionCloseClicked: (() -> Unit)? = null,
            onOptionClicked: (() -> Unit)? = null,
            onOption1Clicked: (() -> Unit)? = null,
            onOption2Clicked: (() -> Unit)? = null,
            onDismissListener: (() -> Unit)? = null,
        ): OptionsStateDialogFragment {
            val dialog = OptionsStateDialogFragment()
            dialog.mCancelable = cancelable

            dialog.title = title
            dialog.titleResId = titleResId

            dialog.title2 = title2
            dialog.titleResId2 = titleResId2

            dialog.iconResId = iconResId

            dialog.message = message
            dialog.messageResId = messageResId

            dialog.message2 = message2
            dialog.messageResId2 = messageResId2

            dialog.message3 = message3
            dialog.messageResId3 = messageResId3

            dialog.question = question
            dialog.questionResId = questionResId

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

            dialog.show(fm, TAG)
            return dialog
        }
    }

    var title: String? = null
    var titleResId: Int? = null
    var title2: String? = null
    var titleResId2: Int? = null
    var iconResId: Int? = null
    var message: SpannableString? = null
    var message2: String? = null
    var message3: String? = null
    var messageResId: Int? = null
    var messageResId2: Int? = null
    var messageResId3: Int? = null
    var question: String? = null
    var questionResId: Int? = null
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
    var onQuestionClicked: (() -> Unit)? = null
    var onDismissListener: (() -> Unit)? = null
    var mCancelable: Boolean = false
    var type: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateView()
    }

    private fun updateView() {
        isCancelable = mCancelable

        viewBinding.apply {
            title?.let {
                tvTitle.text = it
                tvTitle.show()
            }

            titleResId?.let {
                tvTitle.setText(it)
                tvTitle.show()
            }

            iconResId?.let {
                ivIcon.setBackgroundResource(it)
            }

            message?.let {
                tvMessage.text = it
                tvMessage.show()
            }

            messageResId?.let {
                tvMessage.setText(it)
                tvMessage.show()
            }

            message2?.let {
                tvMessage2.text = it
                tvMessage2.show()
            }

            messageResId2?.let {
                tvMessage2.setText(it)
                tvMessage2.show()
            }

            message3?.let {
                tvMessage3.text = it
            }

            messageResId3?.let {
                tvMessage3.setText(it)
            }

            question?.let {
                tvQuestion.text = it
                tvQuestion.show()
            }

            questionResId?.let {
                tvQuestion.setText(it)
                tvQuestion.show()
            }

            if (optionResId != null || optionText != null) {
                btnOption.show()
            } else {
                btnOption.hide()
            }

            if (option1Text != null || option1ResId != null) {
                llOption.show()
                btnOption1.show()
            }

            if (option2Text != null || option2ResId != null) {
                llOption.show()
                btnOption2.show()
            }

            optionText?.let {
                btnOption.text = it
            }

            optionResId?.let {
                btnOption.setText(it)
            }

            option1Text?.let {
                btnOption1.text = it
            }

            option1ResId?.let {
                btnOption1.setText(it)
            }

            option2Text?.let {
                btnOption2.text = it
            }

            option2ResId?.let {
                btnOption2.setText(it)
            }

            if (!title.isNullOrEmpty() || titleResId != null) {
                if (isShowBtnClose) {
                    ivClose.show()
                } else {
                    ivClose.hide()
                }
            } else {
                if (isShowBtnClose) {
                    tvTitle.text = ""
                    tvTitle.show()
                    ivClose.show()
                    ivClose.expandClickArea()
                } else {
                    ivClose.hide()
                }
            }

            btnOption.setOnClickListener { onOptionClicked?.invoke();dismiss() }
            btnOption1.setOnClickListener { onOption1Clicked?.invoke();dismiss() }
            btnOption2.setOnClickListener { onOption2Clicked?.invoke();dismiss() }
            tvQuestion.setOnClickListener {
                it.hide()
                tvMessage.hide()
                tvMessage2.hide()
                tvMessage3.show()

                title2?.let {
                    tvTitle.text = it
                    tvTitle.show()
                }

                titleResId2?.let {
                    tvTitle.setText(it)
                    tvTitle.show()
                }
            }
            ivClose.setOnClickListener { onOptionCloseClicked?.invoke(); dismiss() }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }
}