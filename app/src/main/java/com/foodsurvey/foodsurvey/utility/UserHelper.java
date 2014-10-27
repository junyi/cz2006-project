package com.foodsurvey.foodsurvey.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.foodsurvey.foodsurvey.data.User;
import com.google.gson.Gson;

/**
 * Created on 24/10/14.
 */
public class UserHelper {

    public static User getCurrentUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences("user", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String userString = settings.getString("user", "");
        User user = gson.fromJson(userString, User.class);
        Log.d("User fetch", userString);

        return user;
    }

    public static void storeUser(Context context, User user) {
        SharedPreferences settings = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();

        Gson gson = new Gson();
        String userString = gson.toJson(user);
        Log.d("User store", userString);
        editor.putString("user", userString);
        editor.commit();
    }

    public static void removeCurrentUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
//        editor.remove(ID);
//        editor.remove(EMAIL);
//        editor.remove(USERNAME);
//        editor.remove(LAST_NAME);
//        editor.remove(FIRST_NAME);
//        editor.remove(AGE_GROUP);
//        editor.remove(COMPANY_ID);
//        editor.remove(COMPANY_NAME);
        editor.remove("user");
        editor.commit();
    }
}
