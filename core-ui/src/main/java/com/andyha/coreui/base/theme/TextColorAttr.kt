package com.andyha.coreui.base.theme

import com.andyha.coreui.R

enum class TextColorAttr(val value: Int, val attr: Int) {
    Primary(1, android.R.attr.textColorPrimary),
    Secondary(2, android.R.attr.textColorSecondary),
    Tertiary(3, android.R.attr.textColorTertiary),
    Error(4, R.attr.textColorError);

    companion object {
        private val map = values().associateBy(TextColorAttr::value)
        fun getTextColorAttr(value: Int): Int? = map[value]?.attr
    }
}