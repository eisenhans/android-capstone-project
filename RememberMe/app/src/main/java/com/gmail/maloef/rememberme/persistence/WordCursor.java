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
        word.id = getInteger(WordColumns.ID, -1);
        word.compartment = getInteger(WordColumns.COMPARTMENT, -1);
        word.foreignWord = getString(WordColumns.FOREIGN_WORD, "");
        word.nativeWord = getString(WordColumns.NATIVE_WORD, "");
        word.creationDate = getLongOrNull(WordColumns.CREATION_DATE);
        word.updateDate = getLongOrNull(WordColumns.UPDATE_DATE);
        word.lastRepeatDate = getLongOrNull(WordColumns.LAST_REPEAT_DATE);

        return word;
    }

    private Long getLongOrNull(String columnName) {
        int index = getColumnIndex(columnName);
        if (!isValidIndex(index)) {
            return null;
        }
        // getLong(index) returns a long (not a Long). If the database value is null, 0 is returned. Therefore we handle this case separately.
        if (isNull(index)) {
            return null;
        }
        return getLong(index);
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < getColumnCount();
    }
}
