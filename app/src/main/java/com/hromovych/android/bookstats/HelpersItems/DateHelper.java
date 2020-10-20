package com.hromovych.android.bookstats.HelpersItems;

import android.text.format.DateFormat;

import java.util.Date;

public class DateHelper extends Date {
    public static final Date unknownDate = new Date(0);
    public static final Date undefinedDate = new Date(1);
    public static final Date today = new Date();

    public static int getDateFormatString(Date date) {
        return Integer.parseInt(DateFormat.format("yyyy", date).toString());
    }
}

