package com.gmail.maloef.rememberme.service;

import android.util.Log;
import android.util.Pair;

import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.translate.google.LanguageProvider;

public class LanguageUpdater {

    private LanguageRepository languageRepository;
    private LanguageProvider languageProvider;

    public LanguageUpdater(LanguageRepository languageRepository, LanguageProvider languageProvider) {
        this.languageRepository = languageRepository;
        this.languageProvider = languageProvider;
    }

    public void update(String language) {
        if (isTimeForUpdate(language)) {
            updateLanguages(language);
        }
    }

    public boolean isTimeForUpdate(String nameCode) {
        // ToDo 05.04.16: criteria?
        int languages = languageRepository.countLanguages(nameCode);
        logInfo("found " + languages + " languages for nameCode " + nameCode + " in database");

        return languages < 10;
    }

    public void updateLanguages(String nameCode) {
        Pair<String, String>[] languages = languageProvider.getLanguages(nameCode);
        if (languages.length == 0) {
            return;
        }
        int deleted = languageRepository.deleteLanguages(nameCode);
        int inserted = languageRepository.insertLanguages(languages, nameCode);

        logInfo("updated language names for " + nameCode + ": deleted " + deleted + ", inserted " + inserted);
    }



    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
