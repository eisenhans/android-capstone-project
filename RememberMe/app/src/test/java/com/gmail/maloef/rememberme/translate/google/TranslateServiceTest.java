package com.gmail.maloef.rememberme.translate.google;

import com.gmail.maloef.rememberme.AbstractRobolectricTest;
import com.gmail.maloef.rememberme.BuildConfig;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TranslateServiceTest extends AbstractRobolectricTest {

    GoogleTranslateService service = new GoogleTranslateService(BuildConfig.SERVER_TRANSLATE_API_KEY);

    @Test
    public void testTranslate() {
        String hallo = service.translate("hello", "en", "de");
        assertEquals("hallo", hallo);
    }
}
