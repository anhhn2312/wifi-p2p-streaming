package com.andyha.coreui.base.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.widget.LinearLayout
import com.andyha.coreui.R
import kotlin.math.roundToInt


class MaxHeightLinearLayout : LinearLayout {
    private var maxHeightDp = 0
    private var maxHeightPx = 0

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typeArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.MaxHeightLayout, 0, 0)
        try {
            maxHeightDp = typeArray.getInteger(R.styleable.MaxHeightLayout_maxHeightDp, 0)
            maxHeightPx = typeArray.getInteger(R.styleable.MaxHeightLayout_maxHeightPx, 0)
        } finally {
            typeArray.recycle()
        }
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun dpToPx(dp: Int): Int {
        val displayMetrics: DisplayMetrics? = context?.resources?.displayMetrics
        displayMetrics?.let {
            return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
        } ?: kotlin.run {
            return 0
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        val maxHeightPx = if (maxHeightDp > 0) {
            dpToPx(maxHeightDp)
        } else {
            this.maxHeightPx
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeightPx, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setMaxHeightDp(maxHeightDp: Int) {
        this.maxHeightDp = maxHeightDp
        invalidate()
    }

    fun setMaxHeightPx(maxHeightPx: Int) {
        this.maxHeightPx = maxHeightPx
        invalidate()
    }
}