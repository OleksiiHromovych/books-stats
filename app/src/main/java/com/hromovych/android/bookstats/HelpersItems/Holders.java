package com.hromovych.android.bookstats.HelpersItems;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.hromovych.android.bookstats.R;

import java.util.List;

public class Holders {

    public static class BookHolder extends BaseHolder
            implements View.OnClickListener {

        private final TextView count;
        private final TextView bookName;
        private final TextView author;
        private final TextView pages;

        private final LinearLayout pageLayout;

        protected Book mBook;

        public BookHolder(View itemView) {
            super(itemView);

            count = itemView.findViewById(R.id.book_count);
            bookName = itemView.findViewById(R.id.book_name);
            author = itemView.findViewById(R.id.author);
            pages = itemView.findViewById(R.id.book_pages);
            pageLayout = itemView.findViewById(R.id.page_layout);

            itemView.setOnClickListener(this);
        }

        public void bind(Book book, int pos) {
            mBook = book;
            count.setText(String.valueOf(pos + 1));
            bookName.setText(mBook.getBookName());
            author.setText(mBook.getAuthor());

            if (mBook.getPages() != 0) {
                pages.setText(String.valueOf(mBook.getPages()));
                pageLayout.setVisibility(View.VISIBLE);
            } else
                pageLayout.setVisibility(View.GONE);

        }

        @Override
        public void onClick(View v) {

        }
    }


    public static class Group implements Parent<Book> {

        private final List<Book> groupItems;
        private final String title;
        private boolean isExpanded = false;

        public Group(String title, List<Book> books) {
            groupItems = books;
            this.title = title;
        }

        public Group(String title, List<Book> books, boolean isExpanded) {
            groupItems = books;
            this.title = title;
            this.isExpanded = isExpanded;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public List<Book> getChildList() {
            return groupItems;
        }

        @Override
        public boolean isInitiallyExpanded() {
            return isExpanded;
        }
    }

    public static class GroupViewHolder extends ParentViewHolder {

        private final TextView mGroupTextView;
        private final TextView mGroupBookCount;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            mGroupTextView = itemView.findViewById(R.id.group_book_category);
            mGroupBookCount = itemView.findViewById(R.id.group_book_count_of_book);
        }

        public void bind(Group groupItem) {
            mGroupTextView.setText(groupItem.getTitle());
            mGroupBookCount.setText(String.valueOf(groupItem.getChildList().size()));
        }

        public void setOnClickListener(final View.OnClickListener onClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v);
                    if (isExpanded()) {
                        collapseView();
                    } else {
                        expandView();
                    }
                }
            });
        }
    }


    public abstract static class BaseHolder extends ChildViewHolder {

        public BaseHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}