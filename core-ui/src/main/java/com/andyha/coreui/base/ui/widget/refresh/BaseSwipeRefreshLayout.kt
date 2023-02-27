package com.andyha.coreui.base.ui.widget.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class BaseSwipeRefreshLayout(context: Context, attrs: AttributeSet?) :
    SwipeRefreshLayout(context, attrs) {
    private var startX = 0f
    private var startY = 0f
    private var mIsXMove = false // whether to drag horizontally
    private val mTouchSlop: Int // getScaledTouchSlop() gets a distance, indicating that when the slide is moved, the gesture moves more than this distance to start moving the control. ViewPager uses this distance to determine whether the user has turned the page.

    constructor(context: Context) : this(context, null)

    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsXMove = false
                startX = ev.x
                startY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                // If the horizontal movement is not intercepted, directly return false;
                if (mIsXMove) {
                    return false
                }
                val endX = ev.x
                val endY = ev.y
                val distanceX = Math.abs(endX - startX)
                val distanceY = Math.abs(endY - startY)
                // If dx>xy, it is considered to be swiping left and right, and the event is handed over to viewPager for processing, return false
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsXMove = true
                    return false
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> mIsXMove = false
        }
        // If dy>dx, it is considered as a drop-down event, handed over to swipeRefreshLayout for processing and interception
        return super.onInterceptTouchEvent(ev)
    }
}