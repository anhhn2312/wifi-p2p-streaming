package com.andyha.coreui.base.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.andyha.coreui.base.ui.widget.ConnectionErrorView.DisplayMode.PlaceHolder
import com.andyha.coreui.databinding.ConnectionErrorViewBinding
import com.andyha.coreutils.setOnDebounceClick


class ConnectionErrorView : LinearLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    var viewBinding: ConnectionErrorViewBinding =
        ConnectionErrorViewBinding.inflate(LayoutInflater.from(context), this, true)

    var displayMode: DisplayMode = PlaceHolder
        set(value) {
            field = value
            setupView()
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupView()
    }

    private fun setupView() {
        gravity = Gravity.CENTER
        viewBinding.apply {
            btnTryAgain.isVisible = displayMode == PlaceHolder
        }
    }

    fun updateView(title: Int, message: Int) {
        viewBinding.apply {
            tvTitle.setCustomText(title)
            tvMessage.setCustomText(message)
        }
    }

    fun setOnClickRetry(onRetry: () -> Unit) {
        viewBinding.btnTryAgain.setOnDebounceClick {
            onRetry.invoke()
        }
    }

    enum class DisplayMode {
        Dialog, PlaceHolder
    }
}