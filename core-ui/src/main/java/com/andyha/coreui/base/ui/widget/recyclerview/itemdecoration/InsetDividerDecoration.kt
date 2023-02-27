package com.andyha.coreui.base.ui.widget.recyclerview.itemdecoration

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

/**
 * A decoration which draws a horizontal divider between [RecyclerView.ViewHolder]s of a given
 * type; with a left inset.
 * this class adopted from Plaid
 */
class InsetDividerDecoration(
    private val height: Int,
    @ColorInt private val dividerColor: Int,
    private val excludeLastItemCount: Int = 1,
    private val startPadding: Int = 0,
    private val endPadding: Int = 0
) : RecyclerView.ItemDecoration() {

    private val mDivider = GradientDrawable()
    private val mBounds = Rect()

    init {
        mDivider.setColor(dividerColor)
        mDivider.setSize(0, height)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager != null) {
            this.drawVertical(c, parent)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }

        val childCount = parent.childCount

        for (i in 0 until childCount - excludeLastItemCount) {
            val child = parent.getChildAt(i)
            // Only draw divider when child is drawn
            if (child.height != 0) {
                parent.getDecoratedBoundsWithMargins(child, this.mBounds)
                val bottom = this.mBounds.bottom + Math.round(child.translationY)
                val top = bottom - this.mDivider.intrinsicHeight
                this.mDivider.setBounds(left + startPadding, top, right - endPadding, bottom)
                this.mDivider.draw(canvas)
            }
        }

        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(0, 0, 0, height)
    }
}