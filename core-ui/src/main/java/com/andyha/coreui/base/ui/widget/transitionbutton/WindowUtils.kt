package com.andyha.coreui.base.ui.widget.transitionbutton

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager

object WindowUtils {
    private var displayMetrics: DisplayMetrics? = null
    fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    fun makeStatusbarTransparent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun getWidth(activity: Activity): Int {
        setupDisplayMetrics(activity)
        return displayMetrics!!.widthPixels
    }

    fun getHeight(activity: Activity): Int {
        setupDisplayMetrics(activity)
        return displayMetrics!!.heightPixels
    }

    fun getWidth(context: Context): Int {
        setupDisplayMetrics(context)
        return displayMetrics!!.widthPixels
    }

    fun getHeight(context: Context): Int {
        setupDisplayMetrics(context)
        return displayMetrics!!.heightPixels
    }

    private fun setupDisplayMetrics(activity: Activity) {
        if (displayMetrics == null) displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    }

    private fun setupDisplayMetrics(context: Context) {
        if (displayMetrics == null) displayMetrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        display.getMetrics(displayMetrics)
    }
}