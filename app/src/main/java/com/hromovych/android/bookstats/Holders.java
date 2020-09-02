package com.hromovych.android.bookstats;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holders {

    public static final int CATEGORY_VIEWTYPE = 1;
    public static final int DATE_VIEWTYPE = 2;
    public static final String BOOK_CATEGORY_TEXT = "book_category_text";
    public static final String BOOK_DATE_TEXT = "book_date_text";

    public static class CategoryHolder extends BaseHolder {

        private TextView titleView;

        public CategoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_group, parent, false));
            titleView = (TextView) itemView.findViewById(R.id.group_book_category);
        }

        @Override
        public void bind(Book book) {
            titleView.setText(book.getCategory());
        }

    }

    public static class DateHolder extends BaseHolder {
        private TextView titleView;

        public DateHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_group, parent, false));
            titleView = (TextView) itemView.findViewById(R.id.group_book_category);
        }

        @Override
        public void bind(Book book) {
            titleView.setText(DateFormat.format("yyyy", book.getEndDate()));
        }

    }


    public abstract static class BaseHolder extends RecyclerView.ViewHolder {

        abstract public void bind(Book book);

        public BaseHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}