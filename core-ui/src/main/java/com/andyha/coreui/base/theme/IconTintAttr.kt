package com.andyha.coreui.base.theme

import com.andyha.coreui.R


enum class IconTintAttr(val value: Int, val attr: Int) {
    Primary(1, R.attr.colorIconTintPrimary),
    Secondary(2, R.attr.colorIconTintSecondary),
    Tertiary(3, R.attr.colorIconTintTertiary);

    companion object {
        private val map = IconTintAttr.values().associateBy(IconTintAttr::value)
        fun getIconTintAttr(value: Int): Int? = map[value]?.attr
    }
}