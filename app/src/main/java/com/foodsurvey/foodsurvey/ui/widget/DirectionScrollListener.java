package com.foodsurvey.foodsurvey.ui.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

class DirectionScrollListener extends RecyclerView.OnScrollListener {

    private static final int DIRECTION_CHANGE_THRESHOLD = 1;
    private final FloatingActionButton mFloatingActionButton;
    private int mPrevPosition;
    private int mPrevTop;
    private boolean mUpdated;

    DirectionScrollListener(FloatingActionButton floatingActionButton) {
        mFloatingActionButton = floatingActionButton;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }

    public void onScroll(RecyclerView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        final View topChild = view.getChildAt(0);
        int firstViewTop = 0;
        if (topChild != null) {
            firstViewTop = topChild.getTop();
        }
        boolean goingDown;
        boolean changed = true;
        if (mPrevPosition == firstVisibleItem) {
            final int topDelta = mPrevTop - firstViewTop;
            goingDown = firstViewTop < mPrevTop;
            changed = Math.abs(topDelta) > DIRECTION_CHANGE_THRESHOLD;
        } else {
            goingDown = firstVisibleItem > mPrevPosition;
        }
        if (changed && mUpdated) {
            mFloatingActionButton.hide(goingDown);
        }
        mPrevPosition = firstVisibleItem;
        mPrevTop = firstViewTop;
        mUpdated = true;
    }
}