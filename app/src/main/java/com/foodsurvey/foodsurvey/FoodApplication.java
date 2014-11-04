package com.foodsurvey.foodsurvey;

import android.app.Application;

import com.foodsurvey.foodsurvey.ui.MainActivity;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.PushService;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Application class for the entire app
 *
 * @author Hee Jun Yi
 */
public class FoodApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Set to null to default to device font-family
        CalligraphyConfig.initDefault(null, R.attr.fontPath);

        // Add your initialization code here
        Parse.initialize(this, "BRCNzsuQ6DUIpO7tBRWn1nAPwk8qrDnlWSXURNsm", "59YutQJyji1qrOjdLlYZlKnzKLUXRKwU0n9u7qis");
        // Also in this method, specify a default Activity to handle push notifications
        PushService.setDefaultPushCallback(this, MainActivity.class);

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

//        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);

        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
