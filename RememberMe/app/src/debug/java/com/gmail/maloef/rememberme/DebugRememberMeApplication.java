package com.gmail.maloef.rememberme;

import android.util.Log;

import com.facebook.stetho.Stetho;

public class DebugRememberMeApplication extends RememberMeApplication {

    public void onCreate() {
        super.onCreate();

        if (isRobelectricTest()) {
            Log.i(DebugRememberMeApplication.class.getSimpleName(), "Robolectric found on classpath - Stetho will not be initialized");
        } else {
            Stetho.initializeWithDefaults(this);
            Log.i(DebugRememberMeApplication.class.getSimpleName(), "Stetho initialized");
        }
    }

    private boolean isRobelectricTest() {
        try {
            Class.forName("org.robolectric.Robolectric");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
