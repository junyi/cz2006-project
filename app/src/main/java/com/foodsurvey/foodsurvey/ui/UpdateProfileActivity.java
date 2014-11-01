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
import com.foodsurvey.foodsurvey.control.Managers;
import com.foodsurvey.foodsurvey.control.ResultCallback;
import com.foodsurvey.foodsurvey.entity.User;
import com.foodsurvey.foodsurvey.utility.DialogHelper;
import com.foodsurvey.foodsurvey.utility.UserHelper;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * UI to allow surveyee or administrator to edit/update their profile
 *
 * @author Jomain Tan
 */
public class UpdateProfileActivity extends ActionBarActivity {

    /**
     * Toolbar for the activity
     */
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * UI to display the first name of the user
     */
    @InjectView(R.id.lastname)
    EditText mFirstnameEditText;

    /**
     * UI to display the last name of the user
     */
    @InjectView(R.id.firstname)
    EditText mLastnameEditText;

    /**
     * UI to display the e-mail of the user
     */
    @InjectView(R.id.email)
    EditText mEmailEditText;

    /**
     * UI for user to choose his or her age group
     */
    @InjectView(R.id.age_group)
    Spinner mAgeGroupSpinner;

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
     * Progress dialog for the activity
     */
    private Dialog mProgressDialog = null;

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState Bundle which contains any saved data
     */
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

    /**
     * Called to pre-fill the UI with user's existing profile info
     */
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
                if (mProgressDialog == null)
                    mProgressDialog = DialogHelper.getProgressDialog(this);
                mProgressDialog.show();

                Managers.getUserManager().updateProfile(this, userId, firstName, lastName, email, ageGroup, new ResultCallback<Boolean>() {
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
