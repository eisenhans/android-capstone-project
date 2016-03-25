package com.gmail.maloef.rememberme.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    static final long MILLIS_IN_DAY = 1000 * 60 * 60 * 24;

    public static long getDaysBetweenMidnight(long first, long second) {
        Calendar firstCal = getMidnightOfSameDay(first);
        Calendar secondCal = getMidnightOfSameDay(second);

        long diff = secondCal.getTimeInMillis() - firstCal.getTimeInMillis();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

        return days;
    }

    static Calendar getMidnightOfSameDay(long millis) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(millis));

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }
}
