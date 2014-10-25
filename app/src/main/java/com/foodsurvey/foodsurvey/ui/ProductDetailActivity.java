package com.foodsurvey.foodsurvey.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.foodsurvey.foodsurvey.Product;
import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.ResultCallback;
import com.foodsurvey.foodsurvey.ReviewController;
import com.foodsurvey.foodsurvey.UserHelper;
import com.foodsurvey.foodsurvey.ui.widget.AspectRatioImageView;
import com.foodsurvey.foodsurvey.ui.widget.PaperButton;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.grantland.widget.AutofitHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductDetailActivity extends ActionBarActivity {
    public static final String ARG_PRODUCT = "product";

    @InjectView(R.id.product_image_banner)
    AspectRatioImageView mProductImageBanner;

    @InjectView(R.id.product_title)
    TextView mProductTitleText;

    @InjectView(R.id.product_package_type)
    TextView mProductPackageTypeText;

    @InjectView(R.id.company_name)
    TextView mCompanyNameText;

    @InjectView(R.id.review_button)
    PaperButton mReviewButton;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    private Product mProduct;
    private final View.OnClickListener reviewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ProductDetailActivity.this, ReviewActivity.class);
            intent.putExtra(ReviewActivity.ARG_PRODUCT, Parcels.wrap(mProduct));
            startActivity(intent);
        }
    };

    private final View.OnClickListener shareReviewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ProductDetailActivity.this, ReviewActivity.class);
            intent.putExtra(ReviewActivity.ARG_PRODUCT, Parcels.wrap(mProduct));
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_detail);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.setTitle("");
        mToolbar.inflateMenu(R.menu.menu_product_detail);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_clear_mtrl_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        mProduct = Parcels.unwrap(bundle.getParcelable(ARG_PRODUCT));

        mReviewButton.setOnClickListener(reviewClickListener);

        initializeWithData();
    }

    /**
     * This method is called to initialize the UI with data (whenever the activity is recreated)
     */
    private void initializeWithData() {
        mProductTitleText.setText(mProduct.getCompanyName() + " " + mProduct.getTitle());

        AutofitHelper.create(mProductTitleText);

        mCompanyNameText.setText("Company: " + mProduct.getCompanyName());

        mProductPackageTypeText.setText("Packaging type: " + mProduct.getPackageType());

        String bannerImageUrl = mProduct.getImageUrl();

        Picasso.with(this)
                .load(bannerImageUrl)
                .into(mProductImageBanner);

        String userId = UserHelper.getCurrentUser(this).getId();
        String productId = mProduct.getId();
        System.out.printf("UserId: %s, productId: %s\n", userId, productId);
        ReviewController.getInstance().checkIfReviewExists(userId, productId, new ResultCallback<Boolean>() {
            @Override
            public void onResult(Boolean exists) {
                if (exists) {
                    mReviewButton.setText("SHARE REVIEW");
                    mReviewButton.setOnClickListener(shareReviewClickListener);
                } else {
                    mReviewButton.setOnClickListener(reviewClickListener);
                }
            }
        });

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
        return true;
    }

}
