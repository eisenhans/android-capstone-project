package com.gmail.maloef.rememberme.persistence;

import android.database.Cursor;

import com.gmail.maloef.rememberme.domain.Word;
import com.venmo.cursor.IterableCursorWrapper;

public class WordCursor extends IterableCursorWrapper<Word> {

    public WordCursor(Cursor cursor) {
        super(cursor);
    }

    @Override
    public Word peek() {
        Word word = new Word();
        word._id = getInteger(WordColumns._ID, -1);
        word.compartment = getInteger(WordColumns.COMPARTMENT, -1);
        word.foreignWord = getString(WordColumns.FOREIGN_WORD, "");
        word.nativeWord = getString(WordColumns.NATIVE_WORD, "");
        word.creationDate = getLong(WordColumns.CREATION_DATE, -1);
        word.updateDate = getLong(WordColumns.UPDATE_DATE, -1);
        word.lastRepeatDate = getLong(WordColumns.LAST_REPEAT_DATE, -1);

        return word;
    }
}
