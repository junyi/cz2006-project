package com.foodsurvey.foodsurvey.data;

import android.content.Context;

/**
 * Interface for the user manager
 * Provides methods to sign up (for surveyee) and login (both surveyee and administrator)
 */
public interface UserManagerInterface {

    /**
     * Asynchronous method for surveyees to sign up
     * Returns user ID of the new user if successful and -1 if otherwise
     *
     * @param firstName First name of the user
     * @param lastName  Last name of the user
     * @param username  Username of the user
     * @param ageGroup  Age group of the user
     * @param password  Password of the user
     * @param email     E-mail of the user
     * @param callback  Callback to receive the result
     */
    public void signUp(String firstName, String lastName, String username, String ageGroup, String password, String email, final ResultCallback<Integer> callback);

    /**
     * Asynchronous method to login a user (for both surveyee and administrator)
     * Returns the user ID of the user if successful and -1 if otherwise
     *
     * @param context  Context of the Android system (usually using {@link android.app.Activity} or {@link android.app.Application})
     * @param username Username of the user
     * @param password Password of the user
     * @param callback Callback to receive the result
     */
    public void login(Context context, String username, String password, final ResultCallback<Integer> callback);

    /**
     * Asynchronous method to update a user's profile (for both surveyy and administrator)
     * Return true if successful, false if otherwise
     *
     * @param userId    ID of the user
     * @param firstName First name of the user
     * @param lastName  Last name of the user
     * @param email     E-mail of the user
     * @param ageGroup  Age group of the user (only for surveyee)
     * @param callback  Callback to receive the result
     */
    public void updateProfile(String userId, String firstName, String lastName, String email, String ageGroup, final ResultCallback<Boolean> callback);
}
