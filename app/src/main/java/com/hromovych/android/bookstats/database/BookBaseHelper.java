package com.hromovych.android.bookstats.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.hromovych.android.bookstats.database.BookDBSchema.BookTable;

public class BookBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "bookBase.db";

    public BookBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BookTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                BookTable.Cols.UUID + ", " +
                BookTable.Cols.NAME + "," +
                BookTable.Cols.StartDATE + ", " +
                BookTable.Cols.EndDATE + ", " +
                BookTable.Cols.AUTHOR + ", " +
                BookTable.Cols.CATEGORY + ", " +
                BookTable.Cols.PAGE + ", " +
                BookTable.Cols.STATUS +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
