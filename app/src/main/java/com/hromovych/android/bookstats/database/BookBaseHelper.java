package com.hromovych.android.bookstats.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.hromovych.android.bookstats.database.BookDBSchema.BookTable;

public class BookBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 7;
    public static final String DATABASE_NAME = "bookBase.db";

    public BookBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BookTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                BookTable.Cols.UUID + ", " +
                BookTable.Cols.NAME + "," +
                BookTable.Cols.START_DATE + " integer, " +
                BookTable.Cols.END_DATE + " integer, " +
                BookTable.Cols.AUTHOR + ", " +
                BookTable.Cols.CATEGORY + ", " +
                BookTable.Cols.PAGE + " integer, " +
                BookTable.Cols.TYPE + ", " +
                BookTable.Cols.DESCRIPTION + ", " +
                BookTable.Cols.LABEL + ", " +
                BookTable.Cols.STATUS +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {

            db.execSQL("ALTER TABLE " + BookTable.NAME + " ADD COLUMN " + BookTable.Cols.TYPE +
                    " TEXT");
        }
        if (oldVersion < 3)
            db.execSQL("ALTER TABLE " + BookTable.NAME + " ADD COLUMN " + BookTable.Cols.DESCRIPTION
                    + " TEXT");
        if (oldVersion < 4)
            db.execSQL("ALTER TABLE " + BookTable.NAME + " ADD COLUMN " + BookTable.Cols.LABEL +
                    " TEXT");

        if (oldVersion < 6) {
            db.execSQL("create table templateBase (" +
                    " _id integer primary key autoincrement, " +
                    BookTable.Cols.UUID + ", " +
                    BookTable.Cols.NAME + "," +
                    BookTable.Cols.START_DATE + " integer, " +
                    BookTable.Cols.END_DATE + " integer, " +
                    BookTable.Cols.AUTHOR + ", " +
                    BookTable.Cols.CATEGORY + ", " +
                    BookTable.Cols.PAGE + " integer, " +
                    BookTable.Cols.TYPE + ", " +
                    BookTable.Cols.DESCRIPTION + ", " +
                    BookTable.Cols.LABEL + ", " +
                    BookTable.Cols.STATUS +
                    ")");
            db.execSQL("insert into templateBase (" +
                    BookTable.Cols.UUID + ", " +
                    BookTable.Cols.NAME + "," +
                    BookTable.Cols.START_DATE + ", " +
                    BookTable.Cols.END_DATE + ", " +
                    BookTable.Cols.AUTHOR + ", " +
                    BookTable.Cols.CATEGORY + ", " +
                    BookTable.Cols.PAGE + ", " +
                    BookTable.Cols.TYPE + ", " +
                    BookTable.Cols.DESCRIPTION + ", " +
                    BookTable.Cols.LABEL + ", " +
                    BookTable.Cols.STATUS +
                    ") select " +
                    BookTable.Cols.UUID + ", " +
                    BookTable.Cols.NAME + "," +
                    BookTable.Cols.START_DATE + ", " +
                    BookTable.Cols.END_DATE + ", " +
                    BookTable.Cols.AUTHOR + ", " +
                    BookTable.Cols.CATEGORY + ", " +
                    BookTable.Cols.PAGE + ", " +
                    BookTable.Cols.TYPE + ", " +
                    BookTable.Cols.DESCRIPTION + ", " +
                    BookTable.Cols.LABEL + ", " +
                    BookTable.Cols.STATUS + "" +
                    " from " + BookTable.NAME);
        }
        if (oldVersion < 7) {
            db.execSQL("DROP TABLE " + BookTable.NAME);
            db.execSQL("Alter table templateBase RENAME TO " + BookTable.NAME);
        }
    }

}
