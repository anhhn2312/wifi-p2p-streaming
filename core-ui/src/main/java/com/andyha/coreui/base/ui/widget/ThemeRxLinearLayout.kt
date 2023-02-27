package com.andyha.coreui.base.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.andyha.coreui.R
import com.andyha.coreui.base.theme.ThemeManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest


class ThemeRxLinearLayout : LinearLayout {

    private var coroutineScope: CoroutineScope? = null

    private var backgroundDrawableAttr: Int? = null
        set(value) {
            field = if (value == 0) null
            else value
        }

    private var backgroundColorAttr: Int? = null
        set(value) {
            field = if (value == 0) null
            else value
        }

    private var backgroundResId: Int? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val styleArray = context.obtainStyledAttributes(attrs, R.styleable.ThemeRx)
        backgroundDrawableAttr = styleArray.getInt(R.styleable.ThemeRx_TRX_backgroundDrawable, 0)
        backgroundColorAttr = styleArray.getInt(R.styleable.ThemeRx_TRX_backgroundColor, 0)
        styleArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setBackground()
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
        setBackground()
    }

    override fun setBackgroundResource(resid: Int) {
        super.setBackgroundResource(resid)
        this.backgroundResId = resid
    }

    private fun setBackground() {
        backgroundDrawableAttr?.let {
            ThemeManager.getBackgroundDrawawble(context, it)?.let {
                background = it
            }
        } ?: backgroundColorAttr?.let {
            ThemeManager.getBackgroundColor(context, it)?.let {
                setBackgroundColor(it)
            }
        } ?: backgroundResId?.let {
            background = ContextCompat.getDrawable(context, it)
        }
    }
}