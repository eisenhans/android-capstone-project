package com.gmail.maloef.rememberme.translate.google;

import android.util.Log;
import android.util.Pair;

import com.gmail.maloef.rememberme.domain.Language;

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

    // ToDo 07.03.16: delete
    public Language[] getLanguages() {
        List<Map<String, String>> languageList = data.get("languages");

        Language[] languages = new Language[languageList.size()];
        for (int i = 0; i < languages.length; i++) {
            Map item = languageList.get(i);
            languages[i] = new Language();
            languages[i].code = item.get("language").toString();
            languages[i].name = item.get("name").toString();
            languages[i].nameCode = "en";
        }
        return languages;
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
