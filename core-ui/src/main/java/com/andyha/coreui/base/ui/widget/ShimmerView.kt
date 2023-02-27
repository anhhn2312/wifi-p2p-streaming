package com.andyha.coreui.base.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import com.andyha.coreui.R
import com.facebook.shimmer.ShimmerFrameLayout


class ShimmerView : ShimmerFrameLayout {

    private var itemLayout: Int = 0
    private var itemCount: Int = 0
    private var itemDividerDrawable: Drawable? = null
    private var itemDividerColor: Int? = null
    private var itemDividerHeight: Int = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        obtainAttributes(context, attrs, defStyleAttr, defStyleRes)
        init()
    }

    private fun obtainAttributes(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ShimmerView,
            defStyleAttr,
            defStyleRes
        )

        itemLayout = typedArray.getResourceId(R.styleable.ShimmerView_SV_itemLayout, itemLayout)
        itemCount = typedArray.getInteger(R.styleable.ShimmerView_SV_itemCount, itemCount)
        itemDividerColor =
            typedArray.getColor(R.styleable.ShimmerView_SV_itemDividerColor, itemCount)
        itemDividerDrawable = typedArray.getDrawable(R.styleable.ShimmerView_SV_itemDividerDrawable)
        itemDividerHeight = typedArray.getDimensionPixelSize(
            R.styleable.ShimmerView_SV_itemDividerHeight,
            itemDividerHeight
        )

        typedArray.recycle()
    }

    private fun init() {
        addItemInternal()
    }

    private fun addItemInternal() {
        if (itemLayout == 0 || itemCount == 0) {
            return
        }

        val layoutInflater = LayoutInflater.from(context)
        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val dividerLayoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, itemDividerHeight)
        for (i in 0 until itemCount) {
            val item = layoutInflater.inflate(itemLayout, this, false)
            linearLayout.addView(item)

            if (i >= itemCount - 1) {
                continue
            }

            when {
                itemDividerDrawable != null && itemDividerHeight > 0 -> {
                    val divider = View(context)
                    divider.background = itemDividerDrawable
                    linearLayout.addView(divider, dividerLayoutParams)
                }
                itemDividerColor != null && itemDividerHeight > 0 -> {
                    val divider = View(context)
                    divider.setBackgroundColor(itemDividerColor!!)
                    linearLayout.addView(divider, dividerLayoutParams)
                }
                itemDividerDrawable == null && itemDividerHeight > 0 -> {
                    val divider = Space(context)
                    linearLayout.addView(divider, dividerLayoutParams)
                }
            }
        }
        addView(linearLayout)
    }
}