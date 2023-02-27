package com.andyha.coreui.base.ui.widget.recyclerview.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val leftMargin: Int = 0,
    private val topMargin: Int = 0,
    private val rightMargin: Int = 0,
    private val bottomMargin: Int = 0,
    private val excludeItemCount: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val childAdapterPosition = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: Int.MAX_VALUE
        if (childAdapterPosition < 0 || childAdapterPosition >= itemCount - excludeItemCount) {
            return
        }

        outRect.apply {
            this.left = leftMargin
            this.top = topMargin
            this.right = rightMargin
            this.bottom = bottomMargin
        }
    }
}