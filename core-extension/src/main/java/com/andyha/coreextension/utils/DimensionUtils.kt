package com.andyha.coreextension.utils

import android.content.res.Resources
import android.util.TypedValue

object DimensionUtils {
    /**
     * Convert dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun dpToPx(dp: Float): Float {
        return (dp * Resources.getSystem().displayMetrics.density)
    }

    /**
     * Convert device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    fun pxToDp(px: Float): Float {
        return (px / Resources.getSystem().displayMetrics.density)
    }

    /**
     * Convert devices specific px to sp independent on scaleDensity
     * @param sp A value in sp unit of fontSize. Which we need to convert
     * @return A float value to represent px equivalent to sp value
     */
    fun spToPx(sp: Float): Float {
        return (sp * (Resources.getSystem().displayMetrics.scaledDensity))
    }

    /**
     * Get the width of screen in px
     */
    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    /**
     * Get the height of screen in px
     */
    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }
}

fun Float.dp2Px(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    ).toInt()
}