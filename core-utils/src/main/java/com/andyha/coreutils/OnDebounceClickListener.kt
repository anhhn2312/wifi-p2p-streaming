package com.andyha.coreutils

import android.os.Handler
import android.view.View
import timber.log.Timber


abstract class OnDebounceClickListener(private val disableAfterClick: Boolean) : View.OnClickListener {

    private var mHandler: Handler = Handler()
    private var mLastClickTime: Long = 0

    override fun onClick(v: View) {
        val lastClickTime = mLastClickTime
        val now = System.currentTimeMillis()
        mLastClickTime = now
        if (now - lastClickTime < MIN_DELAY_MS) {
            // Too fast: ignore
            Timber.d("clicked too quickly: ignored")
        } else {
            // Register click
            onDebounceClicked(v)
            disableAfterClick(v, disableAfterClick)
        }
    }

    private fun disableAfterClick(view: View, disableAfterClick: Boolean) {
        if (!disableAfterClick) {
            return
        }

        view.isEnabled = false
        mHandler.postDelayed({ view.isEnabled = true }, RE_ENABLE_TIME)
    }

    abstract fun onDebounceClicked(v: View)

    companion object {
        private const val MIN_DELAY_MS: Long = 600
        private const val RE_ENABLE_TIME: Long = 350

        fun wrap(disableAfterClick: Boolean, onClick: (v: View) -> Unit): OnDebounceClickListener {
            return object : OnDebounceClickListener(disableAfterClick) {
                override fun onDebounceClicked(v: View) {
                    onClick(v)
                }
            }
        }
    }
}

fun View.setOnDebounceClick(onClick: (v: View) -> Unit) {
    this.setOnClickListener(OnDebounceClickListener.wrap(false, onClick))
}

fun View.setOnDebounceClick(disableAfterClick: Boolean, onClick: (v: View) -> Unit) {
    this.setOnClickListener(OnDebounceClickListener.wrap(disableAfterClick, onClick))
}