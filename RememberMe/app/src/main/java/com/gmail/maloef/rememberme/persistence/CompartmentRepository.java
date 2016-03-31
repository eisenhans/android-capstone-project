package com.gmail.maloef.rememberme.persistence;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.gmail.maloef.rememberme.RememberMeConstants;
import com.gmail.maloef.rememberme.domain.BoxOverview;
import com.gmail.maloef.rememberme.domain.CompartmentOverview;
import com.gmail.maloef.rememberme.domain.Word;

import java.util.Date;

import javax.inject.Inject;

public class CompartmentRepository {

    private Context context;
    private ContentResolver contentResolver;

    @Inject
    public CompartmentRepository(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public BoxOverview getBoxOverview(int boxId) {
        BoxOverview boxOverview = new BoxOverview();
        for (int compartment = 1; compartment <= RememberMeConstants.NUMBER_OF_COMPARTMENTS; compartment++) {
            CompartmentOverview compartmentOverview = getCompartmentOverview(boxId, compartment);
            boxOverview.putCompartmentOverview(compartment, compartmentOverview);
        }
        return boxOverview;
    }

    public CompartmentOverview getCompartmentOverview(int boxId, int compartment) {
        WordCursor wordCursor = new WordCursor(
                contentResolver.query(
                        RememberMeProvider.Word.WORDS,
                        new String[] {WordColumns.LAST_REPEAT_DATE},
                        WordColumns.BOX_ID + " = ? and " + WordColumns.COMPARTMENT + " = ?",
                        new String[] {String.valueOf(boxId), String.valueOf(compartment)},
                        WordColumns.LAST_REPEAT_DATE + " desc"));

        int wordCount = wordCursor.getCount();
        long earliestLastRepeatDate = 0;
        for (Word word : wordCursor) {
            earliestLastRepeatDate = earlierRepeatDate(earliestLastRepeatDate, word.lastRepeatDate);
        }
        wordCursor.close();

        logInfo("compartment " + compartment + " contains " + wordCount + " words, repeated on " + new Date(earliestLastRepeatDate));
        return new CompartmentOverview(wordCount, earliestLastRepeatDate);
    }

    private long earlierRepeatDate(long first, long second) {
        if (first == 0) {
            return second;
        }
        if (second == 0) {
            return first;
        }
        return Math.min(first, second);
    }

    void logInfo(String message) {
        Log.i(getClass().getSimpleName(), message);
    }
}
