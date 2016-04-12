package com.gmail.maloef.rememberme.domain;

public class CompartmentOverview {

    public int wordCount;
    public Long earliestLastRepeatDate;
    public int wordsDue;

    public CompartmentOverview(int wordCount, long earliestLastRepeatDate, int wordsDue) {
        this.wordCount = wordCount;
        this.earliestLastRepeatDate = earliestLastRepeatDate;
        this.wordsDue = wordsDue;
    }
}
