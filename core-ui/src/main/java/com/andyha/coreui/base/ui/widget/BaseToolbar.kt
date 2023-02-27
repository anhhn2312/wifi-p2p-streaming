package com.andyha.coreui.base.ui.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.andyha.coreextension.utils.DimensionUtils
import com.andyha.coreresource.R
import com.andyha.coreui.databinding.ViewToolbarBinding
import com.andyha.coreutils.setOnDebounceClick

class BaseToolbar : Toolbar {

    private lateinit var viewBinding: ViewToolbarBinding

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    private fun init() {
        viewBinding = ViewToolbarBinding.inflate(LayoutInflater.from(context), this, false)
        this.addView(viewBinding.root)
        (viewBinding.root.layoutParams as LayoutParams).gravity = Gravity.START
    }

    //region Container title
    /**
     * Set title gravity center
     *
     */
    fun setTitleGravity(typeGravity: Int) {
        val params = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.weight = 1.0f
        params.gravity = typeGravity
        viewBinding.llTitle.layoutParams = params
        viewBinding.llTitle.gravity = typeGravity
    }
    //endregion

    //region Title
    /**
     * Set title text
     *
     * @param title: text
     */
    fun setTitleText(title: String?) {
        viewBinding.tvTitleToolbar.text = title
    }

    fun setTitleText(@StringRes title: Int, vararg formatArgs: Any?) {
        viewBinding.tvTitleToolbar.setCustomText(title, *formatArgs)
    }

    fun setBigTitle() {
        viewBinding.tvTitleToolbar.setTextAppearance(R.style.FontExtraBold_Title)
        val params = (viewBinding.tvTitleToolbar.layoutParams as MarginLayoutParams).apply {
            marginStart = 25
        }
        viewBinding.tvTitleToolbar.layoutParams = params
    }

    fun setTitleColor(color: Int) {
        viewBinding.tvTitleToolbar.setTextColor(color)
    }

    fun setEllipsizeTitle(line: Int, truncateAt: TextUtils.TruncateAt) {
        // CR #17701 - With android version low, need set singleLine = true
        viewBinding.tvTitleToolbar.isSingleLine = line == 1
        viewBinding.tvTitleToolbar.setLines(line)
        viewBinding.tvTitleToolbar.ellipsize = truncateAt
    }

    fun getTitleText(): String = viewBinding.tvTitleToolbar.text.toString()

    fun setOnClickTitle(onTitleClickListener: (() -> Unit)? = null) {
        viewBinding.tvTitleToolbar.setOnClickListener {
            onTitleClickListener?.invoke()
        }
    }
    //endregion title

    //region Sub title
    fun setSubTitleText(subTitle: String) {
        viewBinding.tvSubTitleToolbar.text = subTitle
    }

    fun setSubTitleText(subTitle: Int) {
        viewBinding.tvSubTitleToolbar.setCustomText(subTitle)
    }

    fun setSubtitleVisibility(visibility: Int) {
        viewBinding.tvSubTitleToolbar.visibility = visibility
    }

    fun getSubTitleText(): String = viewBinding.tvSubTitleToolbar.text.toString()

    fun setOnClickSubTitle(onSubTitleClickListener: (() -> Unit)? = null) {
        viewBinding.tvSubTitleToolbar.setOnClickListener {
            onSubTitleClickListener?.invoke()
        }
    }
    //endregion title

    //region icon start
    fun setIconStart(resourceId: Int) {
        viewBinding.imvIconStartToolbar.setImageResource(resourceId)
    }

    fun setRotateIconStart(rotation: Float) {
        viewBinding.imvIconStartToolbar.rotation = rotation
    }

    fun getVisibilityIconStart(): Int = viewBinding.imvIconStartToolbar.visibility

    fun setVisibilityIconStart(visibility: Int) {
        viewBinding.imvIconStartToolbar.visibility = visibility
    }

    fun setDisableIconStart() {
        viewBinding.imvIconStartToolbar.apply {
            alpha = 0.3f
            isClickable = false
        }
    }

    fun setEnableIconStart() {
        viewBinding.imvIconStartToolbar.apply {
            alpha = 1.0f
            isClickable = true
        }
    }

    fun setOnClickIconStart(onIconStartClickListener: (() -> Unit)? = null) {
        viewBinding.imvIconStartToolbar.setOnClickListener {
            onIconStartClickListener?.invoke()
        }
    }

    fun setMarginIconStart(margin: Int) {
        val layoutParams = viewBinding.imvIconStartToolbar.layoutParams as LinearLayout.LayoutParams
        layoutParams.marginEnd = DimensionUtils.dpToPx(margin.toFloat()).toInt()
    }
    //endregion icon start

    //region icon end
    fun setIconEnd(resourceId: Int?) {
        resourceId?.let {
            viewBinding.imvIconEndToolbar.setImageResource(resourceId)
        }
    }

    fun setRotateIconEnd(rotate: Float) {
        viewBinding.imvIconEndToolbar.rotation = rotate
    }

    fun setVisibilityIconEnd(visibility: Int) {
        viewBinding.imvIconEndToolbar.visibility = visibility
    }

    fun setOnClickIconEnd(onIconEndClickListener: (() -> Unit)? = null) {
        viewBinding.imvIconEndToolbar.setOnDebounceClick {
            onIconEndClickListener?.invoke()
        }
    }

    fun setMarginIconEnd(margin: Int) {
        val layoutParams = viewBinding.imvIconEndToolbar.layoutParams as LinearLayout.LayoutParams
        layoutParams.marginStart = DimensionUtils.dpToPx(margin.toFloat()).toInt()
    }
    //endregion icon end

    fun setTextIconEnd(@StringRes textIconEnd: Int) {
        viewBinding.tvIconEnd.setText(textIconEnd)
    }

    fun setTextIconEnd(textIconEnd: String) {
        viewBinding.tvIconEnd.text = textIconEnd
    }

    fun setTextIconEndColor(@ColorRes color: Int) {
        viewBinding.tvIconEnd.setTextColor(ContextCompat.getColor(this.context, color))
    }

    fun setTextIconEndEnabled(isEnabled: Boolean) {
        viewBinding.tvIconEnd.isEnabled = isEnabled
    }

    fun setVisibilityTextIconEnd(visibility: Int) {
        viewBinding.tvIconEnd.visibility = visibility
    }

    fun setOnClickTextEnd(onTextEndClickListener: (() -> Unit)? = null) {
        viewBinding.tvIconEnd.setOnClickListener {
            onTextEndClickListener?.invoke()
        }
    }

    fun refresh() {
        viewBinding.tvTitleToolbar.invalidate()
        viewBinding.tvSubTitleToolbar.invalidate()
    }
}