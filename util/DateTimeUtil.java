package com.webling.graincorp.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class DateTimeUtil {
    public static boolean isToday(LocalDate date) {
        return date.isEqual(LocalDate.now());
    }

    public static boolean isYesterday(LocalDate date) {
        return date.isEqual(LocalDate.now().minusDays(1));
    }

    public static boolean isDatePlusOneOrAfter(long dateInMillis) {
        DateTime dateTime = new DateTime(dateInMillis);
        DateTime today = DateTime.now().withTimeAtStartOfDay();
        DateTime dateTimePlusOneDay = dateTime.plusDays(1).withTimeAtStartOfDay();
        return (today.isEqual(dateTimePlusOneDay) || today.isAfter(dateTimePlusOneDay));
    }
}
