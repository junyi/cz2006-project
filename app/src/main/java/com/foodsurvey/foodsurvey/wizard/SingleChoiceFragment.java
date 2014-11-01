/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foodsurvey.foodsurvey.wizard;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.foodsurvey.foodsurvey.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * UI for surveyees to choose a single option from a list of options
 *
 * @author Hee Jun Yi
 */
public class SingleChoiceFragment extends ListFragment {
    private static final String ARG_KEY = "key";
    private static final String ARG_SUBTITLE = "subtitle";

    private PageFragmentCallbacks mCallbacks;
    private List<String> mChoices;
    private String mKey;
    private String mSubtitle;
    private Page mPage;

    @InjectView(android.R.id.title)
    TextView mTitleText;

    @InjectView(android.R.id.text1)
    TextView mSubtitleText;

    @InjectView(android.R.id.list)
    ListView mListView;

    public static SingleChoiceFragment create(String key, String subtitle) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        args.putString(ARG_SUBTITLE, subtitle);

        SingleChoiceFragment fragment = new SingleChoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SingleChoiceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mSubtitle = args.getString(ARG_SUBTITLE);
        mPage = mCallbacks.onGetPage(mKey);

        SingleFixedChoicePage fixedChoicePage = (SingleFixedChoicePage) mPage;
        mChoices = new ArrayList<String>();
        for (int i = 0; i < fixedChoicePage.getOptionCount(); i++) {
            mChoices.add(fixedChoicePage.getOptionAt(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        ButterKnife.inject(this, rootView);
        mTitleText.setText(mKey);

        if (mSubtitle != null) {
            mSubtitleText.setVisibility(View.VISIBLE);
            mSubtitleText.setText(mSubtitle);
        }


        setListAdapter(new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_single_choice,
                android.R.id.text1,
                mChoices));
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Pre-select currently selected item.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String selection = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
                int parsedSelection = -1;
                if (selection != null) {
                    parsedSelection = Integer.parseInt(selection) - 1;
                }
                for (int i = 0; i < mChoices.size(); i++) {
                    if (i == parsedSelection) {
                        mListView.setItemChecked(i, true);
                        break;
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // The score for each result is given by position + 1
        mPage.getData().putString(Page.SIMPLE_DATA_KEY, Integer.toString(position + 1));
        mPage.notifyDataChanged();
    }

}
