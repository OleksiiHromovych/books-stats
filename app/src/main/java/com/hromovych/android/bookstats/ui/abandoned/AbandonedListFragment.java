package com.hromovych.android.bookstats.ui.abandoned;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hromovych.android.bookstats.HelpersItems.Book;
import com.hromovych.android.bookstats.HelpersItems.DateHelper;
import com.hromovych.android.bookstats.HelpersItems.Holders;
import com.hromovych.android.bookstats.HelpersItems.SimpleFragment;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookLab;
import com.hromovych.android.bookstats.menuOption.settings.PreferencesManager;

import java.util.List;

public class AbandonedListFragment extends SimpleFragment {

    private RecyclerView mRecyclerView;
    private BookAdapter mAdapter;
    private boolean isFullDateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_books_list, container, false);

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        isFullDateFormat = new PreferencesManager(getContext()).isFullDateFormat();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_abandoned);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    public void updateUi() {
        BookLab bookLab = BookLab.get(getActivity());
        List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getString(R.string.title_abandoned)));

        if (mAdapter == null) {
            mAdapter = new AbandonedListFragment.BookAdapter(books);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setBooks(books);
            mAdapter.notifyDataSetChanged();
        }
        int count = books.size();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(
                getResources().getQuantityString(R.plurals.fragment_count_books_subtitile,
                        count, count));
    }

    private class BookHolder extends Holders.BookHolder
            implements View.OnClickListener {

        private final TextView startDate;
        private final TextView endDate;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_book, parent, false));

            startDate = itemView.findViewById(R.id.details_up);
            endDate = itemView.findViewById(R.id.details_down);

            startDate.setVisibility(View.VISIBLE);
            endDate.setVisibility(View.VISIBLE);
        }

        public void bind(Book book, int pos) {
            super.bind(book, pos);

            if (isFullDateFormat) {

                if (!mBook.getStartDate().equals(DateHelper.unknownDate)
                        && !mBook.getStartDate().equals(DateHelper.undefinedDate)) {
                    startDate.setVisibility(View.VISIBLE);
                    startDate.setText(String.format("+ %s", DateFormat.format("MMM dd, yyyy", mBook.getStartDate())));
                } else
                    startDate.setVisibility(View.INVISIBLE);

                if (!mBook.getEndDate().equals(DateHelper.unknownDate)
                        && !mBook.getEndDate().equals(DateHelper.undefinedDate)) {
                    endDate.setText(String.format("- %s", DateFormat.format("MMM dd, yyyy", mBook.getEndDate())));
                    endDate.setVisibility(View.VISIBLE);
                } else
                    endDate.setVisibility(View.INVISIBLE);

            } else {
                startDate.setText("");
                if (!mBook.getStartDate().equals(DateHelper.unknownDate) &&
                        !mBook.getEndDate().equals(DateHelper.unknownDate)
                        && !mBook.getStartDate().equals(DateHelper.undefinedDate)
                        && !mBook.getEndDate().equals(DateHelper.undefinedDate)) {

                    long days = (mBook.getEndDate().getTime() - mBook.getStartDate().getTime())
                            / 1000 / 60 / 60 / 24;
                    if (days != 0)
                        startDate.setText(String.valueOf(days));
                    else
                        startDate.setText("1");
                }
                endDate.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
//            mCallbacks.onBookSelected(mBook);
            Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
        }
    }


    private class BookAdapter extends RecyclerView.Adapter<AbandonedListFragment.BookHolder> {

        private List<Book> mBooks;

        public BookAdapter(List<Book> books) {
            mBooks = books;
        }

        @NonNull
        @Override
        public AbandonedListFragment.BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new AbandonedListFragment.BookHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull AbandonedListFragment.BookHolder holder, int position) {
            Book book = mBooks.get(position);
            holder.bind(book, position);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        public void setBooks(List<Book> books) {
            mBooks = books;
        }
    }
}
