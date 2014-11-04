package com.foodsurvey.foodsurvey.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.control.Managers;
import com.foodsurvey.foodsurvey.control.ResultCallback;
import com.foodsurvey.foodsurvey.ui.widget.PaperButton;
import com.parse.ParseException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * UI for a surveyee to sign up for a new account
 *
 * @author Huang Jinbin
 */
public class RegistrationActivity extends ActionBarActivity {

    /**
     * Toolbar for the activity
     */
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * UI to display the progress bar when loading
     */
    @InjectView(R.id.sign_up_progress)
    View mProgressView;

    /**
     * UI for the entire sign up form
     */
    @InjectView(R.id.sign_up_form)
    View mSignUpFormView;

    /**
     * UI for the user to enter first name
     */
    @InjectView(R.id.firstname)
    EditText mFirstnameEditText;

    /**
     * UI for user to enter last name
     */
    @InjectView(R.id.lastname)
    EditText mLastnameEditText;

    /**
     * UI for user to enter username
     */
    @InjectView(R.id.username)
    EditText mUsernameEditText;

    /**
     * UI for user to enter password
     */
    @InjectView(R.id.password)
    EditText mPasswordEditText;

    /**
     * UI for user to enter e-mail
     */
    @InjectView(R.id.email)
    EditText mEmailEditText;

    /**
     * Button to submit the sign up data
     */
    @InjectView(R.id.sign_up_button)
    PaperButton mSignUpButton;

    /**
     * UI for user to choose age group
     */
    @InjectView(R.id.age_group)
    Spinner mAgeGroupSpinner;

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState Bundle which contains any saved data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_clear_mtrl_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptSignUp() {
        // Reset errors.
        mUsernameEditText.setError(null);
        mEmailEditText.setError(null);
        mPasswordEditText.setError(null);
        mFirstnameEditText.setError(null);
        mLastnameEditText.setError(null);

        // Store values at the time of the login attempt.
        String firstName = mFirstnameEditText.getEditableText().toString();
        String lastName = mLastnameEditText.getEditableText().toString();
        String username = mUsernameEditText.getEditableText().toString();
        String email = mEmailEditText.getEditableText().toString();
        String password = mPasswordEditText.getEditableText().toString();
        String ageGroup = (String) mAgeGroupSpinner.getSelectedItem();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameEditText.setError(getString(R.string.error_field_required));
            focusView = mUsernameEditText;
            cancel = true;
        }

        // Check for a valid first name
        if (TextUtils.isEmpty(firstName)) {
            mFirstnameEditText.setError(getString(R.string.error_field_required));
            focusView = mFirstnameEditText;
            cancel = true;
        }

        // Check for a valid last name
        if (TextUtils.isEmpty(lastName)) {
            mLastnameEditText.setError(getString(R.string.error_field_required));
            focusView = mLastnameEditText;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordEditText.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Managers.getUserManager().signUp(firstName, lastName, username, ageGroup, password, email, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer data) {
                    onSignUpResult(data);
                }
            });
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Helper function to check if email is valid
     *
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    }

    /**
     * Helper function to check if password is valid
     *
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Called when the result of the sign up is returned
     *
     * @param errorCode Error code of the result if any
     */
    private void onSignUpResult(Integer errorCode) {
        showProgress(false);

        if (errorCode == 0) {
            Toast.makeText(RegistrationActivity.this, "Sign up success! Please login to continue.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(RegistrationActivity.this, "Sign up failed!", Toast.LENGTH_SHORT).show();
            switch (errorCode) {
                case ParseException.USERNAME_TAKEN:
                    mUsernameEditText.setError("This username has been taken.");
                    mUsernameEditText.requestFocus();
                    break;
                case ParseException.INVALID_EMAIL_ADDRESS:
                    mEmailEditText.setError("Invalid email address.");
                    mEmailEditText.requestFocus();
                    break;
                case ParseException.EMAIL_TAKEN:
                    mEmailEditText.setError("This email is already linked to another user.");
                    mEmailEditText.requestFocus();
                    break;
                default:
                    Toast.makeText(RegistrationActivity.this, "An unknown error occured, please try again later!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }
}
