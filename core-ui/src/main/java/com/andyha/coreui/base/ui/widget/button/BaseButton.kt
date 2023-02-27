package com.andyha.coreui.base.ui.widget.button

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.andyha.coredata.manager.ConfigurationManager
import com.andyha.coreextension.collectLatestSafely
import com.andyha.coreextension.filterNotNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
open class BaseButton : AppCompatButton {

    private var textId: Int? = null
    private var textArgs: Array<out Any?> = arrayOf()
    private var useCustomText = false

    private var coroutineScope: CoroutineScope? = null

    @Inject
    lateinit var configurationManager: ConfigurationManager

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

        val set = intArrayOf(android.R.attr.text)
        val defArray = context.obtainStyledAttributes(attrs, set)

        try {
            textId = defArray.getResourceId(0, 0)
            textId?.let { setCustomText(it) }
        } finally {
            defArray.recycle()
        }
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
                setCustomText(textId, *textArgs)
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
}