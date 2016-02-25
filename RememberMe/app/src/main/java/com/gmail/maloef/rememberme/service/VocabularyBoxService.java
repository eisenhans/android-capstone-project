package com.gmail.maloef.rememberme.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxColumns;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxCursor;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxProvider;
import com.venmo.cursor.IterableCursor;

public class VocabularyBoxService {

    private Context context;
    private ContentResolver contentResolver;

    public VocabularyBoxService(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public VocabularyBox findBox(int id) {
        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(contentResolver.query(
                VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES,
                null,
                VocabularyBoxColumns._ID + " = ?",
                new String[]{String.valueOf(id)},
                null));

        boxCursor.moveToFirst();
        VocabularyBox box = boxCursor.peek();
        boxCursor.close();

        return box;
    }

    public boolean isOneBoxSaved() {
        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(
                contentResolver.query(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null));

        return !boxCursor.isEmpty();
    }
    
    public int createDefaultBox() {
        // ToDo: look up language from phone settings
        String boxName = context.getResources().getString(R.string.default_name);
        int defaultBoxId = createBox(boxName, "undefined", "de", VocabularyBox.TRANSLATION_DIRECTION_MIXED, true);

        // ToDo: remove
        createBox("English", "en", "de", VocabularyBox.TRANSLATION_DIRECTION_MIXED, false);
        createBox("Spanish", "es", "de", VocabularyBox.TRANSLATION_DIRECTION_MIXED, false);

        return defaultBoxId;
    }

    public int createBox(String boxName, String foreignLanguage, String nativeLanguage, int translationDirection, boolean select) {
        ContentValues values = new ContentValues();
        values.put(VocabularyBoxColumns.NAME, boxName);
        values.put(VocabularyBoxColumns.FOREIGN_LANGUAGE, foreignLanguage);
        values.put(VocabularyBoxColumns.NATIVE_LANGUAGE, nativeLanguage);
        values.put(VocabularyBoxColumns.TRANSLATION_DIRECTION, translationDirection);

        int selectInt = select ? 1 : 0;
        values.put(VocabularyBoxColumns.IS_CURRENT, selectInt);

        if (select) {
            unselectAllBoxes();
        }
        Uri uri = contentResolver.insert(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, values);
        String lastPathSegment = uri.getLastPathSegment();

        return Integer.valueOf(lastPathSegment);
    }

    public String[] getBoxNames() {
        IterableCursor<VocabularyBox> boxes = new VocabularyBoxCursor(
                contentResolver.query(
                        VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES,
                        new String[] {VocabularyBoxColumns.NAME},
                        null, null, null));

        int count = boxes.getCount();
        logInfo("found " + count + " boxes");

        String[] boxNames = new String[count];
        int i = 0;
        for (VocabularyBox box : boxes) {
            boxNames[i] = box.name;
            i++;
        }
        boxes.close();
        return boxNames;
    }

    public VocabularyBox getSelectedBox() {
        IterableCursor<VocabularyBox> boxes = new VocabularyBoxCursor(
                contentResolver.query(
                        VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, null,
                        VocabularyBoxColumns.IS_CURRENT + " = 1", null,
                        null));

        return boxes.moveToFirst() ? boxes.peek() : null;
    }

    public VocabularyBox selectBoxByName(String boxName) {
        VocabularyBoxCursor idCursor = new VocabularyBoxCursor(contentResolver.query(
                VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES,
                new String[]{"_id"},
                VocabularyBoxColumns.NAME + " = ?",
                new String[]{boxName},
                null));

        if (!idCursor.moveToFirst()) {
            logWarn("no box found with name " + boxName);
            return null;
        }
        int id = idCursor.peek()._id;
        idCursor.close();

        selectBox(id);

        return findBox(id);
    }

    void selectBox(int id) {
        unselectAllBoxes();

        ContentValues select = new ContentValues();
        select.put(VocabularyBoxColumns.IS_CURRENT, 1);
        contentResolver.update(
                VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES,
                select,
                "_id = ?",
                new String[]{String.valueOf(id)});
    }

    void unselectAllBoxes() {
        ContentValues unselect = new ContentValues();
        unselect.put(VocabularyBoxColumns.IS_CURRENT, 0);
        contentResolver.update(
                VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES,
                unselect,
                null,
                null);
    }

    public int updateBoxName(int id, String boxName) {
        ContentValues values = new ContentValues();
        values.put(VocabularyBoxColumns.NAME, boxName);
        return update(id, values);
    }

    public int updateForeignLanguage(int id, String foreignLanguage) {
        ContentValues values = new ContentValues();
        values.put(VocabularyBoxColumns.FOREIGN_LANGUAGE, foreignLanguage);
        return update(id, values);
    }

    public int updateNativeLanguage(int id, String nativeLanguage) {
        ContentValues values = new ContentValues();
        values.put(VocabularyBoxColumns.NATIVE_LANGUAGE, nativeLanguage);
        return update(id, values);
    }

    public int updateTranslationDirection(int id, int translationDirection) {
        ContentValues values = new ContentValues();
        values.put(VocabularyBoxColumns.TRANSLATION_DIRECTION, translationDirection);
        return update(id, values);
    }

    private int update(int id, ContentValues newValues) {
        String[] idStringArray = new String[] {String.valueOf(id)};
        return contentResolver.update(VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES, newValues, "_id = ?", idStringArray);
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    void logWarn(String message) {
        Log.w(getClass().getSimpleName(), message);
    }
}
