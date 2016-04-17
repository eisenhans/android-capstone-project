package com.gmail.maloef.rememberme.translate.google;

import android.util.Pair;

import java.util.List;
import java.util.Map;

public class AvailableLanguageResponse extends GoogleTranslateResponse<Map<String, List<Map<String, String>>>> {

    public Pair<String, String>[] getCodeLanguagePairs() {
        List<Map<String, String>> languageList = data.get("languages");

        Pair<String, String>[] pairs = new Pair[languageList.size()];
        for (int i = 0; i < pairs.length; i++) {
            String code = languageList.get(i).get("language").toString();
            String name = languageList.get(i).get("name").toString();
            pairs[i] = Pair.create(code, name);
        }
        return pairs;
    }
}
