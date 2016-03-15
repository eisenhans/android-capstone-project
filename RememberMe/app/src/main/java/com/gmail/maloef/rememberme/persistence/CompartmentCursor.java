package com.gmail.maloef.rememberme.persistence;

import android.database.Cursor;

import com.gmail.maloef.rememberme.domain.Compartment;
import com.venmo.cursor.IterableCursorWrapper;

public class CompartmentCursor extends IterableCursorWrapper<Compartment> {

    public CompartmentCursor(Cursor cursor) {
        super(cursor);
    }

    @Override
    public Compartment peek() {
        Compartment compartment = new Compartment();
        compartment.id = getInteger(CompartmentColumns.ID, -1);
        compartment.vocabularyBox = getInteger(CompartmentColumns.VOCABULARY_BOX, -1);
        compartment.number = getInteger(CompartmentColumns.NUMBER, -1);

        return compartment;
    }
}
