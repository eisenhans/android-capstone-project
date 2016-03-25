package com.gmail.maloef.rememberme.util;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm");

    @Test
    public void daysBetweenMidnightLeapYear() throws ParseException {
        // 2016 is a leap year
        Date first = dateFormat.parse("2016-02-28_17:23");
        Date second = dateFormat.parse("2016-03-01_00:12");
        Date third = dateFormat.parse("2017-03-01_23:59");

        assertEquals(2, DateUtils.getDaysBetweenMidnight(first.getTime(), second.getTime()));
        assertEquals(365, DateUtils.getDaysBetweenMidnight(second.getTime(), third.getTime()));
        assertEquals(367, DateUtils.getDaysBetweenMidnight(first.getTime(), third.getTime()));
    }

    @Test
    public void daysBetweenMidnightYesterdayAndToday() {
        long yesterday = 1458746815501L;
        long today = 1458810226347L;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals("2016-03-23", formatter.format(new Date(yesterday)));
        assertEquals("2016-03-24", formatter.format(new Date(today)));
        assertEquals(1, DateUtils.getDaysBetweenMidnight(yesterday, today));
    }
}
