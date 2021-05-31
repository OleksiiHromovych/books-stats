package com.hromovych.android.bookstats.ui.abandoned;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookLab;
import com.hromovych.android.bookstats.helpersItems.Book;
import com.hromovych.android.bookstats.helpersItems.DateHelper;
import com.hromovych.android.bookstats.helpersItems.Holders;
import com.hromovych.android.bookstats.helpersItems.Labels;
import com.hromovych.android.bookstats.helpersItems.SimpleFragment;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class AbandonedListFragment extends SimpleFragment {

    private RecyclerView mRecyclerView;
    private BookAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_books_list, container, false);

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_abandoned);

        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

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

    private final ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(R.color.backgroundFont)
                            .addSwipeLeftActionIcon(R.drawable.ic_menu_del)
                            .addSwipeRightActionIcon(R.drawable.ic_read_now)
                            .addSwipeLeftLabel(getString(R.string.delete_book))
                            .addSwipeRightLabel(getString(R.string.title_read_now))
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                private boolean isBookDeleted = false;

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    final BookLab bookLab = BookLab.get(getActivity());
                    List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getString(R.string.title_abandoned)));

                    final Book book = books.get(viewHolder.getAdapterPosition());
                    final Book oldBook = bookLab.getBook(book.getId());
                    if (direction == ItemTouchHelper.LEFT) {  //delete
                        isBookDeleted = true;
                        bookLab.deleteBook(book);

                    } else if (direction == ItemTouchHelper.RIGHT) { //to read now
                        isBookDeleted = false;
                        book.setStatus(getStatusConstant(getString(R.string.title_read_now)));
                        book.setStartDate(DateHelper.today);
                        book.setEndDate(DateHelper.undefinedDate);
                        book.setLabel(Labels.NONE_VALUE);
                        bookLab.updateBook(book);
                    }
                    updateUi();

                    displaySnackbar(getString(R.string.swipe_element_text), getString(R.string.undo_title), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isBookDeleted) {
                                bookLab.addBook(oldBook);
                            } else {
                                bookLab.updateBook(oldBook);
                            }
                            updateUi();
                        }
                    });
                }
            };

    public void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT)
                .setAction(actionName, action);

        View v = snack.getView();
        v.setBackgroundColor(getResources().getColor(R.color.backgroundItem));

        ((TextView) v.findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }


    private static class BookHolder extends Holders.BookHolder {

        private final TextView startDate;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_book, parent, false));

            startDate = itemView.findViewById(R.id.details_up);
            startDate.setVisibility(View.VISIBLE);
        }

        public void bind(Book book, int pos) {
            super.bind(book, pos);

            if (!mBook.getStartDate().equals(DateHelper.unknownDate)
                    && !mBook.getStartDate().equals(DateHelper.undefinedDate)) {
                startDate.setVisibility(View.VISIBLE);
                startDate.setText(String.format("+ %s", DateFormat.format("MMM dd, yyyy", mBook.getStartDate())));
            } else
                startDate.setVisibility(View.INVISIBLE);
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
            return new BookHolder(layoutInflater, parent);
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
