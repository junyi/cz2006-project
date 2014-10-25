package com.foodsurvey.foodsurvey.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.foodsurvey.foodsurvey.BuildConfig;

public class FloatingActionButtonHelper {

    /**
     * Constructor
     *
     * @param button The {@link View} you want to animate on and off the screen while scrolling
     */
    public FloatingActionButtonHelper(View button) {
        if (button == null) {
            Log.w(tag, "View is null, nothing will be animated");
        }
        mButton = button;
    }

    /**
     * Set the duration of the animation
     *
     * @param duration the duration in milliseconds</br>Default is 175
     */
    public void setAnimationDuration(long duration) {
        ANIMATION_DURATION = duration;
    }

    /**
     * This should be added to the {@link RecyclerView.OnScrollListener}
     * <code>onScrolled</code>.
     *
     * @param dx
     * @param dy
     */
    public void onScrolled(int dx, int dy) {
        if (mButton == null || dy == 0) {
            return;
        }
        if (dy > 0) { // Scrolling to bottom
            if (mIsScrollDirectionLocked && mScrollingDirection != 0) return;

            if (mButton.getVisibility() == View.GONE || mIsAnimatingOff) {
                return;
            } else {
                mScrollingDirection = SCROLLING_DOWN;
                mIsAnimatingOff = !mIsAnimatingOff;
                mButton.setAlpha(1f);
                mButton.setTranslationY(0f);
                mButton.animate().alpha(0f)
                        .translationY(mButton.getHeight())
                        .setDuration(ANIMATION_DURATION)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mIsAnimatingOff = !mIsAnimatingOff;
                                if (D) Log.d(tag, "Animation off screen Done!!!!");
                                mButton.setVisibility(View.GONE);
                            }
                        }).start();
            }
            return;
        } else { // Scrolling to top
            if (mIsScrollDirectionLocked && mScrollingDirection != 0) return;

            if (mButton.getVisibility() == View.VISIBLE || mIsAnimatingOn) {
                return;
            } else {
                mScrollingDirection = SCROLLING_UP;
                mIsAnimatingOn = !mIsAnimatingOn;
                mButton.setVisibility(View.VISIBLE);
                mButton.setAlpha(0f);
                mButton.setTranslationY(mButton.getHeight());
                mButton.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(ANIMATION_DURATION)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mIsAnimatingOn = !mIsAnimatingOn;
                                if (D) Log.d(tag, "Animation onto screen Done!!!!");
                            }
                        })
                        .start();
            }
        }
    }

    /**
     * This should be added to the {@link RecyclerView.OnScrollListener}
     * <code>onScrollStateChanged</code>.
     */
    public void onScrollStateChanged(int newState) {
        if (!mIsScrollDirectionLocked) return;

        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                mScrollingDirection = 0;
                break;

            default:
                break;
        }
    }

    /**
     * Returns the view that is being animated.
     */
    public View getAnimatedView() {
        return mButton;
    }

    /**
     * This will lock the animation to the first direction that is scrolled.
     * That means that when you scroll toward the bottom then scroll to the top without releasing the screen.
     * The view will not be animated back on the screen.</br>
     * You would have to release the screen and scroll the other direction to get the opposite animation.
     */
    public void lockScrollingDirection() {
        mIsScrollDirectionLocked = true;
    }

    /**
     * Release the scroll direction lock so that a scroll direction change will immediately run the appropriate animation.
     */
    public void unlockScrollingDirection() {
        mIsScrollDirectionLocked = false;
    }

    /**
     * Get the status of the current scroll lock.
     */
    public boolean isScrollDirectionLocked() {
        return mIsScrollDirectionLocked;
    }

    private static final int SCROLLING_UP = 1;

    private static final int SCROLLING_DOWN = 2;

    private int mScrollingDirection = 0;

    private boolean mIsScrollDirectionLocked = false;

    private long ANIMATION_DURATION = 175L;

    private boolean mIsAnimatingOff = false;

    private boolean mIsAnimatingOn = false;

    private View mButton;

    private boolean D = BuildConfig.DEBUG;

    private static final String tag = FloatingActionButtonHelper.class.getSimpleName();
}