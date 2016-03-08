package com.gmail.maloef.rememberme.persistence;

import android.database.Cursor;

import com.gmail.maloef.rememberme.domain.Language;
import com.venmo.cursor.IterableCursorWrapper;

public class LanguageCursor extends IterableCursorWrapper<Language> {

    public LanguageCursor(Cursor cursor) {
        super(cursor);
    }

    @Override
    public Language peek() {
        Language language = new Language();
        language.id = getInteger(LanguageColumns.ID, -1);
        language.code = getString(LanguageColumns.CODE, "");
        language.name = getString(LanguageColumns.NAME, "");
        language.nameCode = getString(LanguageColumns.NAME_CODE, "");

        return language;
    }
}
