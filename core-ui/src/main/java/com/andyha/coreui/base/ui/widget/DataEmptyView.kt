package com.andyha.coreui.base.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.andyha.coreui.R
import com.andyha.coreui.databinding.ViewDataEmptyBinding


class DataEmptyView : LinearLayout {
    private var emptyTitle: String? = null
    private var emptyMessage: String? = null
    var viewBinding: ViewDataEmptyBinding =
        ViewDataEmptyBinding.inflate(LayoutInflater.from(context), this, true)


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DataEmpty, 0, 0)

        try {
            emptyTitle = a.getString(R.styleable.DataEmpty_emptyTitle)
            emptyMessage = a.getString(R.styleable.DataEmpty_emptyMessage)
        } finally {
            a.recycle()
        }
    }

    init {
        isVisible = false
    }

    fun showEmptyMessage() {
        isVisible = true
        emptyTitle?.let { viewBinding.dataEmptyTitle.text = it }
        emptyMessage?.let { viewBinding.dataEmptyMessage.text = it }
    }

    fun showSearchNotFoundMessage() {
        isVisible = true
        viewBinding.dataEmptyTitle.setText(R.string.common_data_empty_title)
        viewBinding.dataEmptyMessage.setText(R.string.common_data_empty_message)
    }

    fun showCustomMessage(title: String, message: String) {
        isVisible = true
        viewBinding.dataEmptyTitle.text = title
        viewBinding.dataEmptyMessage.text = message
    }

    fun hide() {
        isVisible = false
    }
}