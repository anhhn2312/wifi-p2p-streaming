package com.andyha.coreui.base.ui.widget.swipe;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.andyha.coreui.R;

public class ItemTouchHelperCallback extends ItemTouchHelperExtension.Callback {

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        if (viewHolder instanceof BaseSwipeViewHolder && !((BaseSwipeViewHolder) viewHolder).isSwipeEnable()) {
            return 0;
        }

        if (viewHolder instanceof BaseSwipeViewHolder) {
            return makeMovementFlags(0, ((BaseSwipeViewHolder) viewHolder).supportSwipeType());
        }
        return 0;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    private float lastDx = 0F;

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (dY != 0 && dX == 0)
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (viewHolder instanceof BaseSwipeViewHolder) {
            int elasticity = recyclerView.getContext().getResources().getDimensionPixelSize(R.dimen.elasticity_size);
//      initSelectedView(recyclerView, (BaseSwipeViewHolder) viewHolder, isCurrentlyActive);
            if (dX < 0) {
                ((BaseSwipeViewHolder) viewHolder).setState(ItemTouchHelper.START);
                if (lastDx >= 0) {
                    onStartSwipe(recyclerView, (BaseSwipeViewHolder) viewHolder);
                }
                int actionWidth = (int) ((BaseSwipeViewHolder) viewHolder).getActionWidth();
                int maxDX = actionWidth == 0 ? 0 : (actionWidth + elasticity);
                if (dX < (-1) * maxDX) {
                    dX = (-1) * maxDX;
                }
                ((BaseSwipeViewHolder) viewHolder).getContainerLayout().setTranslationX(dX);
            } else if (dX > 0) {
                ((BaseSwipeViewHolder) viewHolder).setState(ItemTouchHelper.END);
                if (lastDx <= 0) {
                    onStartSwipe(recyclerView, (BaseSwipeViewHolder) viewHolder);
                }
                int actionWidth = (int) ((BaseSwipeViewHolder) viewHolder).getActionWidth();
                int maxDX = actionWidth == 0 ? 0 : (actionWidth + elasticity);
                if (dX > maxDX) {
                    dX = maxDX;
                }

                ((BaseSwipeViewHolder) viewHolder).getContainerLayout().setTranslationX(dX);

            } else {
                ((BaseSwipeViewHolder) viewHolder).setState(0);
            }

            if (lastDx != dX) {
                ((BaseSwipeViewHolder) viewHolder).progress(dX);
            }

            lastDx = dX;
        }
    }

    @Override
    public void onItemTouch(MotionEvent event, View view, RecyclerView.ViewHolder viewHolder) {
        boolean isTouchDown = event.getAction() == MotionEvent.ACTION_DOWN;
        boolean isOption = SwipeOptionLayout.isOptionView(view);

        if (isOption && view.isEnabled()) {
            boolean isPressed = true;
            if (!isTouchDown) {
                isPressed = false;
            }
            view.setPressed(isPressed);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, boolean close) {
        super.clearView(recyclerView, viewHolder, close);
        if (viewHolder instanceof BaseSwipeViewHolder && close) {
            ((BaseSwipeViewHolder) viewHolder).getContainerLayout().setTranslationX(0f);
            ((BaseSwipeViewHolder) viewHolder).progress(0);
        }
    }

    private void onStartSwipe(RecyclerView recyclerView, BaseSwipeViewHolder holder) {
//    holder.setStartDirection();
//    holder.startSwipe();
        // TODO need collapse other opened swipe

    }
}
