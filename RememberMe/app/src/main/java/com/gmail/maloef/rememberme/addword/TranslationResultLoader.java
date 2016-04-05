package com.gmail.maloef.rememberme.addword;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.service.LanguageUpdater;
import com.gmail.maloef.rememberme.translate.google.GoogleTranslateService;

public class TranslationResultLoader  extends AsyncTaskLoader<TranslationResult> {

    private final LanguageRepository languageRepository;
    private GoogleTranslateService translateService;
    private String foreignWord;
    private String foreignLanguage;
    private String nativeLanguage;

    public TranslationResultLoader(Context context, LanguageRepository languageRepository, GoogleTranslateService translateService,
                                   String foreignWord, String foreignLanguage, String nativeLanguage) {
        super(context);
        this.languageRepository = languageRepository;
        this.translateService = translateService;
        this.foreignWord = foreignWord;
        this.foreignLanguage = foreignLanguage;
        this.nativeLanguage = nativeLanguage;
    }

    @Override
    public TranslationResult loadInBackground() {
        TranslationResult result = new TranslationResult();

        LanguageUpdater updater = new LanguageUpdater(languageRepository, translateService);
        updater.update();

        result.languages = languageRepository.getLanguages("en");
        result.translation = translateService.translate(foreignWord, foreignLanguage, nativeLanguage);

        return result;
    }

    @Override
    protected void onStartLoading() {
        // otherwise loadInBackground() is never called - not clear why this is necessary
        forceLoad();
    }
}
