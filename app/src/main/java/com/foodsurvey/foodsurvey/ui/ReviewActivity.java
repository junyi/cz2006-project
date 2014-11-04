package com.foodsurvey.foodsurvey.ui;

/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.control.Managers;
import com.foodsurvey.foodsurvey.control.ResultCallback;
import com.foodsurvey.foodsurvey.entity.Product;
import com.foodsurvey.foodsurvey.utility.DialogHelper;
import com.foodsurvey.foodsurvey.utility.UserHelper;
import com.foodsurvey.foodsurvey.wizard.ImagePage;
import com.foodsurvey.foodsurvey.wizard.ModelCallbacks;
import com.foodsurvey.foodsurvey.wizard.Page;
import com.foodsurvey.foodsurvey.wizard.PageFragmentCallbacks;
import com.foodsurvey.foodsurvey.wizard.SingleFixedChoicePage;
import com.foodsurvey.foodsurvey.wizard.TextPage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.me.lewisdeane.ldialogs.CustomDialog;

/**
 * UI container for the review questions
 *
 * @author Jomain Tan
 */
public class ReviewActivity extends ActionBarActivity implements PageFragmentCallbacks, ModelCallbacks {
    /**
     * Argument for the {@link com.foodsurvey.foodsurvey.entity.Product} parcelable to be passed into the activity
     */
    public static final String ARG_PRODUCT = "product";

    /**
     * UI which allows for page swiping of the review questions
     */
    @InjectView(R.id.pager)
    ViewPager mPager;

    /**
     * Button to move to the next page of the review
     */
    @InjectView(R.id.next_button)
    Button mNextButton;

    /**
     * Button to move to the previous page of the review
     */
    @InjectView(R.id.prev_button)
    Button mPrevButton;

    /**
     * Toolbar for the activity
     */
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * Indicates if the current page has been consumed
     */
    private boolean mConsumePageSelectedEvent;

    /**
     * Adapter for the viewpager
     */
    private MyPagerAdapter mPagerAdapter;

    /**
     * List of pages which represents the review questions and answers
     */
    private List<Page> mCurrentPageSequence;

    /**
     * Product entity for which the review is for
     */
    private Product mProduct;

    /**
     * Progress dialog for the activity
     */
    private Dialog mProgressDialog = null;

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState Bundle which contains any saved data
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        mProduct = new Gson().fromJson(bundle.getString(ARG_PRODUCT), Product.class);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.setSubtitle(mProduct.getTitle());
        mToolbar.setTitle("Review");

        populatePageList();

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);


        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size() - 1) {
                    showConfirmationDialog();
                } else {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        updateBottomBar();
    }

    private void populatePageList() {
        mCurrentPageSequence = new ArrayList<Page>();
        Resources res = getResources();
        Page[] pageList = new Page[]{
                new SingleFixedChoicePage(this, res.getString(R.string.q1_title), res.getString(R.string.q1_question))
                        .setChoices(res.getStringArray(R.array.q1_options))
                        .setRequired(true),
                new SingleFixedChoicePage(this, res.getString(R.string.q2_title), res.getString(R.string.q2_question))
                        .setChoices(res.getStringArray(R.array.q2_options))
                        .setRequired(true),
                new SingleFixedChoicePage(this, res.getString(R.string.q3_title), res.getString(R.string.q3_question))
                        .setChoices(res.getStringArray(R.array.q3_options))
                        .setRequired(true),
                new SingleFixedChoicePage(this, res.getString(R.string.q4_title), res.getString(R.string.q4_question))
                        .setChoices(res.getStringArray(R.array.q4_options))
                        .setRequired(true),
                new TextPage(this, res.getString(R.string.q5_question)),
                new ImagePage(this, res.getString(R.string.q6_question))
        };
        mCurrentPageSequence.addAll(Arrays.asList(pageList));

    }

    /**
     * Method to update the bottom bar which contains the next & previous buttons
     */
    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size() - 1) {
            mNextButton.setText(R.string.submit);
        } else {
            mNextButton.setText(R.string.next);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());

            int enabledColor = getResources().getColor(R.color.color_red_accent);
            int disabledColor = getResources().getColor(android.R.color.darker_gray);
            mNextButton.setTextColor(mNextButton.isEnabled() ? enabledColor : disabledColor);
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public Page onGetPage(String key) {
        for (Page childPage : mCurrentPageSequence) {
            if (childPage.getKey().equals(key)) {
                return childPage;
            }
        }
        return null;
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private Fragment mPrimaryItem;
        private int mCutOffPage;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            if (mCurrentPageSequence == null) {
                return 0;
            }
            return Math.min(mCutOffPage + 1, mCurrentPageSequence.size());
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    /**
     * Method to show confirmation dialog when user wants to submit review
     */
    private void showConfirmationDialog() {
        CustomDialog.Builder builder = DialogHelper.getThemedDialogBuilder(ReviewActivity.this, "Confirm submission", "Submit");
        builder.negativeText("Cancel");
        builder.content("Please ensure that all information are correct before submitting.");
        final CustomDialog dialog = builder.build();
        dialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                dialog.dismiss();
                submitReview();
            }

            @Override
            public void onCancelClick() {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * Method called whenever the review is to be submitted
     */
    private void submitReview() {
        String[] reviewDataArray = new String[mCurrentPageSequence.size() + 2];
        int i = 0;

        for (Page page : mCurrentPageSequence) {
            Bundle data = page.getData();
            String extractedData = data.getString(Page.SIMPLE_DATA_KEY);
            reviewDataArray[i++] = extractedData == null ? "" : extractedData;
            Log.d("Review", "Data: " + data.getString(Page.SIMPLE_DATA_KEY));
        }

        String data1 = reviewDataArray[0];
        String data2 = reviewDataArray[1];
        String data3 = reviewDataArray[2];
        String data4 = reviewDataArray[3];
        String data5 = reviewDataArray[4];
        String image = reviewDataArray[5];
        String userId = UserHelper.getCurrentUser(ReviewActivity.this).getId();
        String productId = mProduct.getId();

        Managers.getReviewManager().submitReview(data1, data2, data3, data4, data5, image, userId, productId, new ResultCallback<Boolean>() {
            @Override
            public void onResult(Boolean data) {
                mProgressDialog.dismiss();
                if (data) {
                    Toast.makeText(ReviewActivity.this, "Review submitted!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ReviewActivity.this, "An unknown error occured, please try again later!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (mProgressDialog == null) {
            mProgressDialog = DialogHelper.getProgressDialog(this);
        }
        mProgressDialog.show();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

}