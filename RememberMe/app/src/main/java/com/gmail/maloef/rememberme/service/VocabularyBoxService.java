package com.gmail.maloef.rememberme.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxColumns;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxCursor;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxProvider;

public class VocabularyBoxService {

    private Context context;
    private ContentResolver contentResolver;

    public VocabularyBoxService(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public boolean isOneBoxSaved() {
        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(
                contentResolver.query(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null));

        return !boxCursor.isEmpty();
    }
    
    public void createDefaultBox() {
        ContentValues values = new ContentValues();
        values.put(VocabularyBoxColumns.NAME, context.getResources().getString(R.string.default_name));
        values.put(VocabularyBoxColumns.FOREIGN_LANGUAGE, "");

        // ToDo: look up language from phone settings
        values.put(VocabularyBoxColumns.NATIVE_LANGUAGE, "German");
        values.put(VocabularyBoxColumns.TRANSLATION_DIRECTION, VocabularyBox.TRANSLATION_DIRECTION_MIXED);

        contentResolver.insert(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, values);
        logInfo("created default vocabulary box");
    }

    public String[] getBoxNames() {
        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(
                contentResolver.query(
                        VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES,
                        new String[] {VocabularyBoxColumns.NAME},
                        null, null, null));

        String[] boxNames = new String[boxCursor.getCount()];
        for (int i = 0; i < boxNames.length; i++) {
            boxCursor.moveToNext();
            boxNames[i] = boxCursor.peek().name;
        }
        return boxNames;
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
