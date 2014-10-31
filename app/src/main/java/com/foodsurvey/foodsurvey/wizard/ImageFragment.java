package com.foodsurvey.foodsurvey.wizard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodsurvey.foodsurvey.R;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * UI to allow user to upload an image for review
 */
public class ImageFragment extends Fragment implements ImageChooserListener {

    private static final String NEW_IMAGE_URI = "new_image_uri";

    protected static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private Page mPage;

    @InjectView(R.id.imageView)
    ImageView imageView;

    @InjectView(android.R.id.title)
    TextView mTitleText;

    private Uri mNewImageUri;

    private int chooserType;
    private ImageChooserManager imageChooserManager;
    private String imagePath;

    public static ImageFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ImageFragment f = new ImageFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        if (savedInstanceState != null) {
            String uriString = savedInstanceState.getString(NEW_IMAGE_URI);
            if (!TextUtils.isEmpty(uriString)) {
                mNewImageUri = Uri.parse(uriString);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNewImageUri != null) {
            outState.putString(NEW_IMAGE_URI, mNewImageUri.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_image,
                container, false);
        ButterKnife.inject(this, rootView);
        mTitleText.setText(mKey);

        String imageData = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
        if (!TextUtils.isEmpty(imageData)) {
            Uri imageUri = Uri.parse(imageData);
            imageView.setImageURI(imageUri);
        }

        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String[] options = {"Take photo", "Choose image"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

        return rootView;
    }

    public void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                chooserType, "myfolder", true);
        imageChooserManager.setImageChooserListener(this);
        try {
            imagePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                chooserType, "myfolder", true);
        imageChooserManager.setImageChooserListener(this);
        try {
            imagePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException(
                    "Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        }
    }

    private void writeResult() {
        mPage.getData().putString(Page.SIMPLE_DATA_KEY,
                (imagePath != null) ? imagePath : null);
        mPage.notifyDataChanged();
    }

    // Should be called if for some reason the ImageChooserManager is null (Due
    // to destroying of activity for low memory situations)
    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType,
                "myfolder", true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(imagePath);
    }

    @Override
    public void onImageChosen(ChosenImage chosenImage) {
        if (chosenImage != null) {
//            imagePath = chosenImage.getFileThumbnail();
            imagePath = new File(chosenImage
                    .getFileThumbnail()).toString();
            writeResult();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageURI(Uri.parse(imagePath));
                }
            });
        }
    }

    @Override
    public void onError(String s) {

    }
}
