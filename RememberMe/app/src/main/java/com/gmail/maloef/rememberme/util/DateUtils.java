package com.gmail.maloef.rememberme.util;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class DateUtils {

    static final long MILLIS_IN_DAY = 1000 * 60 * 60 * 24;

    public static long getDaysBetweenMidnight(long first, long second) {
        LocalDate firstDate = new LocalDate(first);
        LocalDate secondDate = new LocalDate(second);
        return Days.daysBetween(firstDate, secondDate).getDays();

        // does not handle daylight saving time correctly!
//        Calendar firstCal = truncateToMidnight(first);
//        Calendar secondCal = truncateToMidnight(second);
//
//        long diff = secondCal.getTimeInMillis() - firstCal.getTimeInMillis();
//        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//
//        return days;
    }

//    public static Calendar truncateToMidnight(long millis) {
//        GregorianCalendar calendar = new GregorianCalendar();
//        calendar.setTime(new Date(millis));
//
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//
//        System.out.println("truncated date " + new Date(millis) + " to midnight: " + calendar.getTime());
//
//        return calendar;
//    }
}
