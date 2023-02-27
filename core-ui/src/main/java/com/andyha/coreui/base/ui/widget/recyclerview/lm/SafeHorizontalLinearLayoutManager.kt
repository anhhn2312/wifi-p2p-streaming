package com.andyha.coreui.base.ui.widget.recyclerview.lm

import android.content.Context
import android.util.AttributeSet


class SafeHorizontalLinearLayoutManager : SafeLinearLayoutManager {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    init {
        orientation = HORIZONTAL
    }
}