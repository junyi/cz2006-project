package com.foodsurvey.foodsurvey.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.data.Managers;
import com.foodsurvey.foodsurvey.data.ResultCallback;
import com.foodsurvey.foodsurvey.data.User;
import com.foodsurvey.foodsurvey.utility.DialogHelper;
import com.foodsurvey.foodsurvey.utility.UserHelper;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UpdateProfileActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.lastname)
    EditText mFirstnameEditText;

    @InjectView(R.id.firstname)
    EditText mLastnameEditText;

    @InjectView(R.id.email)
    EditText mEmailEditText;

    @InjectView(R.id.age_group)
    Spinner mAgeGroupSpinner;

    @InjectView(R.id.age_group_container)
    View mAgeGroupContainer;

    private MainActivity.UserType mUserType;
    private Dialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_profile);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.setTitle("Edit profile");

        Bundle bundle = getIntent().getExtras();
        mUserType = MainActivity.UserType.values()[bundle.getInt(MainActivity.ARG_TYPE, MainActivity.UserType.SURVEYEE.ordinal())];

        initializeWithData();
    }

    private void initializeWithData() {
        User user = UserHelper.getCurrentUser(this);

        mFirstnameEditText.setText(user.getFirstName());
        mLastnameEditText.setText(user.getLastName());
        mEmailEditText.setText(user.getEmail());

        if (mUserType == MainActivity.UserType.ADMIN) {
            mAgeGroupContainer.setVisibility(View.GONE);
        } else {
            String[] ageGroupArray = getResources().getStringArray(R.array.age_groups);
            int position = Arrays.asList(ageGroupArray).indexOf(user.getAgeGroup());

            if (position < 0)
                position = 0;

            mAgeGroupSpinner.setSelection(position);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                String userId = UserHelper.getCurrentUser(this).getId();
                String firstName = mFirstnameEditText.getEditableText().toString();
                String lastName = mLastnameEditText.getEditableText().toString();
                String email = mEmailEditText.getEditableText().toString();
                String ageGroup = null;
                if (mUserType == MainActivity.UserType.SURVEYEE) {
                    ageGroup = (String) mAgeGroupSpinner.getSelectedItem();
                }
                if (mProgressDialog != null)
                    mProgressDialog = DialogHelper.getProgressDialog(this);
                mProgressDialog.show();

                Managers.getUserManager().updateProfile(userId, firstName, lastName, email, ageGroup, new ResultCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean success) {
                        mProgressDialog.dismiss();
                        String message;
                        int result;
                        if (success) {
                            message = "Update successful!";
                            result = RESULT_OK;
                        } else {
                            message = "Update failed!";
                            result = -1;
                        }
                        Toast.makeText(UpdateProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        setResult(result);
                        finish();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
