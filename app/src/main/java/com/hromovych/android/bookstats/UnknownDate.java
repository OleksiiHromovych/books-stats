package com.hromovych.android.bookstats;

import java.util.Date;

public class UnknownDate extends Date {
    private static Date date;

    public UnknownDate() {
        date = new Date(0);
    }

    public Date getUnknownDate() {
        return date;
    }

}

