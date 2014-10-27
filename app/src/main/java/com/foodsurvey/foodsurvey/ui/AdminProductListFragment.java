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
import com.foodsurvey.foodsurvey.data.Managers;
import com.foodsurvey.foodsurvey.data.Product;
import com.foodsurvey.foodsurvey.data.ResultCallback;
import com.foodsurvey.foodsurvey.ui.adapter.ProductListAdapter;
import com.foodsurvey.foodsurvey.ui.widget.FloatingActionButton;
import com.foodsurvey.foodsurvey.ui.widget.FloatingActionButtonHelper;
import com.foodsurvey.foodsurvey.ui.widget.ObservableRecyclerView;
import com.foodsurvey.foodsurvey.utility.UserHelper;
import com.google.gson.Gson;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class AdminProductListFragment extends Fragment implements EndlessScrollListener.Callback {

    private final static int DATA_LIMIT = 10;

    @InjectView(R.id.list_view)
    ObservableRecyclerView mProductListView;

    @InjectView(R.id.add_button)
    FloatingActionButton mAddButton;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.progress)
    CircularProgressBar mProgress;

    @InjectView(R.id.empty)
    View mEmpty;

    private ProductListAdapter mProductListAdapter;
    private EndlessScrollListener mEndlessScrollListener;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButtonHelper mFabHelper;

    /**
     * Required empty constructor
     */
    public AdminProductListFragment() {

    }

    public static AdminProductListFragment newInstance() {
        AdminProductListFragment fragment = new AdminProductListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product_list, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.setColorSchemeColors(R.attr.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeWithData();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);

        mProductListAdapter = new ProductListAdapter(getActivity());
        mProductListView.setAdapter(mProductListAdapter);
//        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getResources().getDrawable(R.drawable.divider));
//        mProductListView.addItemDecoration(divider);
        mProductListView.setHasFixedSize(true);

        mLayoutManager = new ListLayoutManager(getActivity(), TwoWayLayoutManager.Orientation.VERTICAL);
        mProductListView.setLayoutManager(mLayoutManager);

        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mProductListView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), AdminProductDetailActivity.class);
                Product product = mProductListAdapter.getItem(position);
                intent.putExtra(AdminProductDetailActivity.ARG_PRODUCT, new Gson().toJson(product));
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

        mFabHelper = new FloatingActionButtonHelper(mAddButton);

        mEndlessScrollListener = new EndlessScrollListener(this, DATA_LIMIT) {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mFabHelper.onScrollStateChanged(newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mFabHelper.onScrolled(dx, dy);
            }
        };
        mProductListView.setOnScrollListener(mEndlessScrollListener);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdminEditProductDetailActivity.class);
                startActivity(intent);
            }
        });

        initializeWithData();

    }

    public void initializeWithData() {
        String companyId = UserHelper.getCurrentUser(getActivity()).getCompanyId();
        Managers.getProductManager().getProducts(0, DATA_LIMIT, companyId, new ResultCallback<List>() {

            @Override
            public void onResult(List data) {
                onDataReceived(false, data);
            }
        });
    }

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
            }

            if (list.size() == 0) {
                mEmpty.setVisibility(View.VISIBLE);
                mProductListView.setVisibility(View.GONE);
            } else {
                mEmpty.setVisibility(View.GONE);
                mProductListView.setVisibility(View.VISIBLE);
            }
        }
    }

}
