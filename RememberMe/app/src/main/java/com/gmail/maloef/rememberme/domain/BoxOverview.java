package com.gmail.maloef.rememberme.domain;

import com.gmail.maloef.rememberme.RememberMeConstants;

import java.util.HashMap;
import java.util.Map;

public class BoxOverview {

    Map<Integer, CompartmentOverview> overviewMap = new HashMap<>();

    public void putCompartmentOverview(int compartment, CompartmentOverview compartmentOverview) {
        overviewMap.put(compartment, compartmentOverview);
    }

    public int getWordCount(int compartment) {
        return overviewMap.get(compartment).wordCount;
    }

    public long getEarliestLastRepeatDate(int compartment) {
        return overviewMap.get(compartment).earliestLastRepeatDate;
    }

    public int getWordDueCount(int compartment) {
        return overviewMap.get(compartment).wordsDue;
    }

    public boolean isWordDue(int compartment) {
        return getWordDueCount(compartment) > 0;
    }

    public int getWordDueCount() {
        int total = 0;
        for (int compartment = 1; compartment < RememberMeConstants.NUMBER_OF_COMPARTMENTS; compartment++) {
            total += getWordDueCount(compartment);
        }
        return total;
    }
}
