package com.gmail.maloef.rememberme.service;

import android.content.ContentResolver;
import android.content.Context;

import com.gmail.maloef.rememberme.domain.BoxOverview;
import com.gmail.maloef.rememberme.domain.CompartmentOverview;
import com.gmail.maloef.rememberme.persistence.VocabularyBoxProvider;
import com.gmail.maloef.rememberme.persistence.WordColumns;
import com.gmail.maloef.rememberme.persistence.WordCursor;

public class CompartmentService {

    private Context context;
    private ContentResolver contentResolver;

    public CompartmentService(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public BoxOverview getBoxOverview(int boxId) {
        BoxOverview boxOverview = new BoxOverview();
        for (int compartment = 1; compartment <= 5; compartment++) {
            CompartmentOverview compartmentOverview = getCompartmentOverview(boxId, compartment);
            boxOverview.putCompartmentOverview(compartment, compartmentOverview);
        }
        return boxOverview;
    }

    public CompartmentOverview getCompartmentOverview(int boxId, int compartment) {
        WordCursor wordCursor = new WordCursor(
                contentResolver.query(
                        VocabularyBoxProvider.Word.WORDS,
                        new String[] {WordColumns.LAST_REPEAT_DATE},
                        WordColumns.BOX_ID + " = ? and " + WordColumns.COMPARTMENT + " = ?",
                        new String[] {String.valueOf(boxId), String.valueOf(compartment)},
                        WordColumns.LAST_REPEAT_DATE + " desc"));

        int wordCount = wordCursor.getCount();
        Long earliestLastRepeatDate = null;
        if (wordCursor.moveToFirst()) {
            earliestLastRepeatDate = wordCursor.peek().lastRepeatDate;
        }
        wordCursor.close();

        return new CompartmentOverview(wordCount, earliestLastRepeatDate);
    }
}
