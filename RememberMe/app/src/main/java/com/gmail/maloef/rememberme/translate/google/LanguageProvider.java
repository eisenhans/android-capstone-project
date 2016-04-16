package com.gmail.maloef.rememberme.translate.google;

import android.util.Pair;

import java.io.IOException;

public interface LanguageProvider {

    Pair<String, String>[] getLanguages(String nameCode) throws IOException;
}
