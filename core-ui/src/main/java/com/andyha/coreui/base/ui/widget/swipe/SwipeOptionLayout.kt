package com.andyha.coreui.base.ui.widget.swipe

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import com.andyha.coreextension.dimen
import com.andyha.coreui.R
import com.andyha.coreui.databinding.SwipeOptionLayoutBinding
import kotlin.math.abs

class SwipeOptionLayout : RelativeLayout {
    val viewBinding: SwipeOptionLayoutBinding by lazy { SwipeOptionLayoutBinding.bind(this) }

    var state = 0
        set(value) {
            isLeftToRight = state == ItemTouchHelper.END
            viewBinding.swipeOptionRightLayout.isVisible = !isLeftToRight
            viewBinding.swipeOptionLeftLayout.isVisible = isLeftToRight
            field = value
        }
    private var isLeftToRight = false

    private val params by lazy {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        params.weight = 1f
        params.gravity = Gravity.CENTER
        params
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    // will fill from right to left
    fun addLeftOption(option: List<Pair<SwipeOption, OnClickListener>>) {
        for (i in option.indices) {
            val o = option[i]
            o.first.id = Int.MAX_VALUE + i - 20
            o.first.setOnClickListener(o.second)
            viewBinding.swipeOptionRightLayout.addView(o.first, params)
        }
    }

    // will fill from left to right
    fun addRightOption(option: List<Pair<SwipeOption, OnClickListener>>) {
        for (i in option.indices.reversed()) {
            val o = option[i]
            o.first.id = Int.MAX_VALUE - i
            o.first.setOnClickListener(o.second)
            viewBinding.swipeOptionRightLayout.addView(o.first, params)
        }
    }

    fun getActionWidth(): Int {
        val numberOption = if (isLeftToRight) {
            getNumberOptionVisibleLeft()
        } else {
            getNumberOptionVisibleRight()
        }
        return numberOption * context.dimen(R.dimen.swipe_option_shape_size)
    }

    fun setSwipeLayoutWidth(dX: Float, left: Boolean, right: Boolean) {
        if (left) {
            val params = viewBinding.swipeOptionLeftLayout.layoutParams
            params.width = dX.toInt()
            viewBinding.swipeOptionLeftLayout.visibility = View.VISIBLE
            viewBinding.swipeOptionLeftLayout.layoutParams = params
        }
        if (right) {
            val params = viewBinding.swipeOptionRightLayout.layoutParams
            params.width = abs(dX).toInt()
            viewBinding.swipeOptionRightLayout.visibility = View.VISIBLE
            viewBinding.swipeOptionRightLayout.layoutParams = params
        }
    }

    private fun getNumberOptionVisibleRight(): Int {
        var count = 0
        viewBinding.swipeOptionRightLayout.apply {
            for (i in 0 until childCount) {
                val option = getChildAt(0)
                if (option.visibility == View.VISIBLE) count++
            }
        }
        return count
    }

    private fun getNumberOptionVisibleLeft(): Int {
        var count = 0
        viewBinding.swipeOptionLeftLayout.apply {
            for (i in 0 until childCount) {
                val option = getChildAt(0)
                if (option.visibility == View.VISIBLE) count++
            }
        }
        return count
    }

    companion object {
        @JvmStatic
        fun isOptionView(view: View): Boolean {
            return Int.MAX_VALUE - 20 <= view.id && view.id <= Int.MAX_VALUE
        }
    }
}