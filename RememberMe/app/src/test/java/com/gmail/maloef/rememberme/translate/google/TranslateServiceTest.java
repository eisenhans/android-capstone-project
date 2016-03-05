package com.gmail.maloef.rememberme.translate.google;

import com.gmail.maloef.rememberme.AbstractRobolectricTest;
import com.gmail.maloef.rememberme.BuildConfig;

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
}
