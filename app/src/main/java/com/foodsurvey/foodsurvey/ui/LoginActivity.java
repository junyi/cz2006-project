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
import android.widget.EditText;
import android.widget.Toast;

import com.foodsurvey.foodsurvey.R;
import com.foodsurvey.foodsurvey.data.Controllers;
import com.foodsurvey.foodsurvey.data.ResultCallback;
import com.foodsurvey.foodsurvey.data.UserController;
import com.foodsurvey.foodsurvey.data.UserHelper;
import com.foodsurvey.foodsurvey.ui.widget.PaperButton;
import com.foodsurvey.foodsurvey.utility.DialogHelper;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity extends ActionBarActivity {
    @InjectView(R.id.debug2_button)
    PaperButton mDebug2Button;
    @InjectView(R.id.debug_button)
    PaperButton mDebugButton;
    @InjectView(R.id.register_button)
    PaperButton mRegisterButton;
    @InjectView(R.id.login_button)
    PaperButton mLoginButton;
    @InjectView(R.id.username)
    EditText mUsernameText;
    @InjectView(R.id.password)
    EditText mPasswordText;


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

        mDebugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ProductListActivity.class);
                startActivity(i);
            }
        });
        mDebug2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, AdminProductListActivity.class);
                startActivity(i);
            }
        });

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
            Controllers.getUserController().login(this, username, password, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer errorCode) {
                    if (errorCode == 0) {
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        onLoginSuccess();
                    } else {
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


    private void onLoginSuccess() {

        if (mProgressDialog != null)
            mProgressDialog.dismiss();

        Intent intent;
        String companyId = UserHelper.getCurrentUser(this).getCompanyId();
        System.out.println("Company ID: " + companyId == null);
        if (companyId != null && companyId != "") {
            intent = new Intent(LoginActivity.this, AdminProductListActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, ProductListActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
