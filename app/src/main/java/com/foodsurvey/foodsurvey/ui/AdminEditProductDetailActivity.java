package com.foodsurvey.foodsurvey.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.foodsurvey.foodsurvey.data.Controllers;
import com.foodsurvey.foodsurvey.data.Product;
import com.foodsurvey.foodsurvey.data.ProductController;
import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.data.ResultCallback;
import com.foodsurvey.foodsurvey.data.UserHelper;
import com.foodsurvey.foodsurvey.ui.widget.AspectRatioImageView;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.grantland.widget.AutofitHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * UI for Administrators to add/edit products.
 */
public class AdminEditProductDetailActivity extends ActionBarActivity implements ImageChooserListener {
    public static final String KEY_PRODUCT = "product";


    @InjectView(R.id.product_image_banner)
    AspectRatioImageView mProductImageBanner;

    @InjectView(R.id.product_title)
    TextView mProductTitleText;

    @InjectView(R.id.company_name)
    TextView mCompanyNameText;

    @InjectView(R.id.product_package_type)
    Spinner mProductPackageType;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    private int mChooserType;
    private ImageChooserManager mImageChooserManager;
    private String mImagePath;

    private Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_edit_product_detail);

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
        if (bundle != null) {
            mProduct = Parcels.unwrap(bundle.getParcelable(KEY_PRODUCT));
            initialize();
        }

        //
        final String[] options = {"Take photo", "Choose image"};
        mProductImageBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminEditProductDetailActivity.this);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                takePicture();
                                break;
                            default:
                                chooseImage();
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void initialize() {

        mProductTitleText.setText(mProduct.getCompanyName() + " " + mProduct.getTitle());

        AutofitHelper.create(mProductTitleText);

        mCompanyNameText.setText(mProduct.getCompanyName());

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
        getMenuInflater().inflate(R.menu.menu_admin_edit_product_detail, menu);
        return true;
    }


    public void chooseImage() {
        mChooserType = ChooserType.REQUEST_PICK_PICTURE;
        mImageChooserManager = new ImageChooserManager(this,
                mChooserType, "myfolder", true);
        mImageChooserManager.setImageChooserListener(this);
        try {
//            pbar.setVisibility(View.VISIBLE);
            mImagePath = mImageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takePicture() {
        mChooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        mImageChooserManager = new ImageChooserManager(this,
                mChooserType, "myfolder", true);
        mImageChooserManager.setImageChooserListener(this);
        try {
//            pbar.setVisibility(View.VISIBLE);
            mImagePath = mImageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Should be called if for some reason the ImageChooserManager is null (Due
    // to destroying of activity for low memory situations)
    private void reinitializeImageChooser() {
        mImageChooserManager = new ImageChooserManager(this, mChooserType,
                "myfolder", true);
        mImageChooserManager.setImageChooserListener(this);
        mImageChooserManager.reinitialize(mImagePath);
    }

    @Override
    public void onImageChosen(ChosenImage chosenImage) {
        if (chosenImage != null) {
            mImagePath = new File(chosenImage
                    .getFileThumbnail()).toString();
            Log.d("Image path", mImagePath);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProductImageBanner.setImageURI(Uri.parse(mImagePath));
                }
            });
        }
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (mImageChooserManager == null) {
                reinitializeImageChooser();
            }
            mImageChooserManager.submit(requestCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                onSaveButtonPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSaveButtonPressed() {
        String companyId = UserHelper.getCurrentUser(this).getCompanyId();
        String title = mProductTitleText.getEditableText().toString();
        String description = mCompanyNameText.getEditableText().toString();
        String packageType = (String) mProductPackageType.getSelectedItem();
        if (mProduct == null) {
            Controllers.getProductController().createProduct(companyId, title, description, packageType, mImagePath, new ResultCallback<Boolean>() {
                @Override
                public void onResult(Boolean data) {
                    Toast.makeText(AdminEditProductDetailActivity.this, "Product successfully created.", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            Controllers.getProductController().updateProduct(mProduct.getId(), title, description, packageType, mImagePath, new ResultCallback<Boolean>() {
                @Override
                public void onResult(Boolean data) {
                    Toast.makeText(AdminEditProductDetailActivity.this, "Product successfully updated.", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
