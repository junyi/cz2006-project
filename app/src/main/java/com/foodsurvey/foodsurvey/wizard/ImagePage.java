package com.foodsurvey.foodsurvey.wizard;

import android.support.v4.app.Fragment;

/**
 * A page which allows for image-based data
 *
 * @author Hee Jun Yi
 */
public class ImagePage extends TextPage {

    public ImagePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return ImageFragment.create(getKey());
    }

    public ImagePage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }
}
