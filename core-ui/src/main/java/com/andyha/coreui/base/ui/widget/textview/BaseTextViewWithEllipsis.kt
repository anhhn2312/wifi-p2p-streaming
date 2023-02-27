package com.andyha.coreui.base.ui.widget.textview

import android.content.Context
import android.graphics.Canvas
import android.text.SpannableString
import android.util.AttributeSet
import com.andyha.coreextension.firstIndexOf
import com.andyha.coreextension.measureLines
import com.andyha.coreextension.setColorOfSubstring
import com.andyha.coreutils.TextUtils
import timber.log.Timber

class BaseTextViewWithEllipsis : BaseTextView {

    var subStr: String = ""
    var paddingLeftChars: Int = 0
    var enableWithoutAccents: Boolean = false
    var ignoreCase: Boolean = true
    var enableOverlapped: Boolean = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun createTextWithEllipsisSurroundSubText(org: CharSequence): CharSequence {
        val lines = try {
            measureLines(org)
        } catch (e: Exception) {
            Timber.d(e)
            0
        }

        var newText = org

        if (lines > maxLines) {
            val matchedIndex =
                newText.firstIndexOf(subStr, enableWithoutAccents, ignoreCase, enableOverlapped)
            if (matchedIndex != null) {
                val ellipsis = TextUtils.ELLIPSIS_NORMAL
                if (matchedIndex > paddingLeftChars + ellipsis.length) {
                    newText = android.text.TextUtils.concat(
                        SpannableString(ellipsis),
                        newText.subSequence(matchedIndex - paddingLeftChars, newText.length)
                    )
                }
            }
        }

        return newText
    }

    override fun onDraw(canvas: Canvas) {
        val modifiedText = createTextWithEllipsisSurroundSubText(text)
        if (modifiedText.toString() != text.toString()) {
            text = modifiedText
        } else {
            super.onDraw(canvas)
        }
    }

    fun setTextWithEllipsisSurroundSubText(
        textStr: String,
        subStr: String,
        enableWithoutAccents: Boolean = false,
        ignoreCase: Boolean = true,
        enableOverlapped: Boolean = true,
        paddingLeftChars: Int = 0,
        highlightSubStrColor: Int? = null
    ) {
        this.subStr = subStr
        this.enableWithoutAccents = enableWithoutAccents
        this.ignoreCase = ignoreCase
        this.enableOverlapped = enableOverlapped
        this.paddingLeftChars = paddingLeftChars
        text = SpannableString(textStr).apply {
            highlightSubStrColor?.let {
                setColorOfSubstring(
                    context,
                    subStr,
                    it,
                    enableWithoutAccents,
                    ignoreCase,
                    enableOverlapped
                )
            }
        }
    }
}