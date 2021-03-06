package com.hromovych.android.bookstats.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.hromovych.android.bookstats.database.BookDBSchema.BookTable;
import com.hromovych.android.bookstats.helpersItems.Book;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BookLab {

    private static BookLab sBookLab;

    private final Context mContext;
    private final SQLiteDatabase mDatabase;


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

        try (BookCursorWrapper cursor = queryBooks(
                BookTable.Cols.STATUS + " = ?",
                new String[]{s})) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        }
        return books;
    }

    public List<Book> getBooksByWhereArgsMap(Map<String, String> map) {
        List<Book> books = new ArrayList<>();
        String whereClause = TextUtils.join(" AND ", map.keySet());

        try (BookCursorWrapper cursor = queryBooks(
                whereClause,
                map.values().toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        }
        return books;
    }

    public List<Book> getBooksByStatus(String s, String orderBy) {
        List<Book> books = new ArrayList<>();

        try (BookCursorWrapper cursor = queryBooks(
                BookTable.Cols.STATUS + " = ?",
                new String[]{s}, orderBy)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        }
        return books;
    }

    public Book getBook(UUID id) {

        try (BookCursorWrapper cursor = queryBooks(
                BookTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        )) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getBook();
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

    public List<String> getColumnItems(String column) {
        List<String> columnItems = new ArrayList<>();

        try (Cursor cursor = mDatabase.query(
                BookTable.NAME,
                new String[]{column},
                null,
                null,
                null,
                null,
                null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String item = cursor.getString(0);
                if (item == null) item = "";
                columnItems.add(item);
                cursor.moveToNext();
            }
        }
        Set<String> set = new HashSet<>(columnItems);
        columnItems.clear();
        columnItems.addAll(set);
        columnItems.remove(null);
//        columnItems.remove("");
        return columnItems;

    }

    public void extendFromBase(SQLiteDatabase database) {

        try (BookCursorWrapper cursor = new BookCursorWrapper(database.query(
                BookTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                sBookLab.addBook(cursor.getBook());
                cursor.moveToNext();
            }
        }
    }

}
