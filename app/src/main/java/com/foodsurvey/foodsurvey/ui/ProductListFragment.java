package com.foodsurvey.foodsurvey.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.control.Managers;
import com.foodsurvey.foodsurvey.control.ResultCallback;
import com.foodsurvey.foodsurvey.entity.Product;
import com.foodsurvey.foodsurvey.ui.adapter.ProductListAdapter;
import com.foodsurvey.foodsurvey.ui.widget.MultiSwipeRefreshLayout;
import com.foodsurvey.foodsurvey.utility.UserHelper;
import com.google.gson.Gson;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * UI for surveyee to view a list of products
 *
 * @author Hee Jun Yi
 */
public class ProductListFragment extends Fragment implements EndlessScrollListener.Callback {
    private final static int DATA_LIMIT = 10;

    /**
     * UI to display the list of products
     */
    @InjectView(R.id.list_view)
    RecyclerView mProductListView;

    /**
     * UI to display progress when loading
     */
    @InjectView(R.id.progress)
    CircularProgressBar mProgress;

    /**
     * UI for pull to refresh
     */
    @InjectView(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * UI to show when there is no products
     */
    @InjectView(R.id.empty)
    View mEmpty;

    /**
     * Adapter for the recycler view of products
     */
    private ProductListAdapter mProductListAdapter;

    /**
     * Scroll listener to handle endless scrolling and loading of data
     */
    private EndlessScrollListener mEndlessScrollListener;

    /**
     * Layout manager for the recycler view
     */
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * Required empty constructor
     */
    public ProductListFragment() {

    }

    /**
     * Static constructor pattern for fragment
     *
     * @return the new fragment
     */
    public static ProductListFragment newInstance() {
        ProductListFragment fragment = new ProductListFragment();
        return fragment;
    }

    /**
     * Called whenever a view is to be created
     *
     * @param inflater           inflater to inflate the layout
     * @param container          parent viewgroup of the view
     * @param savedInstanceState saved bundle of data
     * @return the inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    /**
     * Called once the view is created
     *
     * @param view               the created view
     * @param savedInstanceState the saved bundle of data
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mSwipeRefreshLayout.setColorSchemeColors(R.attr.colorPrimary, R.attr.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeWithData();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);


        mProductListAdapter = new ProductListAdapter(getActivity());
        mProductListView.setAdapter(mProductListAdapter);
        mProductListView.setHasFixedSize(true);
        mSwipeRefreshLayout.setSwipeableChildren(R.id.list_view, R.id.empty);

        mEndlessScrollListener = new EndlessScrollListener(this, DATA_LIMIT) {
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
        mProductListView.setOnScrollListener(mEndlessScrollListener);


        mLayoutManager = new ListLayoutManager(getActivity(), TwoWayLayoutManager.Orientation.VERTICAL);
        mProductListView.setLayoutManager(mLayoutManager);


        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mProductListView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                Product product = mProductListAdapter.getItem(position);
                intent.putExtra(ProductDetailActivity.ARG_PRODUCT, new Gson().toJson(product));
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

        initializeWithData();

    }

    /**
     * Called to initialize the UI with the list of products
     */
    public void initializeWithData() {
        Managers.getProductManager().getProducts(0, DATA_LIMIT, null, new ResultCallback<List>() {

            @Override
            public void onResult(List data) {
                onDataReceived(false, data);
            }
        });
    }

    /**
     * Method to load more data, used by the endless scroll listener
     *
     * @param offset offset of the data to be requested
     */
    @Override
    public void loadMoreData(int offset) {
        String companyId = UserHelper.getCurrentUser(getActivity()).getCompanyId();
        Managers.getProductManager().getProducts(offset, DATA_LIMIT, companyId, new ResultCallback<List>() {

            @Override
            public void onResult(List data) {
                onDataReceived(true, data);
            }
        });
    }

    /**
     * Method to handle data when data is received
     *
     * @param loadMore True if the request is asking for more data, false if asking for new data to replace the list
     * @param list     The list of data
     */
    public void onDataReceived(boolean loadMore, List list) {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);


        mEndlessScrollListener.onLoadFinished(list == null ? 0 : list.size());
        if (list != null) {
            // If it is load more, append to the list, otherwise replace the list
            mProgress.setVisibility(View.GONE);
            mProductListView.setVisibility(View.VISIBLE);
            if (loadMore) {
                mProductListAdapter.addItems(list);
            } else {
                mProductListAdapter.setItems(list);
                if (list.size() == 0) {
                    mEmpty.setVisibility(View.VISIBLE);
//                    mProductListView.setVisibility(View.GONE);
                } else {
                    mEmpty.setVisibility(View.GONE);
//                    mProductListView.setVisibility(View.VISIBLE);
                }
            }

        }
    }
}
