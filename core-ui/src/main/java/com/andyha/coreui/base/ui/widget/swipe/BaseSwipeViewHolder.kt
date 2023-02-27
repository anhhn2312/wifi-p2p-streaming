package com.andyha.coreui.base.ui.widget.swipe

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.andyha.coreui.R

// extend this class, override getRightOption(), getLeftOption and containerLayout
// layout need include swipe_option_layout

abstract class BaseSwipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val swipeLayout = view.findViewById<SwipeOptionLayout?>(R.id.swipe_option_layout)

    protected var state = 0
    protected var curDir: Int = 0
    private var swipeEnableItem = true

    init {
        createSwipeOption()
    }

    open fun getActionWidth(): Float {
        return swipeLayout.getActionWidth().toFloat()
    }

    fun createSwipeOption() {
        swipeLayout?.addRightOption(getRightOption())
        swipeLayout?.addLeftOption(getLeftOption())
    }

    open fun getRightOption(): List<Pair<SwipeOption, View.OnClickListener>> = arrayListOf()
    open fun getLeftOption(): List<Pair<SwipeOption, View.OnClickListener>> = arrayListOf()

    open fun isSwipeEnable() = swipeEnableItem

    open fun progress(dX: Float) {
        swipeLayout?.setSwipeLayoutWidth(dX, dX >= 0, dX <= 0)
    }

    open fun supportSwipeType() = ItemTouchHelper.START or ItemTouchHelper.END

    abstract fun getContainerLayout(): View
}