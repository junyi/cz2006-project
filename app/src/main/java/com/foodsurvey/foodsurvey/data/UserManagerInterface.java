package com.foodsurvey.foodsurvey.data;

import android.content.Context;

public interface UserManagerInterface {
    public void signUp(String firstName, String lastName, String username, String ageGroup, String password, String email, final ResultCallback<Integer> callback);

    public void login(Context context, String username, String password, final ResultCallback<Integer> callback);
}
