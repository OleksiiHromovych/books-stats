package com.hromovych.android.bookstats.ui.readYet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hromovych.android.bookstats.Book;
import com.hromovych.android.bookstats.BookLab;
import com.hromovych.android.bookstats.Callbacks;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.UnknownDate;

import java.util.Date;
import java.util.List;

public class ReadYetFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private ReadYetFragment.BookAdapter mAdapter;
    private Callbacks mCallbacks;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_yet, container, false);
        mRecyclerView = view.findViewById(R.id.read_yet_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        updateUI();

        return view;
    }

    private ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT ) {
                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    final ColorDrawable backgroundLeft = new ColorDrawable(getResources()
                            .getColor(R.color.backgroundItem));
                    backgroundLeft.setBounds(0, viewHolder.itemView.getTop(),
                            viewHolder.itemView.getLeft() + (int) (dX),
                            viewHolder.itemView.getBottom());
                    backgroundLeft.draw(c);

                    final ColorDrawable backgroundRight = new ColorDrawable(Color.GREEN);
                    backgroundRight.setBounds(viewHolder.itemView.getRight() + (int) dX, viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());
                    backgroundRight.draw(c);

                    Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(),
                            R.drawable.ic_read_now);

                    int iconHorizontalMargin = icon.getIntrinsicWidth();

                    int top = viewHolder.itemView.getTop() + ((viewHolder.itemView.getBottom() -
                            viewHolder.itemView.getTop()) / 2 - (int) icon.getIntrinsicHeight() / 2);
                    icon.setBounds(viewHolder.itemView.getLeft() + iconHorizontalMargin, top,
                            viewHolder.itemView.getLeft() + iconHorizontalMargin +
                                    icon.getIntrinsicWidth(), top + icon.getIntrinsicHeight());
                    icon.draw(c);

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    BookLab bookLab = BookLab.get(getActivity());
                    List<Book> books = bookLab.getBooksByStatus(getResources()
                            .getString(R.string.title_read_yet));

                    Book book = books.get(viewHolder.getAdapterPosition());
                    book.setStatus(getResources().getString(R.string.title_read_now));
                    bookLab.updateBook(book);
                    updateUI();
                }
            };



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
                .getString(R.string.title_read_yet));
        if (mAdapter == null) {
            mAdapter = new ReadYetFragment.BookAdapter(books);
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
        private TextView endDate;


        private Book mBook;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_book, parent, false));

            count = itemView.findViewById(R.id.book_count);
            bookName = itemView.findViewById(R.id.book_name);
            author = itemView.findViewById(R.id.author);
            pages = itemView.findViewById(R.id.book_pages);
            startDate = itemView.findViewById(R.id.details_up);
            endDate = itemView.findViewById(R.id.details_down);
            startDate.setVisibility(View.VISIBLE);
            endDate.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(this);
        }

        public void bind(Book book, int pos) {
            mBook = book;
            count.setText("" + (pos + 1));
            bookName.setText(mBook.getBookName());
            author.setText(mBook.getAuthor());
            pages.setText("" + mBook.getPages());
            Date unknownDate = new UnknownDate().getUnknownDate();
            if (!mBook.getStartDate().equals(unknownDate))
                startDate.setText(DateFormat.format("MMM dd, yyyy", mBook.getStartDate()));
            if (!mBook.getEndDate().equals(unknownDate))

                endDate.setText(DateFormat.format("MMM dd, yyyy", mBook.getEndDate()));

        }

        @Override
        public void onClick(View v) {
            mCallbacks.onBookSelected(mBook);
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<ReadYetFragment.BookHolder> {

        private List<Book> mBooks;

        public BookAdapter(List<Book> books) {
            mBooks = books;
        }

        @NonNull
        @Override
        public ReadYetFragment.BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ReadYetFragment.BookHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ReadYetFragment.BookHolder holder, int position) {
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
