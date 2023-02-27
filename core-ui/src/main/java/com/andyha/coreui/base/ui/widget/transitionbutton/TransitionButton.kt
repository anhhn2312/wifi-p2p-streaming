package com.andyha.coreui.base.ui.widget.transitionbutton

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.appcompat.widget.AppCompatButton
import com.andyha.coreui.R
import com.andyha.coreui.base.ui.widget.button.BaseButton
import com.andyha.coreui.base.ui.widget.button.ThemeRxButton
import com.andyha.coreui.base.ui.widget.transitionbutton.TransitionButton.StopAnimationStyle.SHAKE


class TransitionButton : ThemeRxButton {
    private var currentState: State? = null
    private var isMorphingInProgress = false
    private var initialWidth = 0
    private var initialHeight = 0
    private var initialText: String? = null
    private var progressColor = 0
    private var progressCircularAnimatedDrawable: CircularAnimatedDrawable? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        currentState = State.IDLE

        if (attrs != null) {
            val attrsArray = context.obtainStyledAttributes(attrs, R.styleable.TransitionButton)
            val pc = attrsArray.getResourceId(0, R.color.white)
            progressColor = context.resources.getColor(pc, context.theme)
            attrsArray.recycle()
        }
    }

    fun startAnimation() {
        currentState = State.PROGRESS
        isMorphingInProgress = true
        initialWidth = width
        initialHeight = height
        initialText = text.toString()
        text = null
        isClickable = false
        startElasticAnimation(initialHeight, object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationCancel(animation)
                isMorphingInProgress = false
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (currentState == State.PROGRESS && !isMorphingInProgress) {
            drawIndeterminateProgress(canvas)
        }
    }

    private fun drawIndeterminateProgress(canvas: Canvas) {
        if (progressCircularAnimatedDrawable == null || !progressCircularAnimatedDrawable!!.isRunning) {
            val arcWidth = height / 18
            progressCircularAnimatedDrawable =
                CircularAnimatedDrawable(progressColor, arcWidth.toFloat())
            val offset = (width - height) / 2 + 10
            val right = width - offset
            val bottom = height - 10
            val top = 0 + 10
            progressCircularAnimatedDrawable!!.setBounds(offset, top, right, bottom)
            progressCircularAnimatedDrawable!!.callback = this
            progressCircularAnimatedDrawable!!.start()
        } else {
            progressCircularAnimatedDrawable!!.draw(canvas)
            invalidate()
        }
    }

    private fun stopAnimation(
        stopAnimationStyle: StopAnimationStyle? = null,
        onAnimationEndListener: (() -> Unit)? = null
    ) {
        if (!isAnimating()){
            onAnimationEndListener?.invoke()
            return
        }

        when (stopAnimationStyle) {
            SHAKE -> {
                currentState = State.ERROR
                startElasticAnimation(initialWidth, object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        text = initialText
                        startShakeAnimation(object : AnimationListenerAdapter() {
                            override fun onAnimationEnd(animation: Animation) {
                                currentState = State.IDLE
                                isClickable = true
                                onAnimationEndListener?.invoke()
                            }
                        })
                    }
                })
            }
            StopAnimationStyle.EXPAND -> {
                currentState = State.TRANSITION
                startScaleAnimation(object : AnimationListenerAdapter() {
                    override fun onAnimationEnd(animation: Animation) {
                        super.onAnimationEnd(animation)
                        onAnimationEndListener?.invoke()
                        isClickable = true
                    }
                })
            }

            else -> {
                currentState = State.IDLE
                startElasticAnimation(initialWidth, object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        text = initialText
                        onAnimationEndListener?.invoke()
                        isClickable = true
                    }
                })
            }
        }
    }

    fun stopAnimationToIdle(onAnimationEndListener: (() -> Unit)? = null) =
        stopAnimation(null, onAnimationEndListener)

    fun stopAnimationToError(onAnimationEndListener: (() -> Unit)? = null) =
        stopAnimation(SHAKE, onAnimationEndListener)

    private fun startElasticAnimation(to: Int, onAnimationEnd: AnimatorListenerAdapter) {
        startElasticAnimation(width, to, onAnimationEnd)
    }

    private fun startElasticAnimation(
        from: Int,
        to: Int,
        onAnimationEnd: AnimatorListenerAdapter?
    ) {
        val elasticAnimation = ValueAnimator.ofInt(from, to)
        elasticAnimation.addUpdateListener { valueAnimator: ValueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = layoutParams
            layoutParams.width = animatedValue
            setLayoutParams(layoutParams)
        }
        val animatorSet = AnimatorSet()
        animatorSet.duration = ELASTIC_ANIMATION_DURATION.toLong()
        animatorSet.playTogether(elasticAnimation)
        if (onAnimationEnd != null) animatorSet.addListener(onAnimationEnd)
        animatorSet.start()
    }

    private fun startShakeAnimation(animationListener: Animation.AnimationListener) {
        val shake = TranslateAnimation(0f, 15f, 0f, 0f)
        shake.duration = SHAKE_ANIMATION_DURATION.toLong()
        shake.interpolator = CycleInterpolator(4f)
        shake.setAnimationListener(animationListener)
        startAnimation(shake)
    }

    private fun startScaleAnimation(animationListener: Animation.AnimationListener) {
        val ts = (WindowUtils.getHeight(context) / height * 2.1).toFloat()
        val anim: Animation = ScaleAnimation(
            1f, ts,
            1f, ts,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim.duration = SCALE_ANIMATION_DURATION.toLong()
        anim.fillAfter = true
        anim.setAnimationListener(animationListener)
        startAnimation(anim)
    }

    fun isAnimating(): Boolean {
        return currentState == State.PROGRESS
    }

    open inner class AnimationListenerAdapter : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {}
        override fun onAnimationRepeat(animation: Animation) {}
    }

    private enum class State {
        PROGRESS, IDLE, ERROR, TRANSITION
    }

    enum class StopAnimationStyle {
        EXPAND, SHAKE
    }

    companion object {
        private const val ELASTIC_ANIMATION_DURATION = 200
        private const val SCALE_ANIMATION_DURATION = 250
        private const val SHAKE_ANIMATION_DURATION = 500
    }
}