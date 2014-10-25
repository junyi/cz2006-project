package com.foodsurvey.foodsurvey;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created on 24/10/14.
 */
public class UserHelper {
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String EMAIL = "password";
    public static final String LAST_NAME = "last_name";
    public static final String FIRST_NAME = "first_name";
    public static final String AGE_GROUP = "age_group";
    public static final String COMPANY_ID = "companyId";

    public static User getCurrentUser(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String id = settings.getString(ID, "");
        String email = settings.getString(EMAIL, "");
        String username = settings.getString(USERNAME, "");
        String lastName = settings.getString(LAST_NAME, "");
        String firstName = settings.getString(FIRST_NAME, "");
        String ageGroup = settings.getString(AGE_GROUP, "");
        String companyId = settings.getString(COMPANY_ID, "");

        System.out.printf("Fetch current user: %s, %s, %s, %s, %s, %s, %s\n", id, username, firstName, lastName, ageGroup, email, companyId);


        User user = new User(id, username, firstName, lastName, ageGroup, email, companyId);
        return user;
    }

    public static void storeUser(Context context, User user) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putString(ID, user.getId());
        editor.putString(EMAIL, user.getEmail());
        editor.putString(USERNAME, user.getUsername());
        editor.putString(LAST_NAME, user.getLastName());
        editor.putString(FIRST_NAME, user.getFirstName());
        editor.putString(AGE_GROUP, user.getAgeGroup());
        editor.putString(COMPANY_ID, user.getCompanyId());
        editor.commit();
    }

    public static void removeCurrentUser(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = settings.edit();
        editor.remove(ID);
        editor.remove(EMAIL);
        editor.remove(USERNAME);
        editor.remove(LAST_NAME);
        editor.remove(FIRST_NAME);
        editor.remove(AGE_GROUP);
        editor.remove(COMPANY_ID);
        editor.commit();
    }
}
