package com.foodsurvey.foodsurvey.wizard;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A page which allows for single and fixed choice data
 */
public class SingleFixedChoicePage extends Page {
    protected ArrayList<String> mChoices = new ArrayList<String>();
    protected String mSubtitle;

    public SingleFixedChoicePage(ModelCallbacks callbacks, String key) {
        super(callbacks, key);
    }

    public SingleFixedChoicePage(ModelCallbacks callbacks, String key, String subtitle) {
        super(callbacks, key);
        mSubtitle = subtitle;
    }

    @Override
    public Fragment createFragment() {
        return SingleChoiceFragment.create(mKey, mSubtitle);
    }

    public String getOptionAt(int position) {
        return mChoices.get(position);
    }

    public int getOptionCount() {
        return mChoices.size();
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    public SingleFixedChoicePage setChoices(String... choices) {
        mChoices.addAll(Arrays.asList(choices));
        return this;
    }

    public SingleFixedChoicePage addChoice(String choice) {
        mChoices.add(choice);
        return this;
    }

    public SingleFixedChoicePage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }
}