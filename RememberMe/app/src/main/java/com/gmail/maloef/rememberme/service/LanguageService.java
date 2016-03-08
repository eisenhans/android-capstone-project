package com.gmail.maloef.rememberme.service;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Pair;

import com.gmail.maloef.rememberme.domain.Language;
import com.gmail.maloef.rememberme.persistence.LanguageColumns;
import com.gmail.maloef.rememberme.persistence.LanguageCursor;
import com.gmail.maloef.rememberme.persistence.RememberMeProvider;

import javax.inject.Inject;

// ToDo 07.03.16: move to persistence package (and other classes from this package too)?
public class LanguageService {

    private Context context;
    private ContentResolver contentResolver;

    @Inject
    public LanguageService(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public Pair<String, String>[] getLanguages(String nameCode) {
        LanguageCursor languageCursor = new LanguageCursor(contentResolver.query(
                RememberMeProvider.Language.LANGUAGES,
                null,
                LanguageColumns.NAME_CODE + " = ?",
                new String[]{nameCode},
                null));

        Pair[] pairs = new Pair[languageCursor.getCount()];
        int i = 0;
        for (Language language : languageCursor) {
            pairs[i++] = Pair.create(language.code, language.name);
        }
        languageCursor.close();

        // ToDo 08.03.16: remove
        if (pairs.length == 0 && !isRobelectricTest()) {
            pairs = createDummyLanguages();
        }

        return pairs;
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
