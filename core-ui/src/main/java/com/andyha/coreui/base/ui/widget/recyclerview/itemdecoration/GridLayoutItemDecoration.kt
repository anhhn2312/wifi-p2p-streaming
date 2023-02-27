package com.andyha.coreui.base.ui.widget.recyclerview.itemdecoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andyha.coreextension.dimen
import com.andyha.coreui.R

class GridLayoutItemDecoration(
    context: Context,
    private val spanCount: Int,
    private val orientation: Int = GridLayoutManager.VERTICAL,
    @DimenRes private val horizontalSpacingDimen: Int = R.dimen.dimen16dp,
    @DimenRes private val verticalSpacingDimen: Int = R.dimen.dimen16dp,
) : RecyclerView.ItemDecoration() {

    private val horizontalSpacing = context.dimen(horizontalSpacingDimen)
    private val verticalSpacing = context.dimen(verticalSpacingDimen)

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount // item column

        if (orientation == GridLayoutManager.HORIZONTAL) {
            outRect.top = column * horizontalSpacing / spanCount
            outRect.bottom = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount
            if (position >= spanCount) {
                outRect.left = verticalSpacing
            }
        } else {
            outRect.left = column * horizontalSpacing / spanCount
            outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount
            if (position >= spanCount) {
                outRect.top = verticalSpacing
            }
        }
    }
}