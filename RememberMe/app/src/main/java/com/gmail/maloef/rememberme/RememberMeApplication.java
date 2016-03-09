package com.gmail.maloef.rememberme;

import android.app.Application;
import android.content.Intent;

import com.gmail.maloef.rememberme.di.DaggerRememberMeInjector;
import com.gmail.maloef.rememberme.di.RememberMeInjector;
import com.gmail.maloef.rememberme.di.RememberMeModule;
import com.gmail.maloef.rememberme.service.LanguageUpdateService;

public class RememberMeApplication extends Application {

    private static RememberMeInjector injector;

    @Override
    public void onCreate() {
        super.onCreate();

        injector = DaggerRememberMeInjector.builder()
                .rememberMeModule(new RememberMeModule(getApplicationContext()))
                .build();

        startLanguageUpdateService();
    }

    private void startLanguageUpdateService() {
        // not sure if this is the best way to start a service when the app starts
        Intent startServiceIntent = new Intent(getApplicationContext(), LanguageUpdateService.class);
        startService(startServiceIntent);
    }

    public static RememberMeInjector injector() {
        return injector;
    }
}
