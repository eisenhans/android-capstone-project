package com.gmail.maloef.rememberme.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.gmail.maloef.rememberme.persistence.LanguageColumns;
import com.gmail.maloef.rememberme.persistence.RememberMeProvider;
import com.gmail.maloef.rememberme.translate.google.LanguageProvider;

import javax.inject.Inject;

// ToDo 08.03.16: SyncAdapter
public class LanguageUpdateService {

    private Context context;
    private ContentResolver contentResolver;
    private LanguageProvider languageProvider;

    @Inject
    public LanguageUpdateService(Context context, LanguageProvider languageProvider) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
        this.languageProvider = languageProvider;
    }

    public boolean isTimeForUpdate() {
        return true;
    }

    public void updateLanguages() {
        updateLanguages("en");
    }

    private void updateLanguages(String nameCode) {
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
        return contentResolver.bulkInsert(RememberMeProvider.Language.LANGUAGES, values);
    }

    private int deleteLanguages(String nameCode) {
        return contentResolver.delete(RememberMeProvider.Language.LANGUAGES, LanguageColumns.NAME_CODE + " = ?", new String[] {nameCode});
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
