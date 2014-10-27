package com.foodsurvey.foodsurvey.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.foodsurvey.foodsurvey.data.Managers;
import com.foodsurvey.foodsurvey.data.Product;
import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.data.ResultCallback;
import com.foodsurvey.foodsurvey.data.Review;
import com.foodsurvey.foodsurvey.ui.adapter.AdminReviewListAdapter;
import com.foodsurvey.foodsurvey.ui.widget.ObservableRecyclerView;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;
import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AdminReviewListActivity extends ActionBarActivity implements EndlessScrollListener.Callback {
    /**
     * Argument of the b
     */
    public static final String ARG_PRODUCT = "product";
    private final static int DATA_LIMIT = 10;

    @InjectView(R.id.list_view)
    ObservableRecyclerView mReviewListView;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.progress)
    CircularProgressBar mProgress;

    @InjectView(R.id.empty)
    View mEmpty;

    private AdminReviewListAdapter mReviewListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private EndlessScrollListener mEndlessScrollListener;

    private Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_review_list);

        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        mProduct = Parcels.unwrap(bundle.getParcelable(ARG_PRODUCT));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.setTitle("Reviews");
        mToolbar.setSubtitle(mProduct.getTitle());

        mSwipeRefreshLayout.setColorSchemeColors(R.attr.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeWithData();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);


        mReviewListAdapter = new AdminReviewListAdapter(this);
        mReviewListView.setAdapter(mReviewListAdapter);
        mReviewListView.setHasFixedSize(true);

        mEndlessScrollListener = new EndlessScrollListener(this, DATA_LIMIT);
        mReviewListView.setOnScrollListener(mEndlessScrollListener);

        mLayoutManager = new ListLayoutManager(this, TwoWayLayoutManager.Orientation.VERTICAL);
        mReviewListView.setLayoutManager(mLayoutManager);


        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mReviewListView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int position, long l) {
                //TODO
                Intent intent = new Intent(AdminReviewListActivity.this, AdminReviewDetailActivity.class);
                Review review = mReviewListAdapter.getItem(position);
                intent.putExtra(AdminReviewDetailActivity.ARG_REVIEW, Parcels.wrap(review));
                intent.putExtra(AdminReviewDetailActivity.ARG_PRODUCT, Parcels.wrap(mProduct));
                startActivity(intent);
            }
        });

        itemClickSupport.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView recyclerView, View view, int i, long l) {
                initializeWithData();
                return false;
            }
        });

        mEndlessScrollListener = new EndlessScrollListener(this, DATA_LIMIT) {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };

        mReviewListView.setOnScrollListener(mEndlessScrollListener);

        initializeWithData();

    }

    public void initializeWithData() {
        String productId = mProduct.getId();
        Managers.getReviewManager().getReviews(0, DATA_LIMIT, productId, new ResultCallback<List>() {

            @Override
            public void onResult(List data) {
                onDataReceived(false, data);
            }
        });
    }

    @Override
    public void loadMoreData(int offset) {
        String productId = mProduct.getId();
        Managers.getReviewManager().getReviews(offset, DATA_LIMIT, productId, new ResultCallback<List>() {

            @Override
            public void onResult(List data) {
                onDataReceived(true, data);
            }
        });
    }

    public void onDataReceived(boolean loadMore, List list) {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);


        mEndlessScrollListener.onLoadFinished(list == null ? 0 : list.size());

        if (list != null) {

            mProgress.setVisibility(View.GONE);
            mReviewListView.setVisibility(View.VISIBLE);

            // If it is load more, append to the list, otherwise replace the list
            if (loadMore) {
                mReviewListAdapter.addItems(list);
            } else {
                mReviewListAdapter.setItems(list);
            }

            if (list.size() == 0) {
                mEmpty.setVisibility(View.VISIBLE);
                mReviewListView.setVisibility(View.GONE);
            } else {
                mEmpty.setVisibility(View.GONE);
                mReviewListView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.product_list, menu);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }


}
