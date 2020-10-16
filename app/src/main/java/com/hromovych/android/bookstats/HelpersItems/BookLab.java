package com.hromovych.android.bookstats.HelpersItems;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hromovych.android.bookstats.database.BookBaseHelper;
import com.hromovych.android.bookstats.database.BookCursorWrapper;
import com.hromovych.android.bookstats.database.BookDBSchema.BookTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BookLab {

    private static BookLab sBookLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;


    private BookLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new BookBaseHelper(mContext).getWritableDatabase();

    }

    public static BookLab get(Context context) {
        if (sBookLab == null)
            sBookLab = new BookLab(context);
        return sBookLab;
    }

    public static ContentValues getContentValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(BookTable.Cols.UUID, book.getId().toString());
        values.put(BookTable.Cols.NAME, book.getBookName());
        values.put(BookTable.Cols.AUTHOR, book.getAuthor());
        values.put(BookTable.Cols.PAGE, book.getPages());
        values.put(BookTable.Cols.CATEGORY, book.getCategory());
        values.put(BookTable.Cols.START_DATE, book.getStartDate().getTime());
        values.put(BookTable.Cols.END_DATE, book.getEndDate().getTime());
        values.put(BookTable.Cols.STATUS, book.getStatus());
        values.put(BookTable.Cols.TYPE, book.getType());
        values.put(BookTable.Cols.DESCRIPTION, book.getDescription());
        values.put(BookTable.Cols.LABEL, book.getLabel());
        return values;
    }

    public void addBook(Book b) {
        ContentValues contentValues = getContentValues(b);

        mDatabase.insert(BookTable.NAME, null, contentValues);
    }

    public int deleteBooks() {
        return mDatabase.delete(BookTable.NAME, null, null);
    }


    public List<Book> getBooksByStatus(String s) {
        List<Book> books = new ArrayList<>();

        BookCursorWrapper cursor = queryBooks(
                BookTable.Cols.STATUS + " = ?",
                new String[]{s});
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return books;
    }

    public List<Book> getBooksByWhereArgsMap(Map<String, String> map) {
        List<Book> books = new ArrayList<>();
        StringBuilder whereClause = new StringBuilder();
        for (String key : map.keySet()) {
            whereClause.append(key).append(" AND ");
        }
        whereClause.delete(whereClause.length() - 5, whereClause.length());
        BookCursorWrapper cursor = queryBooks(
                whereClause.toString(),
                (String[]) map.values().toArray(new String[0]));
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return books;
    }

    public List<Book> getBooksByStatus(String s, String orderBy) {
        List<Book> books = new ArrayList<>();

        BookCursorWrapper cursor = queryBooks(
                BookTable.Cols.STATUS + " = ?",
                new String[]{s}, orderBy);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return books;
    }

    public Book getBook(UUID id) {
        BookCursorWrapper cursor = queryBooks(
                BookTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getBook();
        } finally {
            cursor.close();
        }
    }

    public void updateBook(Book book) {
        String uuidString = book.getId().toString();
        ContentValues values = getContentValues(book);

        mDatabase.update(BookTable.NAME, values,
                BookTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private BookCursorWrapper
    queryBooks(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                BookTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                BookTable.Cols.END_DATE + " , " + BookTable.Cols.START_DATE);

        return new BookCursorWrapper(cursor);
    }

    private BookCursorWrapper queryBooks(String whereClause, String[] whereArgs, String orderBy) {
        Cursor cursor = mDatabase.query(
                BookTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                orderBy);

        return new BookCursorWrapper(cursor);
    }

    public void deleteBook(Book book) {
        String uuidString = book.getId().toString();
        mDatabase.delete(BookTable.NAME, BookTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public List<String> getColumnItems(String name) {
        List<String> columnItems = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                BookTable.NAME,
                new String[]{name},
                null,
                null,
                null,
                null,
                null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                columnItems.add(cursor.getString(0));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        Set<String> set = new HashSet<>(columnItems);
        columnItems.clear();
        columnItems.addAll(set);
        columnItems.remove(null);
        columnItems.remove("");
        return columnItems;

    }

    public void extendFromBase(SQLiteDatabase database) {
        BookCursorWrapper cursor = new BookCursorWrapper(database.query(
                BookTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null));

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                sBookLab.addBook(cursor.getBook());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

}
