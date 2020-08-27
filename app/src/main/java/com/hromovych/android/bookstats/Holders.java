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
            itemView.setOnClickListener(this);
        }

        @Override
        public void bind(Book book) {
            titleView.setText(book.getCategory());
        }

        @Override
        public void onClick(View v) {
        }
    }

    public abstract static class BaseHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        abstract public void bind(Book book);

        public BaseHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}