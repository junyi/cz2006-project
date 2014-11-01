package com.foodsurvey.foodsurvey.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.control.Managers;
import com.foodsurvey.foodsurvey.control.ResultCallback;
import com.foodsurvey.foodsurvey.ui.widget.PaperButton;
import com.foodsurvey.foodsurvey.utility.DialogHelper;
import com.foodsurvey.foodsurvey.utility.UserHelper;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * UI which allows both surveyee and administrator to login
 *
 * @author Lee Shei Pin
 */
public class LoginActivity extends ActionBarActivity {

    /**
     * Button for surveyee to register
     */
    @InjectView(R.id.register_button)
    PaperButton mRegisterButton;

    /**
     * Button for both surveyee and administrator to login
     */
    @InjectView(R.id.login_button)
    PaperButton mLoginButton;

    /**
     * Input for user to enter username
     */
    @InjectView(R.id.username)
    EditText mUsernameText;

    /**
     * Input for user to enter password
     */
    @InjectView(R.id.password)
    EditText mPasswordText;

    /**
     * Progress dialog for the activity
     */
    private Dialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        if (ParseUser.getCurrentUser() != null) {
            onLoginSuccess();
            Log.d("Login", "inside!");
        }

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

//        mDebugButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(LoginActivity.this, ProductListActivity.class);
//                startActivity(i);
//            }
//        });
//        mDebug2Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(i);
//            }
//        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mUsernameText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mPasswordText.getWindowToken(), 0);

        // Reset errors.
        mUsernameText.setError(null);
        mPasswordText.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordText.setError(getString(R.string.error_field_required));
            focusView = mPasswordText;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameText.setError(getString(R.string.error_field_required));
            focusView = mUsernameText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
            Managers.getUserManager().login(this, username, password, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer errorCode) {
                    if (errorCode == 0) {
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        onLoginSuccess();
                    } else {
                        if (mProgressDialog != null)
                            mProgressDialog.dismiss();
                        switch (errorCode) {
                            case ParseException.USERNAME_MISSING:
                                mUsernameText.setError(getString(R.string.error_field_required));
                                mUsernameText.requestFocus();
                                break;
                            default:
                                mPasswordText.setError(getString(R.string.error_incorrect_password));
                                mPasswordText.requestFocus();
                        }

                    }
                }
            });
            if (mProgressDialog == null)
                mProgressDialog = DialogHelper.getProgressDialog(this);
            mProgressDialog.show();
        }
    }

    /**
     * Called whenever a login is successful, used to start the activity based on the type of user
     */
    private void onLoginSuccess() {

        if (mProgressDialog != null)
            mProgressDialog.dismiss();

        Intent intent;
        String companyId = UserHelper.getCurrentUser(this).getCompanyId();

        intent = new Intent(LoginActivity.this, MainActivity.class);
        if (companyId != null && companyId != "") {
            intent.putExtra(MainActivity.ARG_TYPE, MainActivity.UserType.ADMIN.ordinal());
        } else {
            intent.putExtra(MainActivity.ARG_TYPE, MainActivity.UserType.SURVEYEE.ordinal());
        }

        startActivity(intent);
        finish();
    }
}
