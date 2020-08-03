package com.hromovych.android.bookstats.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.hromovych.android.bookstats.Book;

import java.util.Date;
import java.util.UUID;

import static com.hromovych.android.bookstats.database.BookDBSchema.BookTable;

public class BookCursorWrapper extends CursorWrapper {

    public BookCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Book getBook() {
        String uuidString = getString(getColumnIndex(BookTable.Cols.UUID));
        String name = getString(getColumnIndex(BookTable.Cols.NAME));
        String author = getString(getColumnIndex(BookTable.Cols.AUTHOR));
        long startDate = getLong(getColumnIndex(BookTable.Cols.StartDATE));
        long endDate = getLong(getColumnIndex(BookTable.Cols.EndDATE));
        int page = getInt(getColumnIndex(BookTable.Cols.PAGE));
        String category = getString(getColumnIndex(BookTable.Cols.CATEGORY));
        String status = getString(getColumnIndex(BookTable.Cols.STATUS));

        Book book = new Book(UUID.fromString(uuidString));
        book.setAuthor(author);
        book.setBookName(name);
        book.setPages(page);
        book.setStartDate(new Date(startDate));
        book.setCategory(category);
        book.setStatus(status);
        book.setEndDate(new Date(endDate));

        return book;
    }

}
