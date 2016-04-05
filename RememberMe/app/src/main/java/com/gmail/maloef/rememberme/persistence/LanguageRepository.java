package com.gmail.maloef.rememberme.persistence;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Pair;

import com.gmail.maloef.rememberme.domain.Language;

import javax.inject.Inject;

public class LanguageRepository {

    private Context context;
    private ContentResolver contentResolver;

    @Inject
    public LanguageRepository(Context context) {
        this.context = context;
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

//    public Pair<String, String>[] getLanguages(String nameCode) {
//        LanguageCursor languageCursor = new LanguageCursor(contentResolver.query(
//                RememberMeProvider.Language.LANGUAGES,
//                null,
//                LanguageColumns.NAME_CODE + " = ?",
//                new String[]{nameCode},
//                null));
//
//        Pair[] pairs = new Pair[languageCursor.getCount()];
//        int i = 0;
//        for (Language language : languageCursor) {
//            pairs[i++] = Pair.create(language.code, language.name);
//        }
//        languageCursor.close();
//
//        // ToDo 08.03.16: remove
//        if (pairs.length == 0 && !isRobelectricTest()) {
//            pairs = createDummyLanguages();
//        }
//
//        return pairs;
//    }

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

    private Pair[] createDummyLanguages() {
        final Pair<String, String>[] pairs = new Pair[3];
        pairs[0] = Pair.create("en", "English");
        pairs[1] = Pair.create("de", "German");
        pairs[2] = Pair.create("es", "Spanish");

        return pairs;
    }

    private boolean isRobelectricTest() {
        try {
            Class.forName("org.robolectric.Robolectric");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
