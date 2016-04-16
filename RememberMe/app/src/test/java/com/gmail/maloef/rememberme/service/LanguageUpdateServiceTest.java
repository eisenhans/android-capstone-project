package com.gmail.maloef.rememberme.service;

import android.app.Application;
import android.util.Pair;

import com.gmail.maloef.rememberme.domain.Language;
import com.gmail.maloef.rememberme.persistence.AbstractPersistenceTest;
import com.gmail.maloef.rememberme.persistence.LanguageRepository;
import com.gmail.maloef.rememberme.translate.google.LanguageProvider;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class LanguageUpdateServiceTest extends AbstractPersistenceTest {

    static final String EN = "en";

    Application application;
    LanguageRepository languageRepository;

    LanguageUpdater languageUpdater;

    @Before
    public void before() throws Exception {
        super.before();
        application = RuntimeEnvironment.application;
        languageRepository = new LanguageRepository(application);
    }

    @Test
    public void updateLanguages() throws IOException {
        assertEquals(0, countLanguagesInDatabase());

        languageUpdater = new LanguageUpdater(languageRepository, createLanguageProvider(0));
        languageUpdater.updateLanguages(EN);
        assertEquals(0, countLanguagesInDatabase());

        languageUpdater = new LanguageUpdater(languageRepository, createLanguageProvider(2));
        languageUpdater.updateLanguages(EN);
        assertEquals(2, countLanguagesInDatabase());

        languageUpdater = new LanguageUpdater(languageRepository, createLanguageProvider(1));
        languageUpdater.updateLanguages(EN);
        assertEquals(1, countLanguagesInDatabase());
    }

    private LanguageProvider createLanguageProvider(final int languages) {
        final Pair<String, String>[] pairs = new Pair[2];
        pairs[0] = Pair.create("en", "English");
        pairs[1] = Pair.create("de", "German");
        return new LanguageProvider() {
            @Override
            public Pair<String, String>[] getLanguages(String nameCode) {
                if (!nameCode.equals(EN)) {
                    return new Pair[0];
                }
                return Arrays.copyOfRange(pairs, 0, languages);
            }
        };
    }

    private int countLanguagesInDatabase() {
        Language[] languages = languageRepository.getLanguages(EN);
        return languages.length;
    }
}
