package com.andyha.coreui.base.ui.widget.recyclerview.itemdecoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import com.andyha.coreextension.dimen
import com.andyha.coreui.R

open class RecyclerViewVerticalSpacing(
    val context: Context,
    @DimenRes spacingDimen: Int = R.dimen.dimen16dp,
    @DimenRes val topOffset: Int? = null,
    @DimenRes val bottomOffset: Int? = null
) :
    RecyclerView.ItemDecoration() {
    private val mPadding: Int = context.dimen(spacingDimen)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION && itemPosition != 0) {
            return
        }

        outRect.top = mPadding

        topOffset?.let { if (itemPosition == 0) outRect.top = context.dimen(it) }

        bottomOffset?.let {
            if (itemPosition == parent.adapter?.itemCount?.minus(1))
                outRect.bottom = context.dimen(it)
        }
    }
}
