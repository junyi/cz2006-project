package com.foodsurvey.foodsurvey.ui;

import android.app.Dialog;
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
import com.foodsurvey.foodsurvey.control.Managers;
import com.foodsurvey.foodsurvey.entity.Product;
import com.foodsurvey.foodsurvey.control.ResultCallback;
import com.foodsurvey.foodsurvey.ui.widget.AspectRatioImageView;
import com.foodsurvey.foodsurvey.ui.widget.PaperButton;
import com.foodsurvey.foodsurvey.utility.DialogHelper;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.grantland.widget.AutofitHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * UI for administrator to view detail of a product
 *
 * @author Lee Shei Pin
 */
public class AdminProductDetailActivity extends ActionBarActivity {

    /**
     * Argument for the {@link com.foodsurvey.foodsurvey.entity.Product} parcelable to be passed into the activity
     */
    public static final String ARG_PRODUCT = "product";

    /**
     * Request code of the intent which is used to start {@link com.foodsurvey.foodsurvey.ui.AdminEditProductDetailActivity}
     */
    private static final int REQUEST_UPDATE_PRODUCT = 1;

    /**
     * UI to show the image of the product
     */
    @InjectView(R.id.product_image_banner)
    AspectRatioImageView mProductImageBanner;

    /**
     * UI to show the product title
     */
    @InjectView(R.id.product_title)
    TextView mProductTitleText;

    /**
     * UI to show the company name of the product
     */
    @InjectView(R.id.company_name)
    TextView mCompanyNameText;

    /**
     * UI to show the product description
     */
    @InjectView(R.id.product_description)
    TextView mProductDescText;

    /**
     * UI to show the packaging type of the product
     */
    @InjectView(R.id.product_package_type)
    TextView mProductPackageTypeText;

    /**
     * Button for the administrator to view the reviews of the product
     */
    @InjectView(R.id.view_review_button)
    PaperButton mViewReviewButton;

    /**
     * Toolbar of the activity
     */
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * Product entity for which the activity is showing the details
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
        mProduct = new Gson().fromJson(bundle.getString(ARG_PRODUCT), Product.class);

        mViewReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProductDetailActivity.this, AdminReviewListActivity.class);
                intent.putExtra(AdminReviewListActivity.ARG_PRODUCT, new Gson().toJson(mProduct));
                startActivity(intent);
            }
        });

        initializeWithData();
    }

    /**
     * Method to populate the UI with data passed from another activity
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
                intent.putExtra(AdminEditProductDetailActivity.ARG_PRODUCT, new Gson().toJson(mProduct));
                startActivityForResult(intent, REQUEST_UPDATE_PRODUCT);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_UPDATE_PRODUCT) {
                if (mProgressDialog != null)
                    mProgressDialog = DialogHelper.getProgressDialog(this);
                mProgressDialog.show();

                Managers.getProductManager().getProductById(mProduct.getId(), new ResultCallback<Product>() {
                    @Override
                    public void onResult(Product data) {
                        mProgressDialog.dismiss();
                        if (data != null) {
                            mProduct = data;
                            initializeWithData();
                        }
                    }
                });
            }
        }
    }

}
