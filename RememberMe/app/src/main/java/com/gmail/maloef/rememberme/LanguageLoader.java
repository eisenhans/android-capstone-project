package com.gmail.maloef.rememberme;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.widget.Toast;

import com.gmail.maloef.rememberme.domain.Language;
import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.service.LanguageUpdater;
import com.gmail.maloef.rememberme.translate.google.LanguageProvider;

import java.io.IOException;

public class LanguageLoader extends AsyncTaskLoader<Language[]> {

    private Activity activity;
    private LanguageRepository languageRepository;
    private LanguageProvider languageProvider;

    public LanguageLoader(Activity activity, LanguageRepository languageRepository, LanguageProvider languageProvider) {
        super(activity);
        this.activity = activity;
        this.languageRepository = languageRepository;
        this.languageProvider = languageProvider;
    }

    @Override
    public Language[] loadInBackground() {
        String appLanguage = "en";
        LanguageUpdater updater = new LanguageUpdater(languageRepository, languageProvider);
        try {
            updater.updateLanguagesIfNeeded(appLanguage);
        } catch (IOException e) {
            showNoInternetConnectionToast();
        }
        return languageRepository.getLanguages(appLanguage);
    }

    private void showNoInternetConnectionToast() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStartLoading() {
        // otherwise loadInBackground() is never called - not clear why this is necessary
        forceLoad();
    }
}
