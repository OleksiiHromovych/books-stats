package com.hromovych.android.bookstats.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.hromovych.android.bookstats.database.BookDBSchema.BookTable;

public class BookBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 2;
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
                BookTable.Cols.START_DATE + ", " +
                BookTable.Cols.END_DATE + ", " +
                BookTable.Cols.AUTHOR + ", " +
                BookTable.Cols.CATEGORY + ", " +
                BookTable.Cols.PAGE + ", " +
                BookTable.Cols.TYPE + ", " +
                BookTable.Cols.STATUS +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2){

            db.execSQL("ALTER TABLE " + BookTable.NAME + " ADD COLUMN " + BookTable.Cols.TYPE +
                    " TEXT");
        }
    }
}
