package com.hromovych.android.bookstats.ui.wantRead;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.hromovych.android.bookstats.Holders;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookDBSchema;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class WantReadFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private WantReadFragment.BookAdapter mAdapter;
    private Callbacks mCallbacks;


    private static final String BOOK_CATEGORY_TEXT = "book_category_text";
    private static final int BOOK_VIEWTYPE = 0;
    private static final int CATEGORY_VIEWTYPE = 1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_want_read, container, false);
        mRecyclerView = view.findViewById(R.id.want_read_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        updateUI();

        return view;
    }


    private ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(R.color.backgroundFont)
                            .addActionIcon(R.drawable.ic_read_now)
                            .addSwipeLeftLabel(getResources().getString(R.string.title_read_now))
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
                            .getString(R.string.title_want_read),
                            BookDBSchema.BookTable.Cols.CATEGORY + " , " +
                                    BookDBSchema.BookTable.Cols.AUTHOR);

                    final Book book = books.get(viewHolder.getAdapterPosition());
                    final Book oldBook = bookLab.getBook(book.getId());
                    book.setStatus(getResources().getString(R.string.title_read_now));
                    if (book.getStartDate().equals(DateHelper.undefinedDate))
                        book.setStartDate(DateHelper.today);
                    book.setEndDate(DateHelper.undefinedDate);
                    book.setType(getResources().getStringArray(R.array.type_spinner_list)[0]);
                    bookLab.updateBook(book);
                    updateUI();

                    displaySnackbar("Swipe element", "Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bookLab.updateBook(oldBook);
                            updateUI();
                        }
                    });

                }
            };


    public void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
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
                        .getString(R.string.title_want_read),
                BookDBSchema.BookTable.Cols.CATEGORY + " , " +
                        BookDBSchema.BookTable.Cols.AUTHOR);
        List<Book> booksCategory = new ArrayList<>();
        String lastCategory = "";
        for (Book book : books) {
            String category = book.getCategory();
            if (category != null && !category.equals(lastCategory)) {
                lastCategory = category;
                Book bookCategory = new Book();
                bookCategory.setCategory(book.getCategory());
                bookCategory.setStatus(BOOK_CATEGORY_TEXT);
                booksCategory.add(bookCategory);
            }
            booksCategory.add(book);

        }
        books = booksCategory;
        if (mAdapter == null) {
            mAdapter = new WantReadFragment.BookAdapter(books);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setBooks(books);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class BookHolder extends Holders.BaseHolder {

        private TextView count;
        private TextView bookName;
        private TextView author;
        private TextView pages;
        private TextView priority;
        private LinearLayout pageLayout;
        private Book mBook;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_book, parent, false));
            itemView.setBackgroundColor(getResources().getColor(R.color.bookPaperLight));
            count = itemView.findViewById(R.id.book_count);
            bookName = itemView.findViewById(R.id.book_name);
            author = itemView.findViewById(R.id.author);
            pages = itemView.findViewById(R.id.book_pages);
            priority = itemView.findViewById(R.id.details_up);
            pageLayout = itemView.findViewById(R.id.page_layout);
            priority.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(this);
        }

        public void bind(Book book) {
            mBook = book;
            bookName.setText(mBook.getBookName());
            author.setText(mBook.getAuthor());

            if (mBook.getPages() != 0)
                pages.setText("" + mBook.getPages());
            else
                pageLayout.setVisibility(View.GONE);

            priority.setText(mBook.getType());

        }

        public void bind(Book book, int pos){
            count.setText("" + (pos + 1));
            bind(book);
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onBookSelected(mBook);
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<Holders.BaseHolder> {

        private List<Book> mBooks;

        public BookAdapter(List<Book> books) {
            mBooks = books;
        }

        @NonNull
        @Override
        public Holders.BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            if (viewType == CATEGORY_VIEWTYPE) {
                return new Holders.CategoryHolder(layoutInflater, parent);
            } else {
                return new WantReadFragment.BookHolder(layoutInflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull Holders.BaseHolder holder, int position) {
            holder.bind(mBooks.get(position));
        }


        @Override
        public int getItemViewType(int position) {
            return mBooks.get(position).getStatus().equals(BOOK_CATEGORY_TEXT) ? CATEGORY_VIEWTYPE
                    : BOOK_VIEWTYPE;
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
