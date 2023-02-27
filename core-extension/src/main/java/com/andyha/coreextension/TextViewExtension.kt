package com.andyha.coreextension

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.util.Patterns
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import timber.log.Timber


fun TextView.setFont(@FontRes resId: Int) {
    val context = this.context
    this.typeface = context.getTypeFace(resId)
}

fun TextView.setTextNullable(text: String?) {
    this.text = text.orEmpty()
}

fun TextView.setTextUnknown(text: String?) {
    if (text != null) {
        this.text = text
    } else {
        this.text = "Unknown"
    }
}

fun TextView.setTextNullable(text: Any?) {
    when (text) {
        is String -> this.text = text as String? ?: ""
        is SpannableString -> this.text = text as SpannableString? ?: ""
        else -> this.text = ""
    }
}

fun TextView.underline() {
    paint.flags = paint.flags or Paint.UNDERLINE_TEXT_FLAG
    paint.isAntiAlias = true
}

fun TextView.deleteLine() {
    paint.flags = paint.flags or Paint.STRIKE_THRU_TEXT_FLAG
    paint.isAntiAlias = true
}

fun TextView.bold() {
    paint.isFakeBoldText = true
    paint.isAntiAlias = true
}

fun TextView.setColorOfSubstring(substring: String, color: Int) {
    try {
        val spannable = SpannableString(text)
        val start = text.indexOf(substring)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            start,
            start + substring.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text = spannable
    } catch (e: Exception) {
        Timber.d("exception in setColorOfSubstring, text=$text, substring=$substring")
    }
}


@SuppressLint("SetTextI18n")
fun TextView.setAvatarName(fullName: String?) {
    val userName = fullName?.trim()
    if (userName.isNullOrEmpty() || (userName.trim().isEmpty())) {
        this.text = ""
    } else {
        // Remove extra spaces in the text string of user name, example: " Andy Ha "
        val listStringOfName =
            userName.trim().replace("[ ]+".toRegex(), " ").split(" ")
        when (listStringOfName.size) {
            1 -> {
                this.text =
                    listStringOfName[0].first().uppercase()
            }

            2 -> {
                val firstName =
                    listStringOfName[0].first().uppercase()
                val lastName =
                    listStringOfName[1].first().uppercase()
                this.text = "$firstName$lastName"
            }
            else -> {
                // > 2 characters
                val lastIndex = listStringOfName.lastIndex
                val firstName =
                    listStringOfName[lastIndex - 1].first().uppercase()
                val lastName =
                    listStringOfName[lastIndex].first().uppercase()
                this.text = "$firstName$lastName"

            }
        }
    }
}

fun TextView.addLinkifyPhoneNumber() {
    movementMethod = LinkMovementMethod.getInstance()
    Linkify.addLinks(
        this,
        Patterns.PHONE,
        "tel:",
        Linkify.sPhoneNumberMatchFilter,
        Linkify.sPhoneNumberTransformFilter
    )
    removeLinksUnderline()
}

fun TextView.removeLinksUnderline() {
    val spannable = SpannableString(text)
    try {
        for (u in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
            spannable.setSpan(object : URLSpan(u.url) {
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0)
        }
    } catch (e: RuntimeException) {
        e.printStackTrace()
    }
    text = spannable
}

fun TextView.setCompoundDrawableLeft(drawable: Int) {
    setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
}

fun TextView.setCompoundDrawableTop(drawable: Int) {
    setCompoundDrawablesWithIntrinsicBounds(0, drawable, 0, 0)
}

fun TextView.setCompoundDrawableRight(drawable: Int) {
    setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
}

fun TextView.setCompoundDrawableBottom(drawable: Int) {
    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, drawable)
}

fun TextView.setNoneCompoundDrawable() {
    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
}

/**
 * Measure the number lines of text when rendered on textview
 *
 * @param text
 */
@Throws(UnsupportedOperationException::class)
fun TextView.measureLines(text: CharSequence): Int {
    var lines = 0
    val totalChars = text.length
    var measuredChars = 0

    if (width == 0) throw UnsupportedOperationException("Need to call this function after having layout, width != 0")

    while (measuredChars < totalChars) {
        var charsInNextLine =
            paint.breakText(text, measuredChars, totalChars, true, width.toFloat(), null)

        if (measuredChars + charsInNextLine < totalChars) {
            // check start next line index is in the middle of word or not
            if (text[measuredChars + charsInNextLine].isWhitespace()
                    .not()
            ) { // in the middle of word
                // set start next line index is the nearest whitespace before + 1
                while (charsInNextLine >= 0 && text[measuredChars + charsInNextLine].isWhitespace()
                        .not()
                ) {
                    charsInNextLine--
                }
                charsInNextLine++
            } else {
                // set start next line index is the nearest not whitespace after
                while (measuredChars + charsInNextLine < totalChars && text[measuredChars + charsInNextLine].isWhitespace()) {
                    charsInNextLine++
                }
            }
        }
        lines++
        measuredChars += charsInNextLine
    }

    return lines
}

fun TextView.setTextAppearanceTypeFace(
    typeface: Typeface?,
    @ColorRes textColor: Int
) {
    this.setTypeface(typeface, Typeface.BOLD)
    this.setTextColor(
        ContextCompat.getColor(
            this.context,
            textColor
        )
    )
}