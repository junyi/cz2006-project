package com.foodsurvey.foodsurvey.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.control.Managers;
import com.foodsurvey.foodsurvey.control.ResultCallback;
import com.foodsurvey.foodsurvey.entity.Product;
import com.foodsurvey.foodsurvey.ui.widget.AspectRatioImageView;
import com.foodsurvey.foodsurvey.ui.widget.PaperButton;
import com.foodsurvey.foodsurvey.utility.UserHelper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.grantland.widget.AutofitHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * UI for the surveyee to view the detail of a product
 *
 * @author Lee Shei Pin
 */
public class ProductDetailActivity extends ActionBarActivity {
    /**
     * Argument for the {@link com.foodsurvey.foodsurvey.entity.Product} parcelable to be passed into the activity
     */
    public static final String ARG_PRODUCT = "product";

    /**
     * UI to display the image of the product
     */
    @InjectView(R.id.product_image_banner)
    AspectRatioImageView mProductImageBanner;

    /**
     * UI to display the product title
     */
    @InjectView(R.id.product_title)
    TextView mProductTitleText;

    /**
     * UI to display the product description
     */
    @InjectView(R.id.product_description)
    TextView mProductDescText;

    /**
     * UI to display the packaging type of the product
     */
    @InjectView(R.id.product_package_type)
    TextView mProductPackageTypeText;

    /**
     * UI to display the company name of the product
     */
    @InjectView(R.id.company_name)
    TextView mCompanyNameText;

    /**
     * Button for surveyee to initiate the review
     */
    @InjectView(R.id.review_button)
    PaperButton mReviewButton;

    /**
     * Toolbar for the activity
     */
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * Product entity which provides the details about the product
     */
    private Product mProduct;

    /**
     * On click listener for the review button when the product is allowed to be reviewed
     */
    private final View.OnClickListener reviewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ProductDetailActivity.this, ReviewActivity.class);
            intent.putExtra(ReviewActivity.ARG_PRODUCT, new Gson().toJson(mProduct));
            startActivity(intent);
        }
    };

    /**
     * On click listener for the review button when the product is not allowed to be reviewed
     * Instead, the button will allow the user to share the review
     */
    private View.OnClickListener shareReviewClickListener;

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState Bundle which contains any saved data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_detail);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.abc_ic_clear_mtrl_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        mProduct = new Gson().fromJson(bundle.getString(ARG_PRODUCT), Product.class);

        initializeWithData();
    }

    /**
     * This method is called to initialize the UI with data (whenever the activity is recreated)
     */
    private void initializeWithData() {
        mProductTitleText.setText(mProduct.getTitle());

        AutofitHelper.create(mProductTitleText);

        mCompanyNameText.setText("Company: " + mProduct.getCompanyName());

        mProductPackageTypeText.setText("Packaging type: " + mProduct.getPackageType());

        if (TextUtils.isEmpty(mProduct.getDescription())) {
            mProductDescText.setText("(No description)");
        } else {
            mProductDescText.setText(mProduct.getDescription());
        }

        String bannerImageUrl = mProduct.getImageUrl();

        Picasso.with(this)
                .load(bannerImageUrl)
                .into(mProductImageBanner);

        String userId = UserHelper.getCurrentUser(this).getId();
        String productId = mProduct.getId();
//        System.out.printf("UserId: %s, productId: %s\n", userId, productId);

        mReviewButton.setEnabled(false);
        Managers.getReviewManager().checkIfReviewExists(userId, productId, new ResultCallback<String>() {
            @Override
            public void onResult(String reviewId) {
                mReviewButton.setEnabled(true);
                if (reviewId != null) {
                    mReviewButton.setText("SHARE REVIEW");
                    setupShareButton(reviewId);
                } else {
                    mReviewButton.setText("REVIEW");
                    mReviewButton.setOnClickListener(reviewClickListener);
                }
            }
        });

    }

    /**
     * Method to setup the sharing button
     *
     * @param reviewId ID of the review
     */
    private void setupShareButton(final String reviewId) {
        shareReviewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = String.format("I just reviewed %s on the PackagingStudy," +
                        " check out my review here:\nhttp://packaging-study.parseapp.com/review?id=%s", mProduct.getTitle(), reviewId);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "PackagingStudy Review");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
            }
        };
        mReviewButton.setOnClickListener(shareReviewClickListener);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

}
