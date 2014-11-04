package com.foodsurvey.foodsurvey.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.control.Managers;
import com.foodsurvey.foodsurvey.control.ResultCallback;
import com.foodsurvey.foodsurvey.entity.Product;
import com.foodsurvey.foodsurvey.ui.widget.AspectRatioImageView;
import com.foodsurvey.foodsurvey.utility.DialogHelper;
import com.foodsurvey.foodsurvey.utility.UserHelper;
import com.google.gson.Gson;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.grantland.widget.AutofitHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * UI for Administrators to add/edit products.
 *
 * @author Jomain Tan
 */
public class AdminEditProductDetailActivity extends ActionBarActivity implements ImageChooserListener {

    /**
     * Argument for the {@link com.foodsurvey.foodsurvey.entity.Product} parcelable to be passed into the activity
     */
    public static final String ARG_PRODUCT = "product";

    /**
     * UI to show the product image
     */
    @InjectView(R.id.product_image_banner)
    AspectRatioImageView mProductImageBanner;

    /**
     * UI to enter the product title
     */
    @InjectView(R.id.product_title)
    EditText mProductTitleText;

    /**
     * UI to show the description of the product
     */
    @InjectView(R.id.description)
    EditText mDescriptionText;

    /**
     * UI to choose the packaging type of the product
     */
    @InjectView(R.id.product_package_type)
    Spinner mProductPackageType;

    /**
     * Toolbar for the activity
     */
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * Progress dialog for the activity
     */
    private Dialog mProgressDialog = null;

    private int mChooserType;
    private ImageChooserManager mImageChooserManager;

    /**
     * Image path of the product image (on the device)
     */
    private String mImagePath;

    /**
     * Indicates if the image has been updated or not
     */
    private boolean mImageChanged = false;

    /**
     * Product entity for which the details are being edited
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
            mProduct = new Gson().fromJson(bundle.getString(ARG_PRODUCT), Product.class);
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

    /**
     * Method to populate the UI with data passed from another activity
     */
    private void initialize() {

        mProductTitleText.setText(mProduct.getTitle());

        AutofitHelper.create(mProductTitleText);

        mDescriptionText.setText(mProduct.getDescription());

        int position = 0;
        String[] packagingTypes = getResources().getStringArray(R.array.packaging_types);
        for (int i = 0; i < packagingTypes.length; i++) {
            if (packagingTypes[i].equals(mProduct.getPackageType())) {
                position = i;
                break;
            }
        }
        mProductPackageType.setSelection(position);

        String bannerImageUrl = mProduct.getImageUrl();

        Picasso.with(this)
                .load(bannerImageUrl)
                .into(mProductImageBanner);

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return Returns true for the menu to be displayed, and false if otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_edit_product_detail, menu);
        return true;
    }

    /**
     * Called when the user choose an image
     */
    private void chooseImage() {
        mChooserType = ChooserType.REQUEST_PICK_PICTURE;
        mImageChooserManager = new ImageChooserManager(this,
                mChooserType, "myfolder", true);
        mImageChooserManager.setImageChooserListener(this);
        try {
            mImagePath = mImageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the user takes a picture
     */
    private void takePicture() {
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

    /**
     * Callback from the image chooser when the image has been choosen
     *
     * @param chosenImage The chosen image
     */
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

    /**
     * Callback from the image choose when an error occured
     *
     * @param s The error message
     */
    @Override
    public void onError(String s) {

    }

    /**
     * Called when a result called using {@link android.app.Activity#startActivityForResult(android.content.Intent, int)} returns to the activity
     *
     * @param requestCode Request code
     * @param resultCode  Result code
     * @param data        Data of the result
     */
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

    /**
     * Called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
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

    /**
     * Called when the save button on the menu is pressed.
     * Validates the information before submitting.
     * Creates a new product or updates the existing product.
     */
    private void onSaveButtonPressed() {
        View focusView = null;
        boolean cancel = false;
        String companyId = UserHelper.getCurrentUser(this).getCompanyId();
        String title = mProductTitleText.getEditableText().toString();
        String description = mDescriptionText.getEditableText().toString();
        String packageType = (String) mProductPackageType.getSelectedItem();


        // Check for a valid product title
        if (TextUtils.isEmpty(title)) {
            mProductTitleText.setError(getString(R.string.error_field_required));
            focusView = mProductTitleText;
            cancel = true;
        }

        // Check for a valid product description
        if (TextUtils.isEmpty(description)) {
            mDescriptionText.setError(getString(R.string.error_field_required));
            focusView = mDescriptionText;
            cancel = true;
        }

        if (mProduct == null && TextUtils.isEmpty(mImagePath)) {
            Toast.makeText(this, "You must include an image for the product!", Toast.LENGTH_SHORT).show();
            cancel = true;
        } else if (mProduct != null && mImageChanged && mImagePath == null) {
            Toast.makeText(this, "You must include an image for the product!", Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        if (cancel) {
            if (focusView != null)
                focusView.requestFocus();
            return;
        }

        if (mProduct == null) {
            Managers.getProductManager().createProduct(companyId, title, description, packageType, mImagePath, new ResultCallback<Boolean>() {
                @Override
                public void onResult(Boolean data) {
                    mProgressDialog.dismiss();
                    if (true) {
                        Toast.makeText(AdminEditProductDetailActivity.this, "Product successfully created.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminEditProductDetailActivity.this, "An unknown error occured, please try again later!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            String imageUrl = mImageChanged ? null : mProduct.getImageUrl();
            String imagePath = mImageChanged ? mImagePath : null;

            Managers.getProductManager().updateProduct(mProduct.getId(), title, description, packageType, imageUrl, imagePath, new ResultCallback<Product>() {
                @Override
                public void onResult(Product product) {
                    Intent data = new Intent();
                    String message;
                    int result = -1;
                    mProgressDialog.dismiss();
                    if (product != null) {
                        message = "Product successfully updated!";
                        data.putExtra(AdminProductDetailActivity.ARG_PRODUCT, new Gson().toJson(product));
                        result = RESULT_OK;
                    } else {
                        message = "Product failed to update!";
                    }

                    Toast.makeText(AdminEditProductDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    setResult(result, data);
                    finish();
                }
            });
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }
}
