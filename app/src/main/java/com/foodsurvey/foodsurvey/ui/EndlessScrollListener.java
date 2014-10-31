package com.foodsurvey.foodsurvey.ui;

import android.support.v7.widget.RecyclerView;

import com.foodsurvey.foodsurvey.ui.widget.ObservableRecyclerView;

/**
 * Listener which allows for endless scrolling
 */
public class EndlessScrollListener extends RecyclerView.OnScrollListener {
    private boolean mScrollAtBottom;

    /**
     * Indicates if the data is currently being loaded.
     */
    private boolean mLoading = false;

    /**
     * Indicates the number of loaded pages in the previous request.
     */
    private int mLoadedDataCount = 0;

    /**
     * Indicates the maximum number of pages which can be requested.
     */
    private int mLimit = 0;

    /**
     * Indicates the next offset of the data to be loaded.
     */
    private int mOffset = 0;


    private Callback mCallback = null;

    public EndlessScrollListener(Callback callback, int limit) {
        mCallback = callback;
        mLimit = limit;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrollAtBottom) {
            isScrollCompleted();
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        ObservableRecyclerView view = (ObservableRecyclerView) recyclerView;
        mScrollAtBottom = view.isScrollAtBottom();
    }

    /**
     * This method should be triggered whenever the loading has finished.
     */
    public void onLoadFinished(int loadedDataCount) {
        mLoadedDataCount = loadedDataCount;
        mLoading = false;
        mOffset++;
    }

    private void isScrollCompleted() {
        /*
            If it's not currently loading, and previously requested number of pages is the same as
            the number of loaded pages (may have more data to load), then request for more
         */
        if (!mLoading && mLoadedDataCount == mLimit && mCallback != null) {
            mLoading = true;
            mCallback.loadMoreData(mOffset);
        }
    }

    public interface Callback {

        public void loadMoreData(final int offset);
    }

}
