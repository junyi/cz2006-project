package com.foodsurvey.foodsurvey.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.data.User;
import com.foodsurvey.foodsurvey.ui.widget.AspectRatioImageView;
import com.foodsurvey.foodsurvey.utility.UserHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileFragment extends Fragment {

    @InjectView(R.id.profile_background)
    AspectRatioImageView mProfileBackground;

    @InjectView(R.id.display_name)
    TextView mDisplayNameText;

    @InjectView(R.id.company_name)
    TextView mCompanyNameText;

    @InjectView(R.id.firstname)
    TextView mFirstNameText;

    @InjectView(R.id.lastname)
    TextView mLastNameText;

    @InjectView(R.id.email)
    TextView mEmailText;

    @InjectView(R.id.age_group)
    TextView mAgeGroupText;

    @InjectView(R.id.age_group_container)
    View mAgeGroupContainer;

    private MainActivity.UserType mUserType;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance(MainActivity.UserType userType) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MainActivity.ARG_TYPE, userType.ordinal());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, view);

        mUserType = MainActivity.UserType.values()[getArguments().getInt(MainActivity.ARG_TYPE, MainActivity.UserType.SURVEYEE.ordinal())];

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeWithData();
    }

    public void initializeWithData() {
        Resources res = getResources();
        User user = UserHelper.getCurrentUser(getActivity());

        if (mUserType == MainActivity.UserType.SURVEYEE) {
            mProfileBackground.setImageDrawable(res.getDrawable(R.drawable.background));
            mDisplayNameText.setTextColor(res.getColor(android.R.color.white));
            mCompanyNameText.setVisibility(View.GONE);
            mAgeGroupText.setText(user.getAgeGroup());
        } else {
            mCompanyNameText.setText(user.getCompanyName());
            mAgeGroupContainer.setVisibility(View.GONE);
        }

        mDisplayNameText.setText(user.getFirstName() + " " + user.getLastName());
        mFirstNameText.setText(user.getFirstName());
        mLastNameText.setText(user.getLastName());
        mEmailText.setText(user.getEmail());

    }
}
