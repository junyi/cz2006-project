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
import com.foodsurvey.foodsurvey.entity.User;
import com.foodsurvey.foodsurvey.ui.widget.AspectRatioImageView;
import com.foodsurvey.foodsurvey.utility.UserHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * UI for both surveyees and administrators to view their profile
 *
 * @author Jomain Tan
 */
public class ProfileFragment extends Fragment {

    /**
     * UI to display the background of the profile
     */
    @InjectView(R.id.profile_background)
    AspectRatioImageView mProfileBackground;

    /**
     * UI to show the display name of the user
     */
    @InjectView(R.id.display_name)
    TextView mDisplayNameText;

    /**
     * UI to show the display name of the user
     */
    @InjectView(R.id.username)
    TextView mUsernameText;

    /**
     * UI to show the company name of the user (if any)
     */
    @InjectView(R.id.company_name)
    TextView mCompanyNameText;

    /**
     * UI to show the first name of the user
     */
    @InjectView(R.id.firstname)
    TextView mFirstNameText;

    /**
     * UI to show the last name of the user
     */
    @InjectView(R.id.lastname)
    TextView mLastNameText;

    /**
     * UI to show the e-mail of the user
     */
    @InjectView(R.id.email)
    TextView mEmailText;

    /**
     * UI to show the age group of the surveyee
     */
    @InjectView(R.id.age_group)
    TextView mAgeGroupText;

    /**
     * View container for the age group (to be set to hidden when the user type is administrator)
     */
    @InjectView(R.id.age_group_container)
    View mAgeGroupContainer;

    /**
     * Used to indicate the type of user
     */
    private MainActivity.UserType mUserType;

    /**
     * Required empty constructor
     */
    public ProfileFragment() {

    }

    /**
     * Static constructor pattern for fragment
     *
     * @param userType Type of user
     * @return the new fragment
     */
    public static ProfileFragment newInstance(MainActivity.UserType userType) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MainActivity.ARG_TYPE, userType.ordinal());
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Called whenever a view is to be created
     *
     * @param inflater           inflater to inflate the layout
     * @param container          parent viewgroup of the view
     * @param savedInstanceState saved bundle of data
     * @return the inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, view);

        mUserType = MainActivity.UserType.values()[getArguments().getInt(MainActivity.ARG_TYPE, MainActivity.UserType.SURVEYEE.ordinal())];

        return view;
    }

    /**
     * Called once the view is created
     *
     * @param view               the created view
     * @param savedInstanceState the saved bundle of data
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeWithData();
    }

    /**
     * Called to initialize the UI with profile dataa
     */
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
        mUsernameText.setText(user.getUsername());

    }
}
