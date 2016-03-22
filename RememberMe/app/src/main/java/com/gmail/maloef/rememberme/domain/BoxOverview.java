package com.gmail.maloef.rememberme.domain;

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
}
