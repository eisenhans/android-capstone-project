package com.gmail.maloef.rememberme.service;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Pair;

import com.gmail.maloef.rememberme.persistence.LanguageColumns;
import com.gmail.maloef.rememberme.persistence.LanguageCursor;
import com.gmail.maloef.rememberme.persistence.RememberMeProvider;
import com.gmail.maloef.rememberme.translate.google.LanguageProvider;

import javax.inject.Inject;

// ToDo 07.03.16: move to persistence package (and other classes from this package too)?
public class LanguageService {

    private Context context;
    private ContentResolver contentResolver;

    LanguageProvider languageProvider;
//    Language[] languages;

    @Inject
    public LanguageService(Context context, LanguageProvider languageProvider) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
        this.languageProvider = languageProvider;
    }

    // ToDo 07.03.16: use a ScnyAdapter to keep contentProvider in sync with languages provided by Google? Or another mechanism?
    public Pair<String, String>[] getCodeLanguagePairs(String nameCode) {
        Pair<String, String>[] pairs = getCodeLangugePairsFromContentResolver(nameCode);
        if (pairs.length == 0) {
            pairs = getLanguagesFromExternal(nameCode);
            insertLanguages(pairs);
        }
        return pairs;
    }

    private Pair<String, String>[] getCodeLangugePairsFromContentResolver(String nameCode) {
        LanguageCursor languageCursor = new LanguageCursor(contentResolver.query(
                RememberMeProvider.Language.LANGUAGES,
                null,
                LanguageColumns.NAME_CODE + " = ?",
                new String[]{nameCode},
                null));

        return new Pair[0];
    }

    private void insertLanguages(Pair<String, String>[] codeLanguagePairs) {

    }

    private Pair<String, String>[] getLanguagesFromExternal(String nameCode) {
        return languageProvider.getCodeLanguagePairs(nameCode);
    }

//    public String[] getLanguageNames(String nameCode) {
//        String[] names = new String[languages.length];
//        for (int i = 0; i < names.length; i++) {
//            names[i] = languages[i].name;
//        }
//        return names;
//    }
//
//    public String[] getIsoCodes(String nameCode) {
//        String[] isoCodes = new String[languages.length];
//        for (int i = 0; i < isoCodes.length; i++) {
//            isoCodes[i] = languages[i].code;
//        }
//        return isoCodes;
//    }


}
