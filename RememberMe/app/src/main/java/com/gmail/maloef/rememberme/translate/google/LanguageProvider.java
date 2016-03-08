package com.gmail.maloef.rememberme.translate.google;

import android.util.Pair;

public interface LanguageProvider {

    Pair<String, String>[] getCodeLanguagePairs(String nameCode);
}
