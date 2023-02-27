package com.andyha.coreui.base.ui.widget.tablayout

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.andyha.coreextension.getColorAttr
import com.andyha.coreextension.localehelper.currentLocale
import com.andyha.coreui.R


open class BaseTabLayout : LinearLayout {

    var layoutBackgroundColor: Int = 0
        set(value) {
            if (field != value) {
                field = value
                initBackground()
            }
        }
    var layoutBackgroundRadius: Float = 0F
        set(value) {
            if (field != value) {
                field = value
                initBackground()
            }
        }
    var tabTextAppearance: Int = 0
        set(value) {
            if (field != value) {
                field = value
                invalidateTextAppearanceTab()
            }
        }
    var tabBackgroundRadius: Float = 0F
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var selectedTabTextAppearance: Int = 0
        set(value) {
            if (field != value) {
                field = value
                invalidateTextAppearanceTab()
            }
        }
    var selectedTabBackgroundColor: Int = 0
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var selectedTabBackgroundMargin: Int = 0
        set(value) {
            if (field != value) {
                field = value
                measureMaterials()
                invalidate()
            }
        }
    var changeTabAnimationDuration: Long = 0
        set(value) {
            if (field != value) {
                field = value
                mChangeTabAnimator = null
            }
        }
    var changeTabAnimationInterpolator: Int = 0
        set(value) {
            if (field != value) {
                field = value
                mChangeTabAnimator = null
            }
        }

    private lateinit var mSelectedTabPaint: Paint

    private var mSelectedTabPosition: Int = 0
    private val mTabs: ArrayList<Tab> = arrayListOf()
    private var mOnTabSelectedListener: ((Int) -> Unit)? = null

    private var mTabWidth: Int = 0
    private var mSelectedTabBackgroundRect: RectF = RectF()
    private var mSelectedTabStartLeft: Float = 0F
    private var mChangeTabDistance = 0

    private var mChangeTabAnimator: ValueAnimator? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.BaseTabLayoutStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        R.style.BaseTabLayoutStyle
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
            R.styleable.BaseTabLayout,
            defStyleAttr,
            defStyleRes
        )

        layoutBackgroundColor = typedArray.getColor(
            R.styleable.BaseTabLayout_bt_layoutBackgroundColor,
            layoutBackgroundColor
        )
        layoutBackgroundRadius = typedArray.getDimension(
            R.styleable.BaseTabLayout_bt_layoutBackgroundRadius,
            layoutBackgroundRadius
        )
        tabBackgroundRadius = typedArray.getDimension(
            R.styleable.BaseTabLayout_bt_tabBackgroundRadius,
            tabBackgroundRadius
        )
        tabTextAppearance =
            typedArray.getResourceId(R.styleable.BaseTabLayout_bt_tabTextAppearance, 0)
        selectedTabTextAppearance =
            typedArray.getResourceId(R.styleable.BaseTabLayout_bt_selectedTabTextAppearance, 0)
        selectedTabBackgroundColor = typedArray.getColor(
            R.styleable.BaseTabLayout_bt_selectedTabBackgroundColor,
            selectedTabBackgroundColor
        )
        selectedTabBackgroundMargin =
            typedArray.getDimensionPixelSize(
                R.styleable.BaseTabLayout_bt_selectedTabBackgroundMargin,
                selectedTabBackgroundMargin
            )
        changeTabAnimationDuration =
            typedArray.getInteger(R.styleable.BaseTabLayout_bt_changeTabAnimationDuration, 0)
                .toLong()
        changeTabAnimationInterpolator =
            typedArray.getResourceId(R.styleable.BaseTabLayout_bt_changeTabAnimationInterpolator, 0)
        typedArray.getTextArray(R.styleable.BaseTabLayout_bt_entries)
            ?.mapIndexed { _, text -> Tab(text.toString().capitalize(context.currentLocale)) }
            ?.let { mTabs.addAll(it) }

        typedArray.recycle()
    }

    private fun init() {
        mSelectedTabPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = selectedTabBackgroundColor
        }

        addAllTabInternal()
        initBackground()
    }

    //region measure
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        measureMaterials()
    }

    private fun measureMaterials() {
        if (width == 0 || height == 0) {
            return
        }

        measureTabWidth()
        measureSelectedTabBackground()
    }

    private fun measureSelectedTabBackground() {
        if (mTabs.isEmpty()) {
            return
        }

        mSelectedTabBackgroundRect.left =
            (paddingStart + (mTabWidth * mSelectedTabPosition) + selectedTabBackgroundMargin).toFloat()
        mSelectedTabBackgroundRect.top = (paddingTop + selectedTabBackgroundMargin).toFloat()
        mSelectedTabBackgroundRect.right =
            mSelectedTabBackgroundRect.left + mTabWidth - (2 * selectedTabBackgroundMargin)
        mSelectedTabBackgroundRect.bottom =
            (height - paddingBottom - selectedTabBackgroundMargin).toFloat()
    }

    private fun measureTabWidth() {
        if (mTabs.isEmpty()) {
            return
        }

        mTabWidth = (width - paddingStart - paddingEnd) / mTabs.size
    }
    //endregion measure

    //region draw
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawSelectedBackground(canvas)
    }

    private fun drawSelectedBackground(canvas: Canvas) {
        if (mTabs.isEmpty()) {
            return
        }

        canvas.drawRoundRect(
            mSelectedTabBackgroundRect,
            tabBackgroundRadius,
            tabBackgroundRadius,
            mSelectedTabPaint
        )
    }
    //endregion draw

    //region change tab animation
    private fun startAnimationChangeTab(oldSelectedTabPosition: Int, newSelectedTabPosition: Int) {
        mSelectedTabStartLeft = mSelectedTabBackgroundRect.left
        mChangeTabDistance = (newSelectedTabPosition - oldSelectedTabPosition) * mTabWidth

        if (mChangeTabAnimator == null) {
            mChangeTabAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                duration = changeTabAnimationDuration
                interpolator =
                    AnimationUtils.loadInterpolator(context, changeTabAnimationInterpolator)
                addUpdateListener {
                    val progress = it.animatedValue as Float
                    measureSelectedTabBackground(progress)
                    invalidate()
                }
            }
        }
        if (mChangeTabAnimator?.isStarted == true) {
            return
        }
        mChangeTabAnimator?.start()
    }

    private fun measureSelectedTabBackground(progress: Float) {
        val leftChanged = mChangeTabDistance * progress
        mSelectedTabBackgroundRect.left = mSelectedTabStartLeft + leftChanged
        mSelectedTabBackgroundRect.right =
            mSelectedTabBackgroundRect.left + mTabWidth - (selectedTabBackgroundMargin * 2)
    }
    //endregion change tab animation

    private fun isTabSelected(index: Int): Boolean {
        return index == mSelectedTabPosition
    }

    private fun initBackground() {
        val radius = FloatArray(8) {
            layoutBackgroundRadius
        }

        val roundRectShape = RoundRectShape(radius, null, null)
        val shapeDrawable = ShapeDrawable(roundRectShape).apply {
            paint.color = layoutBackgroundColor
        }
        background = shapeDrawable
    }

    private fun addAllTabInternal() {
        removeAllViews()

        mTabs.forEachIndexed { index, tab ->
            addTabInternal(tab, index, mTabs.size)
        }

        measureMaterials()
        invalidate()
    }

    private fun addTabInternal(tab: Tab, index: Int, size: Int) {
        val textView = TextView(context).apply {
            text = tab.text
            gravity = Gravity.CENTER
            background = generateTabBackground()
            setTextAppearance(if (isTabSelected(index)) selectedTabTextAppearance else tabTextAppearance)
            setOnClickListener { onClickedTab(index) }
        }
        val layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT).apply {
            weight = 1F
        }
        this.addView(textView, layoutParams)
    }

    private fun generateTabBackground(): Drawable {
        val rippleColor = context.getColorAttr(R.attr.rippleColor)
        val stateListColor = ColorStateList.valueOf(rippleColor)

        val maskRippleColor = context.getColorAttr(R.attr.colorSurface)
        val mask = generateTabBackgroundRippleMask(maskRippleColor)

        return RippleDrawable(stateListColor, null, mask)
    }

    private fun generateTabBackgroundRippleMask(color: Int): Drawable {
        val radius = FloatArray(8) {
            tabBackgroundRadius
        }

        val roundRectShape = RoundRectShape(radius, null, null)
        val shapeDrawable = ShapeDrawable(roundRectShape).apply {
            paint.color = color
        }
        return InsetDrawable(shapeDrawable, selectedTabBackgroundMargin)
    }

    private fun onClickedTab(clickedIndex: Int) {
        if (clickedIndex == mSelectedTabPosition) {
            return
        }

        mOnTabSelectedListener?.invoke(clickedIndex)
    }

    private fun invalidateTextAppearanceTab() {
        mTabs.forEachIndexed { index, tab ->
            if (isTabSelected(index)) {
                (getChildAt(index) as? TextView)?.setTextAppearance(selectedTabTextAppearance)
            } else {
                (getChildAt(index) as? TextView)?.setTextAppearance(tabTextAppearance)
            }
        }
    }

    private fun changeTabAnimationInternal(newSelectedTabPosition: Int) {
        if (mChangeTabAnimator?.isRunning == true) {
            return
        }

        val oldSelectedTabPosition = mSelectedTabPosition
        mSelectedTabPosition = newSelectedTabPosition
        startAnimationChangeTab(oldSelectedTabPosition, mSelectedTabPosition)
        invalidateTextAppearanceTab()
    }

    fun addTab(text: CharSequence, index: Int = mTabs.size) {
        val tab = Tab(text)
        addTab(tab, index)
    }

    fun addTab(tab: Tab, index: Int = mTabs.size) {
        mTabs.add(index, tab)
        addAllTabInternal()
    }

    fun addAllTab(texts: Array<CharSequence>) {
        if (texts.isEmpty()) {
            return
        }
        val tabs = texts.map { Tab(it) }
        addAllTab(tabs.toTypedArray())
    }

    fun addAllTab(texts: List<CharSequence>) {
        if (texts.isEmpty()) {
            return
        }
        val tabs = texts.map { Tab(it) }
        addAllTab(tabs.toTypedArray())
    }

    fun addAllTab(tabs: Array<Tab>) {
        if (tabs.isEmpty()) {
            return
        }
        mTabs.clear()
        mTabs.addAll(tabs)
        addAllTabInternal()
    }

    fun setSelectedTabPosition(index: Int, animation: Boolean = true) {
        if (mSelectedTabPosition == index) {
            return
        }

        if (animation) {
            changeTabAnimationInternal(index)
            return
        }

        mSelectedTabPosition = index
        measureSelectedTabBackground()
        invalidateTextAppearanceTab()
        invalidate()
    }

    fun getSelectedTabPosition(): Int {
        return mSelectedTabPosition
    }

    fun setOnSelectedTabListener(onTabSelectedListener: (Int) -> Unit) {
        this.mOnTabSelectedListener = onTabSelectedListener
    }

    data class Tab(val text: CharSequence)
}