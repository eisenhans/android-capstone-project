package com.gmail.maloef.rememberme.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;

import com.gmail.maloef.rememberme.RememberMeApplication;
import com.gmail.maloef.rememberme.persistence.LanguageColumns;
import com.gmail.maloef.rememberme.persistence.RememberMeProvider;
import com.gmail.maloef.rememberme.translate.google.LanguageProvider;

import javax.inject.Inject;

public class LanguageUpdateService extends IntentService {

    public static final String LANGUAGES_UPDATED = "languages_updated";

    @Inject LanguageProvider languageProvider;

    /**
     * Used by android.
     */
    public LanguageUpdateService() {
        super(LanguageUpdateService.class.getSimpleName());
    }

    /**
     * Used for unit tests.
     */
    public LanguageUpdateService(LanguageProvider languageProvider) {
        super(LanguageUpdateService.class.getSimpleName());
        this.languageProvider = languageProvider;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RememberMeApplication.injector().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        logInfo("handling intent " + intent);
        String language = "en";

        if (isTimeForUpdate(language)) {
            updateLanguages(language);
        }
        Intent doneIntent = new Intent(LANGUAGES_UPDATED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(doneIntent);
    }

    public boolean isTimeForUpdate(String nameCode) {
        // ToDo 09.03.16: read last update date from preferences, do update every week/month
        int languages = countLanguages(nameCode);
        logInfo("found " + languages + " languages for nameCode " + nameCode + " in database");

        return languages < 10;
    }

    private int countLanguages(String nameCode) {
        Cursor cursor = getContentResolver().query(
                RememberMeProvider.Language.LANGUAGES,
                null,
                LanguageColumns.NAME_CODE + " = ?",
                new String[]{nameCode},
                null);

        int languages = cursor.getCount();
        cursor.close();
        return languages;
    }

    public void updateLanguages(String nameCode) {
        Pair<String, String>[] languages = languageProvider.getLanguages(nameCode);
        if (languages.length == 0) {
            return;
        }
        int deleted = deleteLanguages(nameCode);
        int inserted = insertLanguages(languages, nameCode);

        logInfo("updated language names for " + nameCode + ": deleted " + deleted + ", inserted " + inserted);
    }

    private int insertLanguages(Pair<String, String>[] languages, String nameCode) {
        ContentValues[] values = new ContentValues[languages.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = new ContentValues();
            values[i].put("code", languages[i].first);
            values[i].put("name", languages[i].second);
            values[i].put("nameCode", nameCode);
        }
        return getContentResolver().bulkInsert(RememberMeProvider.Language.LANGUAGES, values);
    }

    private int deleteLanguages(String nameCode) {
        return getContentResolver().delete(
                RememberMeProvider.Language.LANGUAGES,
                LanguageColumns.NAME_CODE + " = ?",
                new String[]{nameCode});
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
