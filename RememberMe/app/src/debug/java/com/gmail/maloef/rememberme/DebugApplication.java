package com.gmail.maloef.rememberme;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;

public class DebugApplication extends Application {

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        Log.i(DebugApplication.class.getSimpleName(), "Stetho initialized");
    }
}
