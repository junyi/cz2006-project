package com.foodsurvey.foodsurvey.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.foodsurvey.foodsurvey.data.Managers;
import com.foodsurvey.foodsurvey.data.Product;
import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.data.ResultCallback;
import com.foodsurvey.foodsurvey.utility.UserHelper;
import com.foodsurvey.foodsurvey.ui.adapter.ProductListAdapter;
import com.foodsurvey.foodsurvey.ui.drawable.TextDrawable;
import com.foodsurvey.foodsurvey.ui.widget.FloatingActionButton;
import com.foodsurvey.foodsurvey.ui.widget.FloatingActionButtonHelper;
import com.foodsurvey.foodsurvey.ui.widget.ObservableRecyclerView;
import com.foodsurvey.foodsurvey.utility.DeviceDimensionsHelper;
import com.makeramen.RoundedImageView;
import com.parse.ParseUser;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;
import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AdminProductListActivity extends ActionBarActivity implements EndlessScrollListener.Callback {

    private final static int DATA_LIMIT = 10;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.sidebar)
    View mSidebar;

    @InjectView(R.id.avatar)
    RoundedImageView mAvatar;

    @InjectView(R.id.list_view)
    ObservableRecyclerView mProductListView;

    @InjectView(R.id.add_button)
    FloatingActionButton mAddButton;

    @InjectView(R.id.left_drawer)
    ListView mDrawerList;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.progress)
    CircularProgressBar mProgress;

    @InjectView(R.id.empty)
    View mEmpty;

    private ProductListAdapter mProductListAdapter;
    private EndlessScrollListener mEndlessScrollListener;
    private RecyclerView.LayoutManager mLayoutManager;
    private ActionBarDrawerToggle mDrawerToggle;
    private FloatingActionButtonHelper mFabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_product_list);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.setTitle("All Products");

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        mSidebar.getLayoutParams().width = DeviceDimensionsHelper.getDisplayWidth(this) - actionBarHeight;
        populateSideBar();

        mSwipeRefreshLayout.setColorSchemeColors(R.attr.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeWithData();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);

        // Inflate a menu to be displayed in the mToolbar
//        mToolbar.inflateMenu(R.menu.product_list);


        mProductListAdapter = new ProductListAdapter(this);
        mProductListView.setAdapter(mProductListAdapter);
//        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getResources().getDrawable(R.drawable.divider));
//        mProductListView.addItemDecoration(divider);
        mProductListView.setHasFixedSize(true);

        mLayoutManager = new ListLayoutManager(this, TwoWayLayoutManager.Orientation.VERTICAL);
        mProductListView.setLayoutManager(mLayoutManager);


        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mProductListView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int position, long l) {
                Intent intent = new Intent(AdminProductListActivity.this, AdminProductDetailActivity.class);
                Product product = mProductListAdapter.getItem(position);
                intent.putExtra(ProductDetailActivity.ARG_PRODUCT, Parcels.wrap(product));
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
                Intent intent = new Intent(AdminProductListActivity.this, AdminEditProductDetailActivity.class);
                startActivity(intent);
            }
        });

        initializeWithData();

    }

    public void initializeWithData() {
        String companyId = UserHelper.getCurrentUser(this).getCompanyId();
        Managers.getProductManager().getProducts(0, DATA_LIMIT, companyId, new ResultCallback<List>() {

            @Override
            public void onResult(List data) {
                onDataReceived(false, data);
            }
        });
    }

    @Override
    public void loadMoreData(int offset) {
        String companyId = UserHelper.getCurrentUser(this).getCompanyId();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.product_list, menu);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void populateSideBar() {
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, new String[]{"Products", "Settings", "Logout"}));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        TextDrawable textDrawable = new TextDrawable(this);
        textDrawable.setText("H");
        textDrawable.setTextColor(0xffffff);
        textDrawable.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"));
        mAvatar.setImageDrawable(textDrawable);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position) {
            case 2:
                ParseUser.logOut();
                UserHelper.removeCurrentUser(this);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
        }

        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mSidebar);
    }
}
