package com.foodsurvey.foodsurvey.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.data.Product;
import com.foodsurvey.foodsurvey.data.Review;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created on 25/10/14.
 */
public class AdminReviewDetailActivity extends ActionBarActivity {
    public static final String ARG_REVIEW = "review";
    public static final String ARG_PRODUCT = "product";

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.q1_desc)
    TextView mQ1DescText;

    @InjectView(R.id.q1_score)
    ProgressBar mQ1Score;

    @InjectView(R.id.q2_desc)
    TextView mQ2DescText;

    @InjectView(R.id.q2_score)
    ProgressBar mQ2Score;

    @InjectView(R.id.q3_desc)
    TextView mQ3DescText;

    @InjectView(R.id.q3_score)
    ProgressBar mQ3Score;

    @InjectView(R.id.q4_desc)
    TextView mQ4DescText;

    @InjectView(R.id.q4_score)
    ProgressBar mQ4Score;

    @InjectView(R.id.text_feedback)
    TextView mFeedbackText;

    @InjectView(R.id.image)
    ImageView mImage;

    @InjectView(R.id.age_group)
    TextView mAgeGroupText;

    private Review mReview;
    private Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_review_detail);

        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        Gson gson = new Gson();
        mReview = new Gson().fromJson(bundle.getString(ARG_REVIEW), Review.class);
        mProduct = new Gson().fromJson(bundle.getString(ARG_PRODUCT), Product.class);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.setTitle("Review");
        mToolbar.setSubtitle(mProduct.getTitle());
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initializeWithData();
    }

    /**
     * Method to initialize the UI using obtained data about the review
     */
    private void initializeWithData() {
        int q1Score = Integer.parseInt(mReview.getData1());
        int q2Score = Integer.parseInt(mReview.getData2());
        int q3Score = Integer.parseInt(mReview.getData3());
        int q4Score = Integer.parseInt(mReview.getData4());

        mQ1Score.setProgress(q1Score);
        mQ2Score.setProgress(q2Score);
        mQ3Score.setProgress(q3Score);
        mQ4Score.setProgress(q4Score);

        Resources res = getResources();
        mQ1DescText.setText(res.getStringArray(R.array.q1_options)[q1Score - 1]);
        mQ2DescText.setText(res.getStringArray(R.array.q2_options)[q2Score - 1]);
        mQ3DescText.setText(res.getStringArray(R.array.q3_options)[q3Score - 1]);
        mQ4DescText.setText(res.getStringArray(R.array.q4_options)[q4Score - 1]);

        if (!TextUtils.isEmpty(mReview.getData5()))
            mFeedbackText.setText(mReview.getData5());
        else
            mFeedbackText.setText("(None)");

        if (!TextUtils.isEmpty(mReview.getImageUrl()))
            Picasso.with(this).load(mReview.getImageUrl()).into(mImage);

        mAgeGroupText.setText(mReview.getAgeGroup());
    }
}
