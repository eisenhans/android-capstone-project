package com.gmail.maloef.rememberme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

public class RememberMePreferences {

    private static final String WORD_LIST_INFO_DIALOG_SHOWN = "wordListInfoDialogShown";

    private final SharedPreferences prefs;

    @Inject
    public RememberMePreferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean wasWordListInfoDialogShown() {
        return prefs.getBoolean(WORD_LIST_INFO_DIALOG_SHOWN, false);
    }

    public void setWordListInfoDialogShown() {
        putBoolean(WORD_LIST_INFO_DIALOG_SHOWN, true);
    }

    private void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}
