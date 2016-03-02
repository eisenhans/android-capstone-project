package com.gmail.maloef.rememberme;

import android.app.Application;

import com.gmail.maloef.rememberme.di.DaggerRememberMeInjector;
import com.gmail.maloef.rememberme.di.RememberMeInjector;
import com.gmail.maloef.rememberme.di.RememberMeModule;

public class RememberMeApplication extends Application {

    private static RememberMeInjector injector;

    @Override
    public void onCreate() {
        super.onCreate();

        injector = DaggerRememberMeInjector.builder()
                .rememberMeModule(new RememberMeModule(getApplicationContext()))
                .build();
    }

    public static RememberMeInjector injector() {
        return injector;
    }
}
