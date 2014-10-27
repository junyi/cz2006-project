package com.foodsurvey.foodsurvey.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.data.Product;
import com.foodsurvey.foodsurvey.ui.widget.AspectRatioImageView;
import com.foodsurvey.foodsurvey.ui.widget.PaperButton;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.grantland.widget.AutofitHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AdminProductDetailActivity extends ActionBarActivity {
    public static final String ARG_PRODUCT = "product";

    @InjectView(R.id.product_image_banner)
    AspectRatioImageView mProductImageBanner;

    @InjectView(R.id.product_title)
    TextView mProductTitleText;

    @InjectView(R.id.company_name)
    TextView mCompanyNameText;

    @InjectView(R.id.product_description)
    TextView mProductDescText;

    @InjectView(R.id.product_package_type)
    TextView mProductPackageTypeText;

    @InjectView(R.id.view_review_button)
    PaperButton mViewReviewButton;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;


    private Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_product_detail);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.setTitle("");
        mToolbar.inflateMenu(R.menu.menu_admin_product_detail);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        mProduct = Parcels.unwrap(bundle.getParcelable(ARG_PRODUCT));

        mViewReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProductDetailActivity.this, AdminReviewListActivity.class);
                intent.putExtra(AdminReviewListActivity.ARG_PRODUCT, Parcels.wrap(mProduct));
                startActivity(intent);
            }
        });

        initializeWithData();
    }

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

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_product_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(AdminProductDetailActivity.this, AdminEditProductDetailActivity.class);
                intent.putExtra(AdminEditProductDetailActivity.KEY_PRODUCT, Parcels.wrap(mProduct));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
