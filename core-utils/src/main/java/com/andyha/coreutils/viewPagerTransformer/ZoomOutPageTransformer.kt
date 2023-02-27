package com.andyha.coreutils.viewPagerTransformer

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs


class ZoomOutPageTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val pageWidth = page.width
        val pageHeight = page.height
        when {
            position < -1 -> {
                // This page is way off-screen to the left.
//                page.alpha = 0f
            }
            position <= 1 -> {
                // Modify the default slide transition to shrink the page as well
                val scaleFactor = 1 - (1 - MIN_SCALE) * abs(position)
                val verticalMargin = pageHeight * (1 - scaleFactor) / 2
                val horizontalMargin = pageWidth * (1 - scaleFactor) / 2
                if (position < 0) {
                    page.translationX = horizontalMargin - verticalMargin / 2
                } else {
                    page.translationX = -horizontalMargin + verticalMargin / 2
                }

                // Scale the page down ( between MIN_SCALE and 1 )
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor

                // Fade the page relative to its size.
                page.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
            }
            else -> {
                // This page is way off-screen to the right.
//                page.alpha = 0f
            }
        }
    }

    companion object {
        private const val MIN_SCALE = 0.236f
        private const val MIN_ALPHA = 0.236f
    }
}