package com.foodsurvey.foodsurvey.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.foodsurvey.foodsurvey.data.User;
import com.google.gson.Gson;

/**
 * A static helper class for persistence of user data
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
        editor.remove("user");
        editor.commit();
    }
}
