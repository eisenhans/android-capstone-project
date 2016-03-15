package com.gmail.maloef.rememberme.persistence;

import android.database.Cursor;

import com.gmail.maloef.rememberme.domain.VocabularyBox;
import com.venmo.cursor.IterableCursorWrapper;

public class VocabularyBoxCursor extends IterableCursorWrapper<VocabularyBox> {

    public VocabularyBoxCursor(Cursor cursor) {
        super(cursor);
    }

    @Override
    public VocabularyBox peek() {
        VocabularyBox vocabularyBox = new VocabularyBox();
        vocabularyBox.id = getInteger(VocabularyBoxColumns.ID, -1);
        vocabularyBox.name = getString(VocabularyBoxColumns.NAME, "");
        vocabularyBox.foreignLanguage = getString(VocabularyBoxColumns.FOREIGN_LANGUAGE, "");
        vocabularyBox.nativeLanguage = getString(VocabularyBoxColumns.NATIVE_LANGUAGE, "");
        vocabularyBox.translationDirection = getInteger(VocabularyBoxColumns.TRANSLATION_DIRECTION, -1);
        vocabularyBox.isCurrent = getBoolean(VocabularyBoxColumns.IS_CURRENT, false);

        return vocabularyBox;
    }
}
