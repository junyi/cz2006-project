package com.foodsurvey.foodsurvey.ui.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created on 24/10/14.
 */
public class ObservableRecyclerView extends RecyclerView {

    private boolean mScrollAtBottom = false;

    public ObservableRecyclerView(Context context) {
        super(context);
    }

    public ObservableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = getLayoutManager().getChildAt(getChildCount() - 1);
        int diff = view.getBottom() - getHeight();// Calculate the scrolldiff
//        Log.d("ObservableScrollView", "Diff: " + diff);
//        System.out.printf("Bottom: %d, Height: %d, scrollY: %d, top: %d\n", view.getBottom(), getHeight(), getScrollY(), view.getTop());
        if (diff <= 0) {  // if diff is zero, then the bottom has been reached
            mScrollAtBottom = true;
        } else {
            mScrollAtBottom = false;
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public boolean isScrollAtBottom() {
        return mScrollAtBottom;
    }
}
