package com.hromovych.android.bookstats;

public class Constants {

    public static final class Status {
        // For save status in bd.
        public static final String READ_NOW = "read now";
        public static final String READ_YET = "read yet";
        public static final String WANT_READ = "want read";
    }

    public static final class Priority {
        //For save priority in bd (Type is field for want read book: low, medium, high)
        public static final String LOW = "low";
        public static final String MEDIUM = "medium";
        public static final String HIGH = "high";
    }

    public static final class Type {
        //For save type in bd (Type is field for read now and yet books: paper, audio, electronic)
        public static final String PAPER = "paper";
        public static final String ELECTRONIC = "electronic";
        public static final String AUDIO = "audio";
    }
}