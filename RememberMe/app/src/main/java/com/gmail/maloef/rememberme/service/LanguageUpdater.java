package com.gmail.maloef.rememberme.service;

import android.util.Log;
import android.util.Pair;

import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.translate.google.LanguageProvider;

import java.io.IOException;

public class LanguageUpdater {

    private LanguageRepository languageRepository;
    private LanguageProvider languageProvider;

    public LanguageUpdater(LanguageRepository languageRepository, LanguageProvider languageProvider) {
        this.languageRepository = languageRepository;
        this.languageProvider = languageProvider;
    }

    public void updateLanguagesIfNeeded(String nameCode) throws IOException {
        if (isUpdateNeeded(nameCode)) {
            updateLanguages(nameCode);
        }
    }

    public boolean isUpdateNeeded(String nameCode) {
        int languagesInDatabase = languageRepository.countLanguages(nameCode);
        return languagesInDatabase < 50;
    }

    public void updateLanguages(String nameCode) throws IOException {
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
