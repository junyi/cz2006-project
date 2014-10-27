package com.foodsurvey.foodsurvey.data;

import android.content.Context;
import android.os.AsyncTask;

import com.foodsurvey.foodsurvey.utility.UserHelper;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Implementation of the user manager using Parse
 * Allows for login and sign up of users
 */
public class UserManager implements UserManagerInterface {

    public static final String COMPANY_NAME = "name";

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

    public void updateProfile(Context context, String userId, String firstName, String lastName, String email, String ageGroup, final ResultCallback<Boolean> callback) {
        new UpdateProfileTask(context, userId, firstName, lastName, email, ageGroup) {
            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (callback != null) {
                    callback.onResult(result);
                }
            }
        }.execute();
    }

//    public void getUserbyId(Context context, String userId, final ResultCallback<Boolean> callback) {
//        new FetchUserByIdTask(context, userId) {
//            @Override
//            protected void onPostExecute(Boolean result) {
//                super.onPostExecute(result);
//                if (callback != null) {
//                    callback.onResult(result);
//                }
//            }
//        }.execute();
//    }

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
                    String email = user.getString(DbConstants.USER_EMAIL);
                    String firstName = user.getString(DbConstants.USER_FIRST_NAME);
                    String lastName = user.getString(DbConstants.USER_LAST_NAME);
                    String ageGroup = user.getString(DbConstants.USER_AGE_GROUP);
                    ParseObject company = user.getParseObject(DbConstants.USER_COMPANY_ID);
                    if (company != null)
                        company.fetchIfNeeded();
                    String companyId = company == null ? "" : company.getObjectId();
                    String companyName = company == null ? "" : company.getString(COMPANY_NAME);

                    User currentUser = new User(user.getObjectId(), mUsername, firstName, lastName, ageGroup, email, companyId, companyName);
                    UserHelper.storeUser(mContext, currentUser);
                    System.out.printf("User: %s, %s, %s, %s, %s, %s, %s, %s\n", user.getObjectId(), mUsername, firstName, lastName, ageGroup, email, companyId, companyName);
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

                user.put(DbConstants.USER_FIRST_NAME, mFirstname);
                user.put(DbConstants.USER_LAST_NAME, mLastname);
                user.put(DbConstants.USER_AGE_GROUP, mAgeGroup);

                user.signUp();
            } catch (ParseException e) {
                errorCode = e.getCode();
                e.printStackTrace();
                return errorCode;
            }

            return -1;
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class FetchUserByIdTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserId;
        private final Context mContext;

        FetchUserByIdTask(Context context, String userId) {
            mContext = context;
            mUserId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ParseUser user = ParseUser.getQuery().get(mUserId);

                String username = user.getUsername();
                String email = user.getString(DbConstants.USER_EMAIL);
                String firstName = user.getString(DbConstants.USER_FIRST_NAME);
                String lastName = user.getString(DbConstants.USER_LAST_NAME);
                String ageGroup = user.getString(DbConstants.USER_AGE_GROUP);
                ParseObject company = user.getParseObject(DbConstants.USER_COMPANY_ID);
                if (company != null)
                    company.fetchIfNeeded();
                String companyId = company == null ? "" : company.getObjectId();
                String companyName = company == null ? "" : company.getString(COMPANY_NAME);

                User currentUser = new User(user.getObjectId(), username, firstName, lastName, ageGroup, email, companyId, companyName);
                UserHelper.storeUser(mContext, currentUser);
                System.out.printf("User: %s, %s, %s, %s, %s, %s, %s, %s\n", user.getObjectId(), username, firstName, lastName, ageGroup, email, companyId, companyName);
                return true;

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private class UpdateProfileTask extends AsyncTask<Void, Void, Boolean> {
        private final String mFirstname;
        private final String mLastname;
        private final String mUserId;
        private final String mAgeGroup;
        private final String mEmail;
        private final Context mContext;

        UpdateProfileTask(Context context, String userId, String firstName, String lastName, String email, String ageGroup) {
            mContext = context;
            mUserId = userId;
            mFirstname = firstName;
            mLastname = lastName;
            mAgeGroup = ageGroup;
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                ParseUser user = ParseUser.getQuery().get(mUserId);
                user.put(DbConstants.USER_EMAIL, mEmail);
                user.put(DbConstants.USER_FIRST_NAME, mFirstname);
                user.put(DbConstants.USER_LAST_NAME, mLastname);
                if (mAgeGroup != null)
                    user.put(DbConstants.USER_AGE_GROUP, mAgeGroup);
                user.save();

                String username = user.getUsername();
                String email = user.getString(DbConstants.USER_EMAIL);
                String firstName = user.getString(DbConstants.USER_FIRST_NAME);
                String lastName = user.getString(DbConstants.USER_LAST_NAME);
                String ageGroup = user.getString(DbConstants.USER_AGE_GROUP);
                ParseObject company = user.getParseObject(DbConstants.USER_COMPANY_ID);
                if (company != null)
                    company.fetchIfNeeded();
                String companyId = company == null ? "" : company.getObjectId();
                String companyName = company == null ? "" : company.getString(COMPANY_NAME);

                User currentUser = new User(user.getObjectId(), username, firstName, lastName, ageGroup, email, companyId, companyName);
                UserHelper.storeUser(mContext, currentUser);
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}
