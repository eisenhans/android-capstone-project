package com.gmail.maloef.rememberme.translate.google;

import android.util.Pair;

import com.gmail.maloef.rememberme.AbstractRobolectricTest;
import com.gmail.maloef.rememberme.BuildConfig;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TranslateServiceTest extends AbstractRobolectricTest {

    GoogleTranslateService service = new GoogleTranslateService(BuildConfig.SERVER_TRANSLATE_API_KEY);

    @Test
    public void translate() throws IOException {
        Translation translation = service.translate("porcupine", "en", "de");
        assertEquals("Stachelschwein", translation.translatedText);
        assertNull(translation.detectedSourceLanguage);
    }

    @Test
    public void translateDetectLanguage() throws IOException {
        Translation translation = service.translate("porcupine", "de");
        assertEquals("Stachelschwein", translation.translatedText);
        assertEquals("en", translation.detectedSourceLanguage);
    }

    @Test
    public void getLanguages() throws IOException {
        Pair<String, String>[] pairs = service.getLanguages("en");

        assertEquals(104, pairs.length);

        assertEquals("af", pairs[0].first);
        assertEquals("Afrikaans", pairs[0].second);

        assertEquals("zu", pairs[103].first);
        assertEquals("Zulu", pairs[103].second);
    }
}
