package com.andyha.coreui.base.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.andyha.coreui.R
import com.andyha.coreui.base.theme.IconTintAttr
import com.andyha.coreui.base.theme.ThemeManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest


class ThemeRxImageView : AppCompatImageView{

    private var coroutineScope: CoroutineScope? = null

    private var imageTintAttr: Int? = null
        set(value) {
            field = if (value == 0) null
            else value
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val styleArray = context.obtainStyledAttributes(attrs, R.styleable.ThemeRx)
        imageTintAttr = styleArray.getInt(R.styleable.ThemeRx_TRX_iconTint, IconTintAttr.Primary.value)
        styleArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        onThemeChanged()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            ThemeManager.onThemeChanged.collectLatest { onThemeChanged() }
        } ?: kotlin.run {
            coroutineScope = CoroutineScope(Dispatchers.Main + Job()).apply {
                launch {
                    ThemeManager.onThemeChanged.collectLatest { onThemeChanged() }
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        coroutineScope?.cancel()
    }

    private fun onThemeChanged() {
        imageTintAttr?.let {
            ThemeManager.getIconTint(context, it)?.let {
                imageTintList = it
            }
        }
    }
}