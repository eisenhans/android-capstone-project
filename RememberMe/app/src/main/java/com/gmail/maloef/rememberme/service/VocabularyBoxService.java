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
import com.venmo.cursor.IterableCursor;

import java.util.ArrayList;
import java.util.List;

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
        values.put(VocabularyBoxColumns.IS_CURRENT, 1);

        contentResolver.insert(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, values);

        // ToDo: remove
        values.put(VocabularyBoxColumns.IS_CURRENT, 0);
        values.put(VocabularyBoxColumns.NAME, "English");
        contentResolver.insert(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, values);
        values.put(VocabularyBoxColumns.NAME, "Spanish");
        contentResolver.insert(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, values);

        logInfo("created default vocabulary box");
    }

    public List<String> getBoxNames() {
        IterableCursor<VocabularyBox> boxes = new VocabularyBoxCursor(
                contentResolver.query(
                        VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES,
                        new String[] {VocabularyBoxColumns.NAME},
                        null, null, null));

        int count = boxes.getCount();
        logInfo("found " + count + " boxes");

        List<String> boxNames = new ArrayList<String>(count);
        for (VocabularyBox box : boxes) {
            boxNames.add(box.name);
        }
        boxes.close();
        return boxNames;
    }

    public VocabularyBox getCurrentBox() {
        IterableCursor<VocabularyBox> boxes = new VocabularyBoxCursor(
                contentResolver.query(
                        VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, null,
                        VocabularyBoxColumns.IS_CURRENT + " = 1", null,
                        null));

        return boxes.moveToFirst() ? boxes.peek() : null;
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
