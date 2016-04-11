package com.gmail.maloef.rememberme.service;

import com.gmail.maloef.rememberme.domain.Word;
import com.gmail.maloef.rememberme.util.DateUtils;

import java.util.Date;

public class RepeatWordScheduler {

    /**
     * Words from compartment 1/2/3/4/5 should be repeated after 0/2/7/30/90 days.
     */
    public boolean isWordDue(Word word) {
        return isWordDue(word.compartment, word.lastRepeatDate);
    }

    public boolean isWordDue(int compartment, long lastRepeatDate) {
        if (compartment < 1 || compartment > 5) {
            return false;
        }
        if (lastRepeatDate == 0 || lastRepeatDate == Long.MAX_VALUE) {
            return true;
        }
        long now = new Date().getTime();
        switch(compartment) {
            case 1: return true;
            case 2: return DateUtils.getDaysBetweenMidnight(lastRepeatDate, now) >= 2;
            case 3: return DateUtils.getDaysBetweenMidnight(lastRepeatDate, now) >= 7;
            case 4: return DateUtils.getDaysBetweenMidnight(lastRepeatDate, now) >= 30;
            case 5: return DateUtils.getDaysBetweenMidnight(lastRepeatDate, now) >= 90;
            default: return false;
        }
    }
}
