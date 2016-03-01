package com.gmail.maloef.rememberme.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.gmail.maloef.rememberme.persistence.VocabularyBoxProvider;
import com.gmail.maloef.rememberme.persistence.WordColumns;

import java.util.Date;

public class WordService {

    private Context context;
    private ContentResolver contentResolver;

    public WordService(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public int createWord(int boxId, String foreignWord, String nativeWord) {
        return createWord(boxId, 1, foreignWord, nativeWord);
    }

    public int createWord(int boxId, int compartment, String foreignWord, String nativeWord) {
        ContentValues values = new ContentValues();
        values.put(WordColumns.BOX_ID, boxId);
        values.put(WordColumns.COMPARTMENT, compartment);
        values.put(WordColumns.FOREIGN_WORD, foreignWord);
        values.put(WordColumns.NATIVE_WORD, nativeWord);
        values.put(WordColumns.CREATION_DATE, new Date().getTime());

        Uri uri = contentResolver.insert(VocabularyBoxProvider.Word.WORDS, values);
        String lastPathSegment = uri.getLastPathSegment();
        logInfo("created word: " + values + ", uri: " + uri);

        return Integer.valueOf(lastPathSegment);
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }

    public void updateRepeatDate(int id) {
        ContentValues values = new ContentValues();
        long now = new Date().getTime();
        values.put(WordColumns.LAST_REPEAT_DATE, now);

        contentResolver.update(VocabularyBoxProvider.Word.WORDS, values, WordColumns._ID + " = ?", new String[]{String.valueOf(id)});
    }
}
