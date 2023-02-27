package com.andyha.coreui.base.ui.widget.swipe;

import android.animation.Animator;
import android.animation.ValueAnimator;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecoverAnimation implements Animator.AnimatorListener {

    final float mStartDx;

    final float mStartDy;

    final float mTargetX;

    final float mTargetY;

    final RecyclerView.ViewHolder mViewHolder;

    final int mActionState;

    private final ValueAnimator mValueAnimator;

    public final int mAnimationType;

    public boolean mIsPendingCleanup;

    float mX;

    float mY;

    // if user starts touching a recovering view, we put it into interaction mode again,
    // instantly.
    boolean mOverridden = false;

    public boolean mEnded = false;

    public float mFraction;

    public RecoverAnimation(RecyclerView.ViewHolder viewHolder, int animationType,
                            int actionState, float startDx, float startDy, float targetX, float targetY) {
        mActionState = actionState;
        mAnimationType = animationType;
        mViewHolder = viewHolder;
        mStartDx = startDx;
        mStartDy = startDy;
        mTargetX = targetX;
        mTargetY = targetY;
        mValueAnimator = ValueAnimator.ofFloat(0f, 1f);
        mValueAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        setFraction(animation.getAnimatedFraction());
                    }
                });
        mValueAnimator.setTarget(viewHolder.itemView);
        mValueAnimator.addListener(this);
        setFraction(0f);
    }

    public void setDuration(long duration) {
        mValueAnimator.setDuration(duration);
    }

    public void start() {
        mViewHolder.setIsRecyclable(false);
        mValueAnimator.start();
    }

    public void cancel() {
        mValueAnimator.cancel();
    }

    public void setFraction(float fraction) {
        mFraction = fraction;
    }

    /**
     * We run updates on onDraw method but use the fraction from animator callback.
     * This way, we can sync translate x/y values w/ the animators to avoid one-off frames.
     */
    public void update() {
            /*if (mStartDx == mTargetX) {
//                mX = ViewCompat.getTranslationX(mViewHolder.itemView);
            } else */
        {
            mX = mStartDx + mFraction * (mTargetX - mStartDx);
        }
        if (mStartDy == mTargetY) {
            mY = ViewCompat.getTranslationY(mViewHolder.itemView);
        } else {
            mY = mStartDy + mFraction * (mTargetY - mStartDy);
        }
    }

    public float getHitX() {
        return mX;
    }

    public float getHitY() {
        return mViewHolder.itemView.getY() + mY;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (!mEnded) {
            mViewHolder.setIsRecyclable(true);
        }
        mEnded = true;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        setFraction(1f); //make sure we recover the view's state.
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}