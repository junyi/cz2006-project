package com.foodsurvey.foodsurvey.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.data.User;
import com.foodsurvey.foodsurvey.ui.widget.AspectRatioImageView;
import com.foodsurvey.foodsurvey.utility.DeviceDimensionsHelper;
import com.foodsurvey.foodsurvey.utility.UserHelper;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends ActionBarActivity {
    public enum UserType {SURVEYEE, ADMIN}

    public static final String ARG_TYPE = "type";

    private static final String FRAGMENT_PRODUCT = "product";
    private static final String FRAGMENT_PROFILE = "profile";

    private static final int REQUEST_UPDATE_PROFILE = 1;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.sidebar)
    View mSidebar;

    @InjectView(R.id.display_name)
    TextView mDisplayNameText;

    @InjectView(R.id.company_name)
    TextView mCompanyNameText;

    @InjectView(R.id.left_drawer)
    ListView mDrawerList;

    @InjectView(R.id.profile_background)
    AspectRatioImageView mProfileBackground;

    private ActionBarDrawerToggle mDrawerToggle;
    private UserType mUserType;
    private int mFragmentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        mUserType = UserType.values()[getIntent().getExtras().getInt(ARG_TYPE, UserType.SURVEYEE.ordinal())];

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
        initializeSideBar();

        selectItem(0);
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

    private void initializeSideBar() {

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, new String[]{"Products", "Profile", "Logout"}));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        User user = UserHelper.getCurrentUser(this);
        mDisplayNameText.setText(user.getFirstName() + " " + user.getLastName());

        if (mUserType == UserType.ADMIN) {
            mCompanyNameText.setVisibility(View.VISIBLE);
            mCompanyNameText.setText(user.getCompanyName());
            mProfileBackground.setImageDrawable(getResources().getDrawable(R.drawable.background2));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_edit);
        switch (mFragmentPos) {
            case 0:
                item.setVisible(false);
                break;
            case 1:
                item.setVisible(true);
                break;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(this, UpdateProfileActivity.class);
                intent.putExtra(ARG_TYPE, mUserType.ordinal());
                startActivityForResult(intent, REQUEST_UPDATE_PROFILE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Fragment productFragment;
        Fragment profileFragment = ProfileFragment.newInstance(mUserType);

        if (mUserType == UserType.SURVEYEE) {
            productFragment = ProductListFragment.newInstance();
        } else {
            productFragment = AdminProductListFragment.newInstance();
        }

        mFragmentPos = position;

        switch (position) {
            case 0:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, productFragment, FRAGMENT_PRODUCT)
                        .commit();
                getSupportActionBar().setTitle("");
                mToolbar.setTitle("All Products");
                supportInvalidateOptionsMenu();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, profileFragment, FRAGMENT_PROFILE)
                        .commit();
                getSupportActionBar().setTitle("");
                mToolbar.setTitle("Profile");
                supportInvalidateOptionsMenu();
                break;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_UPDATE_PROFILE) {
                ProfileFragment fragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_PROFILE);
                if (fragment != null) {
                    fragment.initializeWithData();
                }
            }
        }
    }
}
