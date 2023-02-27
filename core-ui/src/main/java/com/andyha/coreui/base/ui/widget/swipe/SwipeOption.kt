package com.andyha.coreui.base.ui.widget.swipe

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.andyha.coreui.R

class SwipeOption : AppCompatImageView {

    private var normalBackgroundTint: Int? = null
    private var dimBackgroundTint: Int? = null

    private fun getDimColor(): Int {
        if (dimBackgroundTint != null && dimBackgroundTint != -1) {
            return dimBackgroundTint!!
        }
        return context.getColor(R.color.material_grey_500)
    }

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SwipeOption, 0, 0)
        try {
            normalBackgroundTint = array.getColor(R.styleable.SwipeOption_normalBackgroundTint, -1)
            dimBackgroundTint = array.getColor(R.styleable.SwipeOption_dimBackgroundTint, -1)
        } finally {
            array.recycle()
        }
    }

    init {
        background = ColorDrawable(ContextCompat.getColor(context, R.color.white_op_70))
        foreground = kotlin.run {
            val outValue = TypedValue()
            context.theme.resolveAttribute(R.attr.defaultRectRippleBackground, outValue, true)
            AppCompatResources.getDrawable(context, outValue.resourceId)
        }
        scaleType = ScaleType.CENTER_INSIDE
    }

    companion object {
        @JvmStatic
        fun getColor(option: SwipeOption): Int {
            return if (option.isEnabled) {
                option.normalBackgroundTint ?: option.getDimColor()
            } else option.getDimColor()
        }

        @JvmStatic
        fun create(
            context: Context,
            icon: Int,
            backgroundTint: Int = R.color.white_op_20
        ): SwipeOption {
            val option = SwipeOption(context)
            option.setImageResource(icon)
            option.backgroundTintList = ColorStateList.valueOf(context.getColor(backgroundTint))
            return option
        }
    }
}