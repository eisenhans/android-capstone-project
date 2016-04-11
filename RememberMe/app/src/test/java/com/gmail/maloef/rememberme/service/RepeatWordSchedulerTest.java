package com.gmail.maloef.rememberme.service;

import com.gmail.maloef.rememberme.AbstractRobolectricTest;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RepeatWordSchedulerTest extends AbstractRobolectricTest {

    // a short period in milliseconds, long enough so that leap seconds are ignored
    static final long SHORT_PERIOD_MILLIS = 1000 * 60 + 1;

    long never1 = 0;
    long never2 = Long.MAX_VALUE;

    long now = new Date().getTime();
    LocalDate today = new LocalDate();

    RepeatWordScheduler scheduler = new RepeatWordScheduler();

    @Test
    public void isWordDueCompartment1() {
        assertTrue(scheduler.isWordDue(1, never1));
        assertTrue(scheduler.isWordDue(1, never2));

        assertTrue(scheduler.isWordDue(1, now));
        assertTrue(scheduler.isWordDue(1, today.toDate().getTime()));
    }

    @Test
    public void isWordDueCompartment2() {
        assertTrue(scheduler.isWordDue(2, never1));
        assertTrue(scheduler.isWordDue(2, never2));

        assertFalse(scheduler.isWordDue(2, now));
        assertFalse(scheduler.isWordDue(2, today.toDate().getTime()));

        long yesterdayMidnight = today.minusDays(1).toDate().getTime();

        assertFalse(scheduler.isWordDue(2, yesterdayMidnight));
        assertTrue(scheduler.isWordDue(2, yesterdayMidnight - SHORT_PERIOD_MILLIS));
    }

    @Test
    public void isWordDueCompartment3() {
        long sixDaysAgoMidnight = today.minusDays(6).toDate().getTime();
        assertFalse(scheduler.isWordDue(3, sixDaysAgoMidnight));
        assertTrue(scheduler.isWordDue(3, sixDaysAgoMidnight - SHORT_PERIOD_MILLIS));
    }

    @Test
    public void isWordDueCompartment4() {
        long twentyNineDaysAgoMidnight = today.minusDays(29).toDate().getTime();
        assertFalse(scheduler.isWordDue(4, twentyNineDaysAgoMidnight));
        assertTrue(scheduler.isWordDue(4, twentyNineDaysAgoMidnight - SHORT_PERIOD_MILLIS));
    }

    @Test
    public void isWordDueCompartment5() {
        long eightyNineDaysAgoMidnight = today.minusDays(89).toDate().getTime();
        assertFalse(scheduler.isWordDue(5, eightyNineDaysAgoMidnight));
        assertTrue(scheduler.isWordDue(5, eightyNineDaysAgoMidnight - SHORT_PERIOD_MILLIS));
    }

    @Test
    public void isWordDueCompartment6() {
        assertFalse(scheduler.isWordDue(6, never1));
        assertFalse(scheduler.isWordDue(6, never2));
        assertFalse(scheduler.isWordDue(6, now));
    }
}
