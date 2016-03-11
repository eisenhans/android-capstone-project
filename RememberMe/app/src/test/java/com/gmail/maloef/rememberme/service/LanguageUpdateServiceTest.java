package com.gmail.maloef.rememberme.service;

import android.app.Application;
import android.util.Pair;

import com.gmail.maloef.rememberme.persistence.AbstractPersistenceTest;
import com.gmail.maloef.rememberme.translate.google.LanguageProvider;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class LanguageUpdateServiceTest extends AbstractPersistenceTest {

    static final String EN = "en";

    Application application;
    LanguageService languageService;

    LanguageUpdateService languageUpdateService;

    @Before
    public void before() throws Exception {
        super.before();
        application = RuntimeEnvironment.application;
        languageService = new LanguageService(application);
    }

    @Test
    public void updateLanguages() {
        assertEquals(0, countLanguagesInDatabase());

        languageUpdateService = new LanguageUpdateService(createLanguageProvider(0));
        languageUpdateService.updateLanguages(EN);
        assertEquals(0, countLanguagesInDatabase());

        languageUpdateService = new LanguageUpdateService(createLanguageProvider(2));
        languageUpdateService.updateLanguages(EN);
        assertEquals(2, countLanguagesInDatabase());

        languageUpdateService = new LanguageUpdateService(createLanguageProvider(1));
        languageUpdateService.updateLanguages(EN);
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
        Pair<String, String>[] languages = languageService.getLanguages(EN);
        return languages.length;
    }
}
