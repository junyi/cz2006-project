package com.foodsurvey.foodsurvey.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.control.Managers;
import com.foodsurvey.foodsurvey.entity.Product;
import com.foodsurvey.foodsurvey.control.ResultCallback;
import com.foodsurvey.foodsurvey.entity.Review;
import com.foodsurvey.foodsurvey.ui.adapter.AdminReviewListAdapter;
import com.foodsurvey.foodsurvey.ui.widget.ObservableRecyclerView;
import com.google.gson.Gson;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * UI for the administrator to view all the reviews related to a product
 *
 * @author Huang Jinbin
 */
public class AdminReviewListActivity extends ActionBarActivity implements EndlessScrollListener.Callback {
    /**
     * Argument for the {@link com.foodsurvey.foodsurvey.entity.Product} parcelable to be passed into the activity
     */
    public static final String ARG_PRODUCT = "product";

    /**
     * Maximum number of data to fetch in each request
     */
    private final static int DATA_LIMIT = 10;

    /**
     * UI to display the list of reviews
     */
    @InjectView(R.id.list_view)
    ObservableRecyclerView mReviewListView;

    /**
     * UI to enable pull to refresh
     */
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Toolbar for the activity
     */
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * UI to show loading progress
     */
    @InjectView(R.id.progress)
    CircularProgressBar mProgress;

    @InjectView(R.id.empty)
    View mEmpty;

    /**
     * Adapter to populate the list with data
     */
    private AdminReviewListAdapter mReviewListAdapter;

    /**
     * Layout manager associated to the {@link android.support.v7.widget.RecyclerView}{@code mReviewListView}
     */
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * Scroll listener which allows endless scrolling for the recycler view
     */
    private EndlessScrollListener mEndlessScrollListener;

    /**
     * Product entity for which the review is related to
     */
    private Product mProduct;

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState Bundle which contains any saved data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_review_list);

        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        mProduct = new Gson().fromJson(bundle.getString(ARG_PRODUCT), Product.class);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.setTitle("Reviews");
        mToolbar.setSubtitle(mProduct.getTitle());
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                Intent intent = new Intent(AdminReviewListActivity.this, AdminReviewDetailActivity.class);
                Review review = mReviewListAdapter.getItem(position);
                Gson gson = new Gson();
                intent.putExtra(AdminReviewDetailActivity.ARG_REVIEW, gson.toJson(review));
                intent.putExtra(AdminReviewDetailActivity.ARG_PRODUCT, gson.toJson(mProduct));

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

                if (!recyclerView.canScrollVertically(-1)) {
                    mSwipeRefreshLayout.setEnabled(true);
                } else {
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        };

        mReviewListView.setOnScrollListener(mEndlessScrollListener);

        initializeWithData();

    }

    /**
     * Method to initialize the review list using obtained data about the associated product
     */
    public void initializeWithData() {
        String productId = mProduct.getId();
        Managers.getReviewManager().getReviews(0, DATA_LIMIT, productId, new ResultCallback<List>() {

            @Override
            public void onResult(List data) {
                onDataReceived(false, data);
            }
        });
    }

    /**
     * Method to load more data, used by the endless scroll listener
     * @param offset offset of the data to be requested
     */
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

    /**
     * Method to handle data when data is received
     * @param loadMore True if the request is asking for more data, false if asking for new data to replace the list
     * @param list The list of data
     */
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }


}
