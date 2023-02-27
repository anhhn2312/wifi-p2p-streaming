package com.andyha.coreui.base.ui.widget.edittext

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.andyha.coredata.manager.ConfigurationManager
import com.andyha.coreextension.collectLatestSafely
import com.andyha.coreextension.filterNotNull
import com.andyha.coreui.R
import com.andyha.coreui.base.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class BaseEditText : AppCompatEditText {
    private var textId = 0
    private var hintId = 0
    private var textArgs: Array<out Any?> = arrayOf()
    private var hintArgs: Array<out Any?> = arrayOf()
    private var useCustomText = false
    var isError = false
        set(value) {
            field = value
            refreshDrawableState()
        }

    private var coroutineScope: CoroutineScope? = null

    @Inject
    lateinit var configurationManager: ConfigurationManager

    constructor(context: Context) : super(context) {}
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            observeLocaleChanged()
        } ?: kotlin.run {
            coroutineScope = CoroutineScope(Dispatchers.Main + Job()).apply {
                launch { observeLocaleChanged() }
            }
        }
    }

    private suspend fun observeLocaleChanged() {
        configurationManager.currentLocale
            .filterNotNull()
            .distinctUntilChanged()
            .collectLatestSafely {
                setCustomHint(hintId, hintArgs)
            }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val set = intArrayOf(android.R.attr.text, android.R.attr.hint)
        val array = context.obtainStyledAttributes(attrs, set)
        try {
            textId = array.getResourceId(0, 0)
            hintId = array.getResourceId(1, 0)
            setCustomText(textId)
            setCustomHint(hintId)
        } finally {
            array.recycle()
        }
    }

    fun setCustomText(textId: Int?, vararg formatArgs: Any?) {
        if (textId != null && textId != 0) {
            val newText = if (formatArgs.isEmpty()) {
                context.getString(textId)
            } else context.getString(textId, *formatArgs)
            if (text?.toString() != newText) {
                this.textId = textId
                textArgs = formatArgs
                useCustomText = true
                setText(newText)
                useCustomText = false
            }
        }
    }

    fun setCustomHint(textId: Int?, vararg formatArgs: Any?) {
        if (textId != null && textId != 0) {
            val newHint = if (formatArgs.isNullOrEmpty()) {
                context.getString(textId)
            } else context.getString(textId, *formatArgs)
            if (hint != newHint) {
                hintId = textId
                hintArgs = formatArgs
                hint = context.getString(textId, *formatArgs)
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
        hintId = 0
        textArgs = emptyArray()
        hintArgs = emptyArray()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (!useCustomText) {
            reset()
        }
        super.setText(text, type)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isError) {
            // We are going to add 1 extra state.
            mergeDrawableStates(drawableState, STATE_ERROR)
        }
        return drawableState
    }

    companion object {
        private val STATE_ERROR = intArrayOf(R.attr.state_error)
    }
}