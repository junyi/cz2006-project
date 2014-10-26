package com.foodsurvey.foodsurvey;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created on 19/10/14.
 */

public class FoodApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Set to null to default to device font-family
        CalligraphyConfig.initDefault(null, R.attr.fontPath);

        // Add your initialization code here
        Parse.initialize(this, "BRCNzsuQ6DUIpO7tBRWn1nAPwk8qrDnlWSXURNsm", "59YutQJyji1qrOjdLlYZlKnzKLUXRKwU0n9u7qis");


//        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }

}
