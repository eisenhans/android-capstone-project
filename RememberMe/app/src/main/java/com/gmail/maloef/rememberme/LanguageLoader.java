package com.gmail.maloef.rememberme;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.gmail.maloef.rememberme.domain.Language;
import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.service.LanguageUpdater;
import com.gmail.maloef.rememberme.translate.google.LanguageProvider;

public class LanguageLoader extends AsyncTaskLoader<Language[]> {

    private LanguageRepository languageRepository;
    private LanguageProvider languageProvider;

    public LanguageLoader(Context context, LanguageRepository languageRepository, LanguageProvider languageProvider) {
        super(context);
        this.languageRepository = languageRepository;
        this.languageProvider = languageProvider;
    }

    @Override
    public Language[] loadInBackground() {
        LanguageUpdater updater = new LanguageUpdater(languageRepository, languageProvider);

        updater.update();

        return languageRepository.getLanguages("en");
    }

    @Override
    protected void onStartLoading() {
        // otherwise loadInBackground() is never called - not clear why this is necessary
        forceLoad();
    }
}
