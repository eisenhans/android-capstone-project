package com.gmail.maloef.rememberme.activity.word.load;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.widget.Toast;

import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.service.LanguageUpdater;
import com.gmail.maloef.rememberme.translate.google.GoogleTranslateService;
import com.gmail.maloef.rememberme.translate.google.Translation;

import java.io.IOException;

public class TranslationResultLoader  extends AsyncTaskLoader<TranslationResult> {

    private Activity activity;
    private LanguageRepository languageRepository;
    private GoogleTranslateService translateService;
    private String foreignWord;
    private String foreignLanguage;
    private String nativeLanguage;

    public TranslationResultLoader(Activity activity, LanguageRepository languageRepository, GoogleTranslateService translateService,
                                   String foreignWord, String foreignLanguage, String nativeLanguage) {
        super(activity);
        this.activity = activity;
        this.languageRepository = languageRepository;
        this.translateService = translateService;
        this.foreignWord = foreignWord;
        this.foreignLanguage = foreignLanguage;
        this.nativeLanguage = nativeLanguage;
    }

    @Override
    public TranslationResult loadInBackground() {
        String appLanguage = "en";

        LanguageUpdater updater = new LanguageUpdater(languageRepository, translateService);
        try {
            updater.updateLanguagesIfNeeded(appLanguage);
        } catch (IOException e) {
            showNoInternetConnectionToast();
        }
        TranslationResult result = new TranslationResult();
        result.languages = languageRepository.getLanguages(appLanguage);

        try {
            result.translation = translateService.translate(foreignWord, foreignLanguage, nativeLanguage);
        } catch (IOException e) {
            result.translation = Translation.create(null, null);
            showNoInternetConnectionToast();
        }
        return result;
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
