package com.gmail.maloef.rememberme.domain;

public class CompartmentOverview {

    public int wordCount;
    public Long earliestLastRepeatDate;

    public CompartmentOverview(int wordCount, long earliestLastRepeatDate) {
        this.wordCount = wordCount;
        this.earliestLastRepeatDate = earliestLastRepeatDate;
    }
}
