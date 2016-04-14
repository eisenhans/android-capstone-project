package com.gmail.maloef.rememberme.persistence;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Pair;

import com.gmail.maloef.rememberme.domain.Language;

import javax.inject.Inject;

public class LanguageRepository {

    private ContentResolver contentResolver;

    @Inject
    public LanguageRepository(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    public int countLanguages(String nameCode) {
        Cursor cursor = contentResolver.query(
                RememberMeProvider.Language.LANGUAGES,
                null,
                LanguageColumns.NAME_CODE + " = ?",
                new String[]{nameCode},
                null);

        int languages = cursor.getCount();
        cursor.close();
        return languages;
    }

    public Language[] getLanguages(String nameCode) {
        LanguageCursor languageCursor = new LanguageCursor(contentResolver.query(
                RememberMeProvider.Language.LANGUAGES,
                null,
                LanguageColumns.NAME_CODE + " = ?",
                new String[]{nameCode},
                null));

        Language[] languages = new Language[languageCursor.getCount()];
        int i = 0;
        for (Language language : languageCursor) {
            languages[i++] = language;
        }
        languageCursor.close();

        return languages;
    }

    public int insertLanguages(Pair<String, String>[] languages, String nameCode) {
        ContentValues[] values = new ContentValues[languages.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = new ContentValues();
            values[i].put("code", languages[i].first);
            values[i].put("name", languages[i].second);
            values[i].put("nameCode", nameCode);
        }
        return contentResolver.bulkInsert(RememberMeProvider.Language.LANGUAGES, values);
    }

    public int deleteLanguages(String nameCode) {
        return contentResolver.delete(
                RememberMeProvider.Language.LANGUAGES,
                LanguageColumns.NAME_CODE + " = ?",
                new String[]{nameCode});
    }
}
