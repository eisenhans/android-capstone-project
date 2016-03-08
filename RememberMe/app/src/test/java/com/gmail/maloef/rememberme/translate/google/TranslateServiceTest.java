package com.gmail.maloef.rememberme.translate.google;

import android.util.Pair;

import com.gmail.maloef.rememberme.AbstractRobolectricTest;
import com.gmail.maloef.rememberme.BuildConfig;
import com.gmail.maloef.rememberme.domain.Language;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TranslateServiceTest extends AbstractRobolectricTest {

    GoogleTranslateService service = new GoogleTranslateService(BuildConfig.SERVER_TRANSLATE_API_KEY);

    @Test
    public void testTranslate() {
        Translation translation = service.translate("porcupine", "en", "de");
        assertEquals("Stachelschwein", translation.translatedText);
        assertNull(translation.detectedSourceLanguage);
    }

    @Test
    public void testTranslateDetectLanguage() {
        Translation translation = service.translate("porcupine", "de");
        assertEquals("Stachelschwein", translation.translatedText);
        assertEquals("en", translation.detectedSourceLanguage);
    }

    @Test
    public void testDetectLanguage() {
        String language = service.detectLanguage("porcupine");
        assertEquals("en", language);
    }

    @Test
    public void testGetLanguages() {
        Language[] languages = service.getLanguages("en");

        assertEquals(104, languages.length);

        assertEquals("af", languages[0].code);
        assertEquals("Afrikaans", languages[0].name);
        assertEquals("en", languages[0].nameCode);

        assertEquals("zu", languages[103].code);
        assertEquals("Zulu", languages[103].name);
        assertEquals("en", languages[103].nameCode);
    }

    @Test
    public void testGetCodeLanguagePairs() {
        Pair<String, String>[] pairs = service.getCodeLanguagePairs("en");

        assertEquals(104, pairs.length);

        assertEquals("af", pairs[0].first);
        assertEquals("Afrikaans", pairs[0].second);

        assertEquals("zu", pairs[103].first);
        assertEquals("Zulu", pairs[103].second);
    }
}
