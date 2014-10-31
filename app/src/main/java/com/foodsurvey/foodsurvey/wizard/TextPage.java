package com.foodsurvey.foodsurvey.wizard;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * A page which allows for text-based data
 */
public class TextPage extends Page {

	public TextPage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return TextFragment.create(getKey());
	}


	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
	}

	public TextPage setValue(String value) {
		mData.putString(SIMPLE_DATA_KEY, value);
		return this;
	}
}
