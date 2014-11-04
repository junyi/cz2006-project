package com.foodsurvey.foodsurvey.ui.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created on 24/10/14.
 */
public class ObservableRecyclerView extends RecyclerView {

    private boolean mScrollAtTop = false;

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
//            View viewTop = getLayoutManager().getChildAt(0);
//
//            if (viewTop.getTop() <= 0) {
//                mScrollAtTop = true;
//            } else {
//                mScrollAtTop = false;
//            }
//
//            Log.d("ObservableScrollView", "ViewTop: " + viewTop.getTop());
//
//
//            View viewBottom = getLayoutManager().getChildAt(getChildCount() - 1);
//
//            int bottomDiff = viewBottom.getBottom() - getHeight();// Calculate the scrolldiff
////        System.out.printf("Bottom: %d, Height: %d, scrollY: %d, top: %d\n", view.getBottom(), getHeight(), getScrollY(), view.getTop());
//            if (bottomDiff <= 0) {  // if diff is zero, then the bottom has been reached
//                mScrollAtBottom = true;
//            } else {
//                mScrollAtBottom = false;
//            }
        mScrollAtBottom = !canScrollVertically(1);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public boolean isScrollAtBottom() {
        return mScrollAtBottom;
    }

    public boolean isScrollAtTop() {
        return mScrollAtTop;
    }
}
