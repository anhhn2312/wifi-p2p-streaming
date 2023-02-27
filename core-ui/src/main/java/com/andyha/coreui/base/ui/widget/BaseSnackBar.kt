package com.andyha.coreui.base.ui.widget

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.andyha.coreextension.dimen
import com.andyha.coreextension.getDimenAttr
import com.andyha.coreui.R
import com.andyha.coreui.databinding.BaseSnackBarBinding
import com.andyha.coreutils.setOnDebounceClick
import com.google.android.material.snackbar.BaseTransientBottomBar
import java.util.*
import kotlin.concurrent.schedule

class BaseSnackBar(
    parent: ViewGroup,
    content: View,
    val action: String? = null,
    contentViewCallback: com.google.android.material.snackbar.ContentViewCallback,
    var dismissTime: Long = LENGTH_MESSAGE,
) : BaseTransientBottomBar<BaseSnackBar>(parent, content, contentViewCallback) {

    private class ContentViewCallback(private val content: View, type: SnackbarType) :
        com.google.android.material.snackbar.ContentViewCallback {
        override fun animateContentIn(delay: Int, duration: Int) {
            content.alpha = 0f
            content.scaleY = 0f
            ViewCompat.animate(content)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(duration.toLong()).startDelay = delay.toLong()
        }

        override fun animateContentOut(delay: Int, duration: Int) {
            content.alpha = 1f
            content.scaleY = 1f
            ViewCompat.animate(content)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(duration.toLong()).startDelay = delay.toLong()
        }
    }

    override fun getAnimationMode(): Int {
        return ANIMATION_MODE_FADE
    }

    override fun show() {
        super.show()
        Timer().schedule(dismissTime) {
            this@BaseSnackBar.dismiss()
        }
    }

    companion object {
        private const val LENGTH_MESSAGE = 2000L // Snackbar with only message dismisses in 2s
        private const val LENGTH_ACTION = 4000L // Snackbar with action dismisses in 4s

        @JvmStatic
        internal fun show(
            viewGroup: ViewGroup,
            messageResId: Int? = null,
            message: String? = null,
            iconResId: Int? = null,
            actionTextResId: Int? = null,
            actionText: String? = null,
            positiveTextResId: Int? = null,
            positiveText: String? = null,
            negativeTextResId: Int? = null,
            negativeText: String? = null,
            type: SnackbarType = SnackbarType.Bottom,
            onActionButtonClicked: (() -> Unit)? = null,
            onPositiveButtonClicked: (() -> Unit)? = null,
            onNegativeButtonClicked: (() -> Unit)? = null
        ) {
            if (messageResId == null && message == null) return

            val messageStr =
                messageResId?.let { viewGroup.context.getString(messageResId) } ?: message

            val actionTextStr =
                actionTextResId?.let { viewGroup.context.getString(it) } ?: actionText

            val positiveTextStr =
                positiveTextResId?.let { viewGroup.context.getString(it) } ?: positiveText

            val negativeTextStr =
                negativeTextResId?.let { viewGroup.context.getString(negativeTextResId) }
                    ?: negativeText

            val parent =
                if (viewGroup is SwipeRefreshLayout || viewGroup is ScrollView)
                    viewGroup.children.first { it is ViewGroup } as ViewGroup
                else viewGroup

            show(
                parent,
                messageStr!!,
                iconResId,
                actionTextStr,
                positiveTextStr,
                negativeTextStr,
                type,
                onActionButtonClicked,
                onPositiveButtonClicked,
                onNegativeButtonClicked
            )
        }

        private fun show(
            parent: ViewGroup,
            message: String,
            iconResId: Int? = null,
            actionText: String? = null,
            positiveText: String? = null,
            negativeText: String? = null,
            type: SnackbarType = SnackbarType.Bottom,
            onActionButtonClicked: (() -> Unit)? = null,
            onPositiveButtonClicked: (() -> Unit)? = null,
            onNegativeButtonClicked: (() -> Unit)? = null,
        ) {
            BaseSnackBarBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
                tvMessage.text = message

                actionText?.let {
                    btnAction.isVisible = true
                    btnAction.text = it
                }

                positiveText?.let {
                    btnPositive.isVisible = true
                    btnPositive.text = it
                    tvMessage.gravity = Gravity.START
                }

                negativeText?.let {
                    btnNegative.isVisible = true
                    btnNegative.text = it
                }

                iconResId?.let {
                    icon.isVisible = true
                    icon.setImageResource(it)
                    tvMessage.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                }

                root.background = ContextCompat.getDrawable(parent.context, type.backgroundRes)

                BaseSnackBar(parent, root, negativeText, ContentViewCallback(root, type)).apply {

                    dismissTime = when {
                        action != null -> LENGTH_ACTION
                        else -> LENGTH_MESSAGE
                    }

                    if (type == SnackbarType.Top) {
                        animationMode = ANIMATION_MODE_FADE

                        val param = view.layoutParams
                        if (param is RelativeLayout.LayoutParams) {
                            param.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                            param.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                            param.topMargin = context.getDimenAttr(android.R.attr.actionBarSize)
                        } else if (param is ConstraintLayout.LayoutParams) {
                            param.bottomToBottom = -1
                            param.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                            param.topMargin = context.getDimenAttr(android.R.attr.actionBarSize)
                        }
                        view.layoutParams = param
                    }

                    view.setBackgroundColor(Color.TRANSPARENT)

                    view.setPadding(
                        context.dimen(R.dimen.dimen8dp),
                        0,
                        context.dimen(R.dimen.dimen8dp),
                        context.dimen(R.dimen.dimen16dp)
                    )

                    duration = LENGTH_INDEFINITE

                    behavior = NoSwipeBehavior()

                    btnAction.setOnDebounceClick {
                        onActionButtonClicked?.invoke()
                        dismiss()
                    }

                    btnPositive.setOnDebounceClick {
                        onPositiveButtonClicked?.invoke()
                        dismiss()
                    }

                    btnNegative.setOnDebounceClick {
                        onNegativeButtonClicked?.invoke()
                        dismiss()
                    }

                    show()
                }
            }
        }
    }
}

enum class SnackbarType(val backgroundRes: Int = R.drawable.bg_common_dialog) {
    Bottom,
    Top,
}

internal class NoSwipeBehavior : BaseTransientBottomBar.Behavior() {
    override fun canSwipeDismissView(child: View) = false
}

// Extension functions -----------------------------------------------------------------------------

fun Fragment.showSnackBar(message: String, type: SnackbarType = SnackbarType.Bottom) {
    (this.view as? ViewGroup)?.let { BaseSnackBar.show(it, message = message, type = type) }
}

fun Fragment.showSnackBar(messageResId: Int, type: SnackbarType = SnackbarType.Bottom) {
    (this.view as? ViewGroup)?.let {
        BaseSnackBar.show(
            it,
            messageResId = messageResId,
            type = type
        )
    }
}

fun Fragment.showSnackBar(
    messageResId: Int? = null,
    message: String? = null,
    iconResId: Int? = null,
    actionTextResId: Int? = null,
    actionText: String? = null,
    positiveTextResId: Int? = null,
    positiveText: String? = null,
    negativeTextResId: Int? = null,
    negativeText: String? = null,
    type: SnackbarType = SnackbarType.Bottom,
    onActionButtonClicked: (() -> Unit)? = null,
    onPositiveButtonClicked: (() -> Unit)? = null,
    onNegativeButtonClicked: (() -> Unit)? = null,
) {
    (this.view as? ViewGroup)?.let {
        BaseSnackBar.show(
            it,
            messageResId,
            message,
            iconResId,
            actionTextResId,
            actionText,
            positiveTextResId,
            positiveText,
            negativeTextResId,
            negativeText,
            type,
            onActionButtonClicked,
            onPositiveButtonClicked,
            onNegativeButtonClicked
        )
    }
}

fun Any.showSnackBar(
    messageResId: Int? = null,
    message: String? = null,
    iconResId: Int? = null,
    actionTextResId: Int? = null,
    actionText: String? = null,
    positiveTextResId: Int? = null,
    positiveText: String? = null,
    negativeTextResId: Int? = null,
    negativeText: String? = null,
    type: SnackbarType = SnackbarType.Bottom,
    onActionButtonClicked: (() -> Unit)? = null,
    onPositiveButtonClicked: (() -> Unit)? = null,
    onNegativeButtonClicked: (() -> Unit)? = null,
) {
    when (true) {
        (this is Fragment) -> {
            this.showSnackBar(
                messageResId,
                message,
                iconResId,
                actionTextResId,
                actionText,
                positiveTextResId,
                positiveText,
                negativeTextResId,
                negativeText,
                type,
                onActionButtonClicked,
                onPositiveButtonClicked,
                onNegativeButtonClicked
            )
        }
        (this is Activity) -> {
            val viewGroup = this.findViewById<ViewGroup>(android.R.id.content)
            BaseSnackBar.show(
                viewGroup,
                messageResId,
                message,
                iconResId,
                actionTextResId,
                actionText,
                positiveTextResId,
                positiveText,
                negativeTextResId,
                negativeText,
                type,
                onActionButtonClicked,
                onPositiveButtonClicked,
                onNegativeButtonClicked
            )
        }
        else -> {
            throw IllegalArgumentException("Ops...This object doesn't support showing snackbar")
        }
    }
}