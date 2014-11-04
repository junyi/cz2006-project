package com.foodsurvey.foodsurvey;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.foodsurvey.foodsurvey.ui.MainActivity;
import com.foodsurvey.foodsurvey.utility.UserHelper;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;

public class FoodPushBroadcastReceiver extends ParsePushBroadcastReceiver {
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        Log.d("Push", "Received");

        if (ParseUser.getCurrentUser() != null && TextUtils.isEmpty(UserHelper.getCurrentUser(context).getCompanyId())) {
            super.onPushReceive(context, intent);
        }
    }

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.d("Push", "Clicked");
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
