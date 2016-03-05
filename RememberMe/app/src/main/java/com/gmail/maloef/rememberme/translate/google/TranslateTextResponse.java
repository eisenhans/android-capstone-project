package com.gmail.maloef.rememberme.translate.google;

import java.util.List;
import java.util.Map;

public class TranslateTextResponse extends GoogleTranslateResponse<Map<String, List<Map<String, String>>>> {

    public Translation getTranslation() {
        List<Map<String, String>> translations = data.get("translations");
        Map<String, String> translationMap = translations.get(0);

        return Translation.create(translationMap.get("detectedSourceLanguage"), translationMap.get("translatedText"));
    }
}
