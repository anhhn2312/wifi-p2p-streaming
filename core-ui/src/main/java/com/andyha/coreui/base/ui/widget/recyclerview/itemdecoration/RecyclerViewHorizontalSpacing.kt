package com.andyha.coreui.base.ui.widget.recyclerview.itemdecoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import com.andyha.coreextension.dimen
import com.andyha.coreui.R

open class RecyclerViewHorizontalSpacing(
    val context: Context,
    @DimenRes val spacingDimen: Int = R.dimen.dimen16dp,
    isReverse: Boolean? = false
) : RecyclerView.ItemDecoration() {
    private val mPadding: Int = context.dimen(spacingDimen)
    private val mIsReverse: Boolean? = isReverse

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }
        if (mIsReverse == true) {
            outRect.left = mPadding
        } else {
            outRect.right = mPadding
        }
    }


}
