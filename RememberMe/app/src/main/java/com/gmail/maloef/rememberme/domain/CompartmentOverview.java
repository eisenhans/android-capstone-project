package com.gmail.maloef.rememberme.domain;

public class CompartmentOverview {

    public int wordCount;
    public Long earliestLastRepeatDate;
    public boolean wordDue;

    public CompartmentOverview(int wordCount, long earliestLastRepeatDate, boolean wordDue) {
        this.wordCount = wordCount;
        this.earliestLastRepeatDate = earliestLastRepeatDate;
        this.wordDue = wordDue;
    }
}
