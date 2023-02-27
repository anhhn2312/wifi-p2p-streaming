package com.andyha.coreextension

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.provider.MediaStore
import android.text.*
import android.text.style.*
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.andyha.coreextension.utils.BaseTextUtils
import com.andyha.coreextension.utils.CustomTypefaceSpan
import com.andyha.coreextension.utils.ValidationRegex
import timber.log.Timber
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.KeyStore
import java.text.Normalizer
import java.util.*
import java.util.regex.Pattern


fun SpannableString.setBoldAndColorOfSubstring(
    context: Context,
    @ColorRes color: Int = R.color.color_text_only_button_text,
    substring: String?,
    enableWithoutAccents: Boolean = false,
    enableBoldSubString: Boolean = false,
    enableColorSubString: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
    fromLast: Boolean = false,
    maxItems: Int? = null,
): SpannableString {
    if (substring.isNullOrEmpty()) {
        return this
    }

    try {
        val colorHighLight = ContextCompat.getColor(context, color)
        val indexes = indexesOf(
            substring,
            enableWithoutAccents,
            ignoreCase,
            enableOverlapped,
            fromLast,
            maxItems
        )
        for (start in indexes) {
            if (enableBoldSubString)
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    start + substring.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

            if (enableColorSubString)
                setSpan(
                    ForegroundColorSpan(colorHighLight),
                    start,
                    start + substring.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
        }
        return this
    } catch (e: Exception) {
        Timber.d("exception in setFontSizeOfSubstring, text=$this, substring=$substring")
        return SpannableString("")
    }
}

/**
 * Set different font size for different parts of a string.
 *
 * @return SpannableString
 */
fun SpannableString.setFontSizeOfSubstring(
    context: Context,
    substring: String,
    @DimenRes sizeRes: Int,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
    fromLast: Boolean = false,
    maxItems: Int? = null,
): SpannableString {
    try {
        val indexes = indexesOf(
            substring,
            enableWithoutAccents,
            ignoreCase,
            enableOverlapped,
            fromLast,
            maxItems
        )
        for (start in indexes) {
            setSpan(
                AbsoluteSizeSpan(context.dimen(sizeRes)),
                start,
                start + substring.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return this
    } catch (e: Exception) {
        Timber.d("exception in setFontSizeOfSubstring, text=$this, substring=$substring")
        return SpannableString("")
    }
}

/**
 * Set different font size for substring.
 *
 * @return SpannableString
 */
fun SpannableString.setSubstringCenterView(
    substring: String,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
    fromLast: Boolean = false,
    maxItems: Int? = null,
): SpannableString {
    try {
        val indexes = indexesOf(
            substring,
            enableWithoutAccents,
            ignoreCase,
            enableOverlapped,
            fromLast,
            maxItems
        )
        for (start in indexes) {
            setSpan(
                AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                start,
                start + substring.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return this
    } catch (e: Exception) {
        Timber.d("exception in setFontSizeOfSubstring, text=$this, substring=$substring")
        return SpannableString("")
    }
}

/**
 * Set bold of substring
 *
 * @param context
 * @param substring
 * @return
 */
fun SpannableString.setBoldOfSubstring(
    substring: String?,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
    fromLast: Boolean = false,
    maxItems: Int? = null,
): SpannableString {
    if (substring.isNullOrEmpty()) {
        return this
    }
    try {
        val indexes = indexesOf(
            substring,
            enableWithoutAccents,
            ignoreCase,
            enableOverlapped,
            fromLast,
            maxItems
        )
        for (start in indexes) {
            setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                start + substring.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return this
    } catch (e: Exception) {
        Timber.d("exception in setFontSizeOfSubstring, text=$this, substring=$substring")
        return SpannableString("")
    }
}

fun SpannableString.setFontOfSubstring(substring: String?, typeface: Typeface): SpannableString {
    if (substring.isNullOrEmpty()) {
        return this
    }
    try {
        val indexes: MutableList<Int> = mutableListOf()
        var id = 0
        while (id != -1) {
            id = indexOf(substring, id, true)
            if (id != -1) {
                indexes.add(id)
                id++
            }
        }
        for (start in indexes) {
            setSpan(
                StyleSpan(typeface.style),
                start,
                start + substring.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return this
    } catch (e: Exception) {
        Timber.d("exception in setFontSizeOfSubstring, text=$this, substring=$substring")
        return SpannableString("")
    }
}

fun SpannableString.setClickableOfSubstring(
    substring: String,
    onClick: (() -> Unit)?,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
    fromLast: Boolean = false,
    maxItems: Int? = null,
): SpannableString {
    try {
        val indexes = indexesOf(
            substring,
            enableWithoutAccents,
            ignoreCase,
            enableOverlapped,
            fromLast,
            maxItems
        )

        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                onClick?.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#2E78FF")
                ds.isUnderlineText = false
            }
        }

        for (start in indexes) {
            setSpan(
                clickableSpan,
                start,
                start + substring.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

        }
        return this
    } catch (e: Exception) {
        Timber.d("exception in setClickableOfSubstring, text=$this, substring=$substring")
        return SpannableString("")
    }
}


fun SpannableString.getSpanStringFontWithSubString(
    fontFullText: Typeface,
    fontSubText: Typeface,
    _textFull: String?,
    _subText: String?,
): SpannableString {
    val textFull = _textFull ?: ""
    val subText = _subText ?: ""

    this.setSpan(
        CustomTypefaceSpan("", fontFullText),
        0,
        textFull.length,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    )

    val firstIndex = textFull.indexOf(subText)
    val lastIndex = if (firstIndex != -1) {
        firstIndex + subText.length
    } else {
        0
    }

    if (firstIndex != -1) {
        this.setSpan(
            CustomTypefaceSpan("", fontSubText),
            firstIndex,
            lastIndex,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
    }
    return this
}

fun SpannableString.setItalicOfSubstring(
    substring: String,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
    fromLast: Boolean = false,
    maxItems: Int? = null,
): SpannableString {
    try {
        val indexes = indexesOf(
            substring,
            enableWithoutAccents,
            ignoreCase,
            enableOverlapped,
            fromLast,
            maxItems
        )
        for (start in indexes) {
            setSpan(
                StyleSpan(Typeface.ITALIC),
                start,
                start + substring.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return this
    } catch (e: Exception) {
        Timber.d("exception in setFontSizeOfSubstring, text=$this, substring=$substring")
        return SpannableString("")
    }
}

private fun String.newEmptyKeyStore(): KeyStore? {
    return KeyStore.getInstance(KeyStore.getDefaultType())
}

fun String.capitalizeFirstChar(): String {
    return this.split(" ").joinToString(" ") { it.capitalize() }.trimEnd()
}

fun String.replaceAllNewLines(prefix: String = " "): String {
    return this.replace("\\r?\\n|\\r".toRegex(), prefix)
}

fun String?.underline(): SpannableString =
    SpannableString(this).apply {
        setSpan(
            UnderlineSpan(),
            0,
            length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

fun SpannableString.setColorOfSubstring(
    context: Context,
    substring: String?,
    @ColorRes color: Int,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
    fromLast: Boolean = false,
    maxItems: Int? = null,
): SpannableString {
    if (substring.isNullOrEmpty()) {
        return this
    }
    try {
        val indexes = indexesOf(
            substring,
            enableWithoutAccents,
            ignoreCase,
            enableOverlapped,
            fromLast,
            maxItems
        )
        for (start in indexes) {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, color)),
                start,
                start + substring.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return this
    } catch (e: Exception) {
        Timber.d("exception in setColorOfSubstring, text=$this, substring=$substring")
        return this
    }
}

fun String.findIndex(fromLast: Boolean): ((String, Int, Boolean) -> Int) =
    if (fromLast) ::lastIndexOf else ::indexOf

fun SpannableString.firstIndexOf(
    subStr: String?,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
): Int? {
    return indexesOf(
        subStr,
        enableWithoutAccents,
        ignoreCase,
        enableOverlapped,
        false,
        1
    ).firstOrNull()
}

fun SpannableString.lastIndexOf(
    subStr: String?,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
): Int? {
    return indexesOf(
        subStr,
        enableWithoutAccents,
        ignoreCase,
        enableOverlapped,
        true,
        1
    ).lastOrNull()
}

fun SpannableString.indexesOf(
    subStr: String?,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
    fromLast: Boolean = false,
    maxItems: Int? = null,
): List<Int> {
    return this.toString()
        .indexesOf(subStr, enableWithoutAccents, ignoreCase, enableOverlapped, fromLast, maxItems)
}

fun CharSequence.firstIndexOf(
    subStr: String?,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
): Int? {
    return indexesOf(
        subStr,
        enableWithoutAccents,
        ignoreCase,
        enableOverlapped,
        false,
        1
    ).firstOrNull()
}

fun CharSequence.lastIndexOf(
    subStr: String?,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
): Int? {
    return indexesOf(
        subStr,
        enableWithoutAccents,
        ignoreCase,
        enableOverlapped,
        true,
        1
    ).lastOrNull()
}

fun CharSequence.indexesOf(
    subStr: String?,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
    fromLast: Boolean = false,
    maxItems: Int? = null,
): List<Int> {
    return this.toString()
        .indexesOf(subStr, enableWithoutAccents, ignoreCase, enableOverlapped, fromLast, maxItems)
}

fun String.firstIndexOf(
    subStr: String?,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
): Int? {
    return indexesOf(
        subStr,
        enableWithoutAccents,
        ignoreCase,
        enableOverlapped,
        false,
        1
    ).firstOrNull()
}

fun String.lastIndexOf(
    subStr: String?,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
): Int? {
    return indexesOf(
        subStr,
        enableWithoutAccents,
        ignoreCase,
        enableOverlapped,
        true,
        1
    ).lastOrNull()
}

fun String.indexesOf(
    subStr: String?,
    enableWithoutAccents: Boolean = false,
    ignoreCase: Boolean = true,
    enableOverlapped: Boolean = true,
    fromLast: Boolean = false,
    maxItems: Int? = null,
): List<Int> {
    val list = mutableListOf<Int>()

    if (subStr.isNullOrBlank()) return list

    try {
        val orgSrc = this.toString()
        val engSrc = BaseTextUtils.convertToEnglish(orgSrc)
        val engSubStr = BaseTextUtils.convertToEnglish(subStr)

        val step = when {
            enableOverlapped && !fromLast -> 1
            enableOverlapped && fromLast -> -1
            !enableOverlapped && !fromLast -> subStr.length
            else -> -(subStr.length)
        }

        var i = (if (fromLast) length else -1) - step // initial index for first round

        while (true) {
            val iOrg = orgSrc.findIndex(fromLast)(subStr, i + step, ignoreCase)
            val iEng = engSrc.findIndex(fromLast)(engSubStr, i + step, ignoreCase)

            when {
                iEng != -1 && enableWithoutAccents -> {
                    i = iEng
                    list.add(iEng)
                }
                iOrg != -1 -> {
                    i = iOrg
                    list.add(iOrg)
                }
                else -> {
                    return list
                }
            }

            if (maxItems != null && list.size == maxItems) {
                return list
            }
        }

    } catch (e: Exception) {
        Timber.d("exception in indexOf, text=$this, substring=$subStr")
        return list
    }
}


fun String.isValidEmail(
    context: Context,
    acceptEmpty: Boolean = false,
    fieldTitle: String? = null,
    resultMessage: (String) -> Unit
): Boolean {
    return when {
        this.isEmpty() -> { acceptEmpty }

        !Pattern.matches(ValidationRegex.REGEX_EMAIL, this) -> false

        else -> true
    }
}

fun String.isValidPassword(
    context: Context,
    fieldTitle: String? = null,
): Boolean {
    return when {
        this.isEmpty() -> {
            false
        }

        this.length < ValidationRegex.LIMIT_SHORTEST_PASSWORD_LENGTH -> {
            false
        }

        Pattern.matches(ValidationRegex.REGEX_PASS_WORD_CONTAIN_NUMBER, this)
                && Pattern.matches(ValidationRegex.REGEX_PASS_WORD_CONTAIN_LOWER_CASE, this)
                && Pattern.matches(ValidationRegex.REGEX_PASS_WORD_CONTAIN_UPPER_CASE, this) -> {
            true
        }
        else -> false
    }
}

fun String.convertToLocale(): Locale {
    return try {
        val tag = this.replace("_", "-")
        Locale.forLanguageTag(tag)
    } catch (e: Exception) {
        Locale(this)
    }
}

fun String?.removeImageFileByPath(context: Context) {
    this?.let {
        try {
            val imageCrop = File(it)
            if (imageCrop.exists()) {
                val result = imageCrop.delete()
                Timber.d("Delete file \"$this\" result: $result")
                if (result) {
                    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val contentResolver = context.contentResolver
                    val url = MediaStore.Images.Media.DATA + "=?"
                    val deleteRows = contentResolver.delete(uri, url, arrayOf(it))
                    Timber.d("Delete image data \"$this\" result: $deleteRows")
                }
            }
        } catch (er: Exception) {

        }
    } ?: kotlin.run {
        Timber.d("The image path is null")
    }
}

fun String.unAccent(): String {
    val output = StringBuffer()
    val pattern = Pattern.compile(
        "(?:[${ValidationRegex.VIETNAMESE_DIACRITIC_CHARACTERS}]|[A-Z])++",
        Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE
    )
    val m = pattern.matcher(Normalizer.normalize(this, Normalizer.Form.NFD))
    while (m.find()) {
        output.append(m.group())
    }
    return if (output.toString().isNotEmpty()) output.toString().replace(" ", "") else this
}

fun Array<String?>?.convertToString(): String? {
    var result: String? = null
    if (this.isNullOrEmpty()) {
        return result
    }
    this.forEach {
        it?.let { content ->
            result = if (result.isNullOrEmpty()) {
                content
            } else {
                result.plus(", ").plus(content)
            }
        }
    }
    return result
}

fun String?.formatTitleToMessage(): String? {
    if (this?.endsWith("*") == true) {
        return this.substring(0, this.length - 1)
    }
    return this
}

/**
 * issue: https://stackoverflow.com/questions/68621404/android-12-beta-cannot-view-pdf-link-with-customtabsintent
 * issue: https://stackoverflow.com/questions/63250795/pdf-no-preview-available-in-android-webview
 */
fun String.makeGoogleDocPreviewUrl(): String {
    //Url Convert to UTF-8 It important.
    val encodeUrl = try {
        URLEncoder.encode(this, "UTF-8")
    } catch (ex: UnsupportedEncodingException) {
        this
    }
    return "https://docs.google.com/gview?embedded=true&url=$encodeUrl"
}
