package com.gmail.maloef.rememberme.translate.google;

import java.util.List;
import java.util.Map;

public class TranslateTextResponse extends GoogleTranslateResponse<Map<String, List<Map<String, String>>>> {

    public String getTranslation() {
        List<Map<String, String>> translations = data.get("translations");
        Map<String, String> translation = translations.get(0);
        String translatedText = translation.get("translatedText");

        return translatedText;
    }
}
