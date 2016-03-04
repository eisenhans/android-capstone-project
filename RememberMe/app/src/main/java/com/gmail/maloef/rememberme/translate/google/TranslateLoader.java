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

    public TranslateLoader(Context context, GoogleTranslateService translateService, String foreignWord, String foreignLanguage, String nativeLanguage) {
        super(context);

        this.foreignWord = foreignWord;
        this.foreignLanguage = foreignLanguage;
        this.nativeLanguage = nativeLanguage;

        this.translateService = translateService;
    }

    @Override
    public String loadInBackground() {
        translation = translateService.translate(foreignWord, foreignLanguage, nativeLanguage);

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
