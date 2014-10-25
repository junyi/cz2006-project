package com.foodsurvey.foodsurvey;

import android.content.Context;
import android.os.AsyncTask;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created on 25/10/14.
 */
public class UserController {
    public static final String LAST_NAME = "last_name";
    public static final String FIRST_NAME = "first_name";

    private static UserController instance = null;

    public static UserController getInstance() {
        if (instance == null)
            instance = new UserController();
        return instance;
    }

    public void signUp(String firstName, String lastName, String username, String ageGroup, String password, String email, final ResultCallback<Integer> callback) {
        new UserSignUpTask(firstName, lastName, username, ageGroup, password, email) {
            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                if (callback != null) {
                    callback.onResult(result);
                }
            }
        }.execute();
    }

    public void login(Context context, String username, String password, final ResultCallback<Integer> callback) {
        new UserLoginTask(context, username, password) {
            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                if (callback != null) {
                    callback.onResult(result);
                }
            }
        }.execute();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mUsername;
        private final String mPassword;
        private final Context mContext;

        UserLoginTask(Context context, String username, String password) {
            mContext = context;
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                ParseUser.logOut();
                UserHelper.removeCurrentUser(mContext);
                ParseUser user = ParseUser.logIn(mUsername, mPassword);
                if (user != null) {
                    String email = user.getString("email");
                    String firstName = user.getString("first_name");
                    String lastName = user.getString("last_name");
                    String ageGroup = user.getString("ageGroup");
                    ParseObject company = user.getParseObject("companyId");
                    String companyId = company == null ? "" : company.getObjectId();

                    User currentUser = new User(user.getObjectId(), mUsername, firstName, lastName, ageGroup, email, companyId);
                    UserHelper.storeUser(mContext, currentUser);
                    System.out.printf("User: %s, %s, %s, %s, %s, %s, %s\n", user.getObjectId(), mUsername, firstName, lastName, ageGroup, email, companyId);
                    return 0;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return e.getCode();
            }
            return -1;
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserSignUpTask extends AsyncTask<Void, Void, Integer> {
        private final String mFirstname;
        private final String mLastname;
        private final String mUsername;
        private final String mAgeGroup;
        private final String mEmail;
        private final String mPassword;
        private int errorCode;

        UserSignUpTask(String firstName, String lastName, String username, String ageGroup, String password, String email) {
            mFirstname = firstName;
            mLastname = lastName;
            mUsername = username;
            mAgeGroup = ageGroup;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                ParseUser user = new ParseUser();
                user.setUsername(mUsername);
                user.setPassword(mPassword);
                user.setEmail(mEmail);

                user.put(UserConstant.FIRST_NAME, mFirstname);
                user.put(UserConstant.LAST_NAME, mLastname);

                user.signUp();
            } catch (ParseException e) {
                errorCode = e.getCode();
                e.printStackTrace();
                return errorCode;
            }

            return 0;
        }
    }

}
