package com.hromovych.android.bookstats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holders {
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

    public abstract static class BaseHolder extends RecyclerView.ViewHolder {

        abstract public void bind(Book book);

        public BaseHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}