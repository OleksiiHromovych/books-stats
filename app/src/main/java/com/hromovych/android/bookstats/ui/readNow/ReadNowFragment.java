package com.hromovych.android.bookstats.ui.readNow;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hromovych.android.bookstats.Book;
import com.hromovych.android.bookstats.BookLab;
import com.hromovych.android.bookstats.Callbacks;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.UnknownDate;

import java.util.List;

public class ReadNowFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private BookAdapter mAdapter;
    private Callbacks mCallbacks;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_now, container, false);
        mRecyclerView = view.findViewById(R.id.read_now_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void updateUI() {
        BookLab bookLab = BookLab.get(getActivity());
        List<Book> books = bookLab.getBooksByStatus(getResources()
                .getString(R.string.title_read_now));

        if (mAdapter == null) {
            mAdapter = new BookAdapter(books);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setBooks(books);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class BookHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView count;
        private TextView bookName;
        private TextView author;
        private TextView pages;
        private TextView startDate;


        private Book mBook;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_book, parent, false));

            count = itemView.findViewById(R.id.book_count);
            bookName = itemView.findViewById(R.id.book_name);
            author = itemView.findViewById(R.id.author);
            pages = itemView.findViewById(R.id.book_pages);
            startDate = itemView.findViewById(R.id.details_up);
            startDate.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(this);
        }

        public void bind(Book book, int pos) {
            mBook = book;
            count.setText("" + (pos + 1));
            bookName.setText(mBook.getBookName());
            author.setText(mBook.getAuthor());
            pages.setText("" + mBook.getPages());
            if (!mBook.getStartDate().equals(new UnknownDate().getUnknownDate()))
                startDate.setText(DateFormat.format("MMM dd, yyyy", mBook.getStartDate()));

        }

        @Override
        public void onClick(View v) {
            mCallbacks.onBookSelected(mBook);

        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {

        private List<Book> mBooks;

        public BookAdapter(List<Book> books) {
            mBooks = books;
        }

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new BookHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            Book book = mBooks.get(position);
            holder.bind(book, position);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        public List<Book> getBooks() {
            return mBooks;
        }

        public void setBooks(List<Book> books) {
            mBooks = books;
        }
    }
}
