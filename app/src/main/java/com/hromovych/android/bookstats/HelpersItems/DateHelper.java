package com.hromovych.android.bookstats.HelpersItems;

import android.text.format.DateFormat;

import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelper extends Date {
    public static final Date unknownDate = new Date(0);
    public static final Date undefinedDate = new Date(1);
    public static final Date today = new Date();

    public static int getDateFormatString(Date date) {
        return Integer.parseInt(DateFormat.format("yyyy", date).toString());
    }

    public static String getYearStringFromDate(Date date) {
        return DateFormat.format("yyyy", date).toString();
    }

    public static long getSecondFromDate(int year, int month, int day) {
        return new GregorianCalendar(year, month, day).
                getTimeInMillis();
    }
}

