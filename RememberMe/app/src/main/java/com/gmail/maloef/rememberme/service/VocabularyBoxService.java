package com.gmail.maloef.rememberme.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.gmail.maloef.rememberme.R;
import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxColumns;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxCursor;
import com.gmail.maloef.rememberme.persistence.RememberMeProvider;

import java.util.Arrays;
import java.util.Locale;

import javax.inject.Inject;

public class VocabularyBoxService {

    private Context context;
    private ContentResolver contentResolver;

    @Inject
    public VocabularyBoxService(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public VocabularyBox findBox(int id) {
        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(contentResolver.query(
                RememberMeProvider.VocabularyBox.VOCABULARY_BOXES,
                null,
                VocabularyBoxColumns._ID + " = ?",
                new String[]{String.valueOf(id)},
                null));

        boxCursor.moveToFirst();
        VocabularyBox box = boxCursor.peek();
        boxCursor.close();

        return box;
    }

    public VocabularyBox findBoxByName(String boxName) {
        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(contentResolver.query(
                RememberMeProvider.VocabularyBox.VOCABULARY_BOXES,
                null,
                VocabularyBoxColumns.NAME + " = ?",
                new String[]{boxName},
                null));

        VocabularyBox box = boxCursor.moveToFirst() ? boxCursor.peek() : null;
        boxCursor.close();

        return box;
    }

    public boolean isBoxSaved(String boxName) {
        Cursor cursor = contentResolver.query(
                RememberMeProvider.VocabularyBox.VOCABULARY_BOXES,
                new String[]{VocabularyBoxColumns._ID},
                VocabularyBoxColumns.NAME + " = ?",
                new String[]{boxName},
                null);

        boolean result = cursor.moveToFirst();
        cursor.close();

        return result;
    }

    public boolean isOneBoxSaved() {
        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(
                contentResolver.query(RememberMeProvider.VocabularyBox.VOCABULARY_BOXES, null, null, null, null));

        boolean empty = boxCursor.isEmpty();
        boxCursor.close();

        return !empty;
    }
    
    public int createDefaultBox() {
        // ToDo: look up language from phone settings
        String boxName = context.getResources().getString(R.string.default_name);
        String nativeLanguage = Locale.getDefault().getLanguage();
        logInfo("default language of device: " + nativeLanguage + ", foreign language to be detected");
        int defaultBoxId = createBox(boxName, null, nativeLanguage, VocabularyBox.TRANSLATION_DIRECTION_MIXED, true);

        // ToDo: remove
        createBox("English", "en", nativeLanguage, VocabularyBox.TRANSLATION_DIRECTION_MIXED, false);
        createBox("Spanish", "es", nativeLanguage, VocabularyBox.TRANSLATION_DIRECTION_MIXED, false);

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
        Uri uri = contentResolver.insert(RememberMeProvider.VocabularyBox.VOCABULARY_BOXES, values);
        String lastPathSegment = uri.getLastPathSegment();

        logInfo("created box: " + values + ", uri: " + uri);

        return Integer.valueOf(lastPathSegment);
    }

    public String[] getBoxNames() {
        // alphabetical order does not work - problem in IterableCursorWrapper implementation?
//        VocabularyBoxCursor boxCursor = new VocabularyBoxCursor(
//                contentResolver.query(
//                        VocabularyBoxProvider.VocabularyBox.VOCABULARY_BOXES,
//                        new String[] {VocabularyBoxColumns.NAME},
//                        null, null, VocabularyBoxColumns.NAME + " collate nocase"));

        Cursor boxCursor = contentResolver.query(
                RememberMeProvider.VocabularyBox.VOCABULARY_BOXES,
                new String[]{VocabularyBoxColumns.NAME},
                null, null, VocabularyBoxColumns.NAME + " collate nocase");

        int count = boxCursor.getCount();
        logInfo("found " + count + " boxes");

        String[] boxNames = new String[count];
        int i = 0;
        while (boxCursor.moveToNext()) {
            //boxNames[i] = boxCursor.peek().name;
            boxNames[i] = boxCursor.getString(boxCursor.getColumnIndex(VocabularyBoxColumns.NAME));
            i++;
        }
        boxCursor.close();
        logInfo("box names found: " + Arrays.asList(boxNames));
        return boxNames;
    }

    public VocabularyBox getSelectedBox() {
        VocabularyBoxCursor boxes = new VocabularyBoxCursor(
                contentResolver.query(
                        RememberMeProvider.VocabularyBox.VOCABULARY_BOXES, null,
                        VocabularyBoxColumns.IS_CURRENT + " = 1", null,
                        null));

        VocabularyBox box = boxes.moveToFirst() ? boxes.peek() : null;
        boxes.close();

        return box;
    }

    public VocabularyBox selectBoxByName(String boxName) {
        VocabularyBoxCursor idCursor = new VocabularyBoxCursor(contentResolver.query(
                RememberMeProvider.VocabularyBox.VOCABULARY_BOXES,
                new String[]{"_id"},
                VocabularyBoxColumns.NAME + " = ?",
                new String[]{boxName},
                null));

        if (!idCursor.moveToFirst()) {
            logWarn("no box found with name " + boxName);
            idCursor.close();
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
                RememberMeProvider.VocabularyBox.VOCABULARY_BOXES,
                select,
                "_id = ?",
                new String[]{String.valueOf(id)});
    }

    void unselectAllBoxes() {
        ContentValues unselect = new ContentValues();
        unselect.put(VocabularyBoxColumns.IS_CURRENT, 0);
        contentResolver.update(
                RememberMeProvider.VocabularyBox.VOCABULARY_BOXES,
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
        return contentResolver.update(RememberMeProvider.VocabularyBox.VOCABULARY_BOXES, newValues, "_id = ?", idStringArray);
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    void logWarn(String message) {
        Log.w(getClass().getSimpleName(), message);
    }
}
