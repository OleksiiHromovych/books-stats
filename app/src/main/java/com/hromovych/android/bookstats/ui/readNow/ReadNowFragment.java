package com.hromovych.android.bookstats.ui.readNow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hromovych.android.bookstats.Book;
import com.hromovych.android.bookstats.BookLab;
import com.hromovych.android.bookstats.Callbacks;
import com.hromovych.android.bookstats.DateHelper;
import com.hromovych.android.bookstats.R;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

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

        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        updateUI();

        return view;
    }

    private ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(R.color.backgroundFont)
                            .addSwipeLeftActionIcon(R.drawable.ic_read_yet)
                            .addSwipeRightActionIcon(R.drawable.ic_want_read)
                            .addSwipeLeftLabel(getResources().getString(R.string.title_read_yet))
                            .addSwipeRightLabel(getResources().getString(R.string.title_want_read))
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    final BookLab bookLab = BookLab.get(getActivity());
                    List<Book> books = bookLab.getBooksByStatus(getResources()
                            .getString(R.string.title_read_now));

                    final Book book = books.get(viewHolder.getAdapterPosition());
                    if (direction == ItemTouchHelper.LEFT) {
                        book.setStatus(getResources().getString(R.string.title_read_yet));
                        if (book.getStartDate().equals(DateHelper.undefinedDate))
                            book.setStartDate(DateHelper.today);

                        if (book.getEndDate().equals(DateHelper.undefinedDate))
                            book.setEndDate(DateHelper.today);

                    } else if (direction == ItemTouchHelper.RIGHT) {
                        book.setType(getResources().getStringArray(R.array.priority_spinner_list)[1]);
                        book.setStatus(getResources().getString(R.string.title_want_read));
                        book.setStartDate(DateHelper.undefinedDate);
                        book.setEndDate(DateHelper.undefinedDate);
                    }
                    bookLab.updateBook(book);
                    updateUI();

                    displaySnackbar("Swipe element", "Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            book.setStatus(getResources().getString(R.string.title_read_now));
                            if (book.getStartDate().equals(DateHelper.undefinedDate))
                                book.setStartDate(DateHelper.today);
                            book.setEndDate(DateHelper.undefinedDate);

                            bookLab.updateBook(book);
                            updateUI();
                        }
                    });
                }
            };


    public void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT)
                .setAction(actionName, action);

        View v = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
        params.gravity = Gravity.TOP;
        v.setLayoutParams(params);
        v.setBackgroundColor(getResources().getColor(R.color.backgroundItem));

        ((TextView) v.findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
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
            if (!mBook.getStartDate().equals(DateHelper.unknownDate))
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
