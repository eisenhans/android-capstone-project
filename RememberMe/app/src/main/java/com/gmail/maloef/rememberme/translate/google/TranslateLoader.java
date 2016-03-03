package com.gmail.maloef.rememberme.translate.google;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class TranslateLoader extends AsyncTaskLoader<String> {

    String foreignWord;
    String foreignLanguage;
    String nativeLanguage;

    String translation;

    GoogleTranslateService translateService;

    public TranslateLoader(Context context, String foreignWord, String foreignLanguage, String nativeLanguage, GoogleTranslateService translateService) {
        super(context);

        this.foreignWord = foreignWord;
        this.foreignLanguage = foreignLanguage;
        this.nativeLanguage = nativeLanguage;

        // TODO: 03.03.2016 inject
        this.translateService = translateService;
    }

    @Override
    public String loadInBackground() {
        translation = translateService.translate(foreignWord, foreignLanguage, nativeLanguage);

        logInfo("loaded translation for " + foreignWord + ": " + translation);

        return translation;
    }

    @Override
    protected void onStartLoading() {
        if (translation == null) {
            // otherwise loadInBackground() is never called - not clear why this is necessary
            forceLoad();
        }
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
