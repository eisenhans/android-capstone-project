package com.gmail.maloef.rememberme.di;

import android.content.Context;

import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.translate.google.GoogleTranslateService;
import com.gmail.maloef.rememberme.translate.google.LanguageProvider;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RememberMeModule {

    Context context;

    public RememberMeModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Provides @Singleton
    public LanguageProvider provideLanguageProvider() {
        return new GoogleTranslateService();
    }

    @Provides @Singleton
    public Tracker provideGoogleAnalyticsTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);

        Tracker tracker = analytics.newTracker(R.xml.global_tracker);
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(true);

        return tracker;
    }
}
