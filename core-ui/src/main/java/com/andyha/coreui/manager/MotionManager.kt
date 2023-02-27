package com.andyha.coreui.manager

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.transition.Transition
import android.view.View
import android.view.animation.Interpolator
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import com.andyha.coreui.R
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.platform.Hold
import com.google.android.material.transition.platform.MaterialContainerTransform

object MotionManager {

    fun buildEnterContainerTransform(context: Context): MaterialContainerTransform {
        return MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            transitionDirection = MaterialContainerTransform.TRANSITION_DIRECTION_ENTER
            duration = context.resources.getInteger(R.integer.shared_element_duration).toLong()
            addTarget(android.R.id.content)
            setAllContainerColors(Color.TRANSPARENT)
        }
    }

    fun buildReturnContainerTransform(context: Context): MaterialContainerTransform {
        return MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            transitionDirection = MaterialContainerTransform.TRANSITION_DIRECTION_RETURN
            duration = context.resources.getInteger(R.integer.shared_element_duration).toLong()
            addTarget(android.R.id.content)
            setAllContainerColors(Color.TRANSPARENT)
        }
    }

    fun buildActivityOption(
        activity: Activity,
        vararg pair: Pair<View, String>
    ): ActivityOptionsCompat {
        return ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity,
            *pair
        )
    }

    fun buildExitHoldTransition(context: Context): Transition {
        return Hold().apply {
            duration = context.resources.getInteger(R.integer.shared_element_duration).toLong()
        }
    }

    fun buildFadeThroughTransition(context: Context, rootIds: Array<Int>): MaterialFadeThrough {
        return MaterialFadeThrough().apply {
            // Add targets for this transition to explicitly run transitions only on these views. Without
            // targeting, a MaterialFadeThrough would be run for every view in the Fragment's layout.
            // Id of root view in 4 tabs
            rootIds.forEach {
                addTarget(it)
            }

            duration = context.resources.getInteger(R.integer.shared_element_duration).toLong()
        }
    }

    fun buildSharedAxisTransition(
        axis: Int,
        entering: Boolean,
        startView: Int = 0,
        endView: Int = 0
    ): MaterialSharedAxis {
        val transition = MaterialSharedAxis(axis, entering)

        // Add targets for this transition to explicitly run transitions only on these views. Without
        // targeting, a MaterialSharedAxis transition would be run for every view in the
        // Fragment's layout.
        if (startView != 0) {
            transition.addTarget(startView)
        }
        if (endView != 0) {
            transition.addTarget(endView)
        }
        return transition
    }

    fun buildAutoTransition(
        duration: Long = 300L,
        interpolator: Interpolator = FastOutSlowInInterpolator()
    ): androidx.transition.Transition {
        return AutoTransition().apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }

    fun buildFadeTransition(
        duration: Long = 300L,
        interpolator: Interpolator = FastOutSlowInInterpolator()
    ): Fade {
        return Fade().apply {
            this.duration = duration
            this.interpolator = interpolator
        }
    }

    fun buildChangeBoundTransition(
        duration: Long = 500L,
        interpolator: Interpolator = FastOutSlowInInterpolator()
    ): ChangeBounds {
        return ChangeBounds().apply {
            this.duration = duration
            this.interpolator = FastOutSlowInInterpolator()
        }
    }
}