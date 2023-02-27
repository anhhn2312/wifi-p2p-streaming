package com.andyha.coreextension

import androidx.core.graphics.ColorUtils


fun Int.setAlpha(alpha: Float): Int {
    var colorAlpha = alpha
    if (colorAlpha < 0) colorAlpha = 0f
    else if (colorAlpha > 255) colorAlpha = 255f
    return ColorUtils.setAlphaComponent(this, colorAlpha.toInt())
}