package com.hromovych.android.bookstats.database;

public class BookDBSchema {
    public static final class BookTable {
        public static final String NAME = "books";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String START_DATE = "start_date";
            public static final String END_DATE = "end_date";
            public static final String AUTHOR = "author";
            public static final String PAGE = "page";
            public static final String CATEGORY = "category";
            public static final String STATUS = "status";
            public static final String TYPE = "type";
            public static final String DESCRIPTION = "description";
            public static final String LABEL = "label";
        }
    }
}
