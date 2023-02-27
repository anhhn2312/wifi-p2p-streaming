package com.andyha.coreui.base.theme

import com.andyha.coreui.R


enum class BackgroundDrawableAttr(val value: Int, val backgroundAttr: Int) {
    AppBackground(1, R.attr.defaultAppBackground),
    BorderlessRipple(2, R.attr.defaultBorderlessRippleBackground),
    RectRipple(3, R.attr.defaultRectRippleBackground),
    RoundRectRipple(4, R.attr.defaultRoundRectRippleBackground),
    OvalRipple(5, R.attr.defaultOvalRippleBackground),
    TopItem(6, R.attr.defaultTopItemBackground),
    TopItemNoRipple(7, R.attr.defaultTopItemBackgroundNoRipple),
    MiddleItem(8, R.attr.defaultMiddleItemBackground),
    MiddleItemNoRipple(9, R.attr.defaultMiddleItemBackgroundNoRipple),
    BottomItem(10, R.attr.defaultBottomItemBackground),
    BottomItemNoRipple(11, R.attr.defaultBottomItemBackgroundNoRipple),
    FullItem(12, R.attr.defaultFullItemBackground),
    FullItemNoRipple(13, R.attr.defaultFullItemBackgroundNoRipple),
    ButtonPrimary(14, R.attr.defaultPrimaryButtonBackground);


    companion object {
        private val map = values().associateBy(BackgroundDrawableAttr::value)
        fun getBackgroundDrawableAttr(value: Int): Int? = map[value]?.backgroundAttr
    }
}