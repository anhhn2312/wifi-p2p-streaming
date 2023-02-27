package com.andyha.coreui.base.ui.widget.textview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

open class BaseTextView : AppCompatTextView{
    private var textId: Int? = null
    private var hintId: Int? = null
    private var textArgs: Array<out Any?> = arrayOf()
    private var hintArgs: Array<out Any?> = arrayOf()
    private var useCustomText = false

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        includeFontPadding = false

        val set = intArrayOf(android.R.attr.text, android.R.attr.hint)
        val defArray = context.obtainStyledAttributes(attrs, set)

        try {
            textId = defArray.getResourceId(0, 0)
            hintId = defArray.getResourceId(1, 0)
            textId?.let { setCustomText(it) }
            hintId?.let { setCustomHint(it) }
        } finally {
            defArray.recycle()
        }
    }

    fun setCustomText(textId: Int?, vararg formatArgs: Any?) {
        if (textId != null && textId != 0) {
            val newText = if (formatArgs.isEmpty()) {
                context.getString(textId)
            } else {
                context.getString(textId, *formatArgs)
            }
            if (text != newText) {
                this.textId = textId
                textArgs = formatArgs
                useCustomText = true
                text = newText
                useCustomText = false
            }
        }
    }

    fun setCustomHint(hintId: Int?, vararg formatArgs: Any?) {
        if (hintId != null && hintId != 0) {
            val newHint = if (formatArgs.isEmpty()) {
                context.getString(hintId)
            } else {
                context.getString(hintId, *formatArgs)
            }
            if (hint != newHint) {
                this.hintId = hintId
                hintArgs = formatArgs
                hint = context.getString(hintId, *formatArgs)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setCustomText(textId, *textArgs)
        setCustomHint(hintId, *hintArgs)
    }

    private fun reset() {
        textId = 0
        textArgs = emptyArray()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (!useCustomText) {
            reset()
        }
        super.setText(text, type)
    }
}