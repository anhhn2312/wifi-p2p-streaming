package com.andyha.coreui.base.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


class NonSwipeableViewPager : ViewPager {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        // Never allow swiping to switch between pages
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Never allow swiping to switch between pages
        return false
    }
}