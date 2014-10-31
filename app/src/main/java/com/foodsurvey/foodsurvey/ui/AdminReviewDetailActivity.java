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
 * UI for the administrator to view the detail of a review
 */
public class AdminReviewDetailActivity extends ActionBarActivity {

    /**
     * Argument for the {@link com.foodsurvey.foodsurvey.data.Review} parcelable to be passed into the activity
     */
    public static final String ARG_REVIEW = "review";

    /**
     * Argument for the {@link com.foodsurvey.foodsurvey.data.Product} parcelable to be passed into the activity
     */
    public static final String ARG_PRODUCT = "product";

    /**
     * Toolbar for the activity
     */
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * UI to display the description for the 1st question of the review
     */
    @InjectView(R.id.q1_desc)
    TextView mQ1DescText;

    /**
     * UI to display the score given by the surveyee for question 1
     */
    @InjectView(R.id.q1_score)
    ProgressBar mQ1Score;

    /**
     * UI to display the description for the 2nd question of the review
     */
    @InjectView(R.id.q2_desc)
    TextView mQ2DescText;

    /**
     * UI to display the score given by the surveyee for question 2
     */
    @InjectView(R.id.q2_score)
    ProgressBar mQ2Score;

    /**
     * UI to display the description for the 3rd question of the review
     */
    @InjectView(R.id.q3_desc)
    TextView mQ3DescText;

    /**
     * UI to display the score given by the surveyee for question 3
     */
    @InjectView(R.id.q3_score)
    ProgressBar mQ3Score;

    /**
     * UI to display the description for the 4th question of the review
     */
    @InjectView(R.id.q4_desc)
    TextView mQ4DescText;

    /**
     * UI to display the score given by the surveyee for question 4
     */
    @InjectView(R.id.q4_score)
    ProgressBar mQ4Score;

    /**
     * UI to display the text feedback given by the surveyee
     */
    @InjectView(R.id.text_feedback)
    TextView mFeedbackText;

    /**
     * UI to display the image feedback given by the surveyee
     */
    @InjectView(R.id.image)
    ImageView mImage;


    /**
     * UI to display the age group of the surveyee
     */
    @InjectView(R.id.age_group)
    TextView mAgeGroupText;

    /**
     * Review entity for which the activity is showing the details
     */
    private Review mReview;

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
