package com.hromovych.android.bookstats.ui.readYet;

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
import com.hromovych.android.bookstats.Holders.BaseHolder;
import com.hromovych.android.bookstats.MainActivity;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookDBSchema;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ReadYetFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private ReadYetFragment.BookAdapter mAdapter;
    private Callbacks mCallbacks;

    private boolean showDate;

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
        View view = inflater.inflate(R.layout.fragment_read_yet, container, false);
        mRecyclerView = view.findViewById(R.id.read_yet_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        updateUI();

        showDate = getActivity().getSharedPreferences(MainActivity.GET_SHARED_PREFERENCES,
                Context.MODE_PRIVATE).getBoolean(MainActivity.SHOW_DATE_PREFERENCES, true);

        return view;
    }

    private ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(R.color.backgroundFont)
                            .addActionIcon(R.drawable.ic_read_now)
                            .addSwipeRightLabel(getResources().getString(R.string.title_read_now))
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    if (viewHolder instanceof Holders.CategoryHolder)
                        return 0;
                    return super.getMovementFlags(recyclerView, viewHolder);
                }
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    final BookLab bookLab = BookLab.get(getActivity());
                    List<Book> books = getBooks(bookLab);
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
        List<Book> books = getBooks(bookLab);
        if (mAdapter == null) {
            mAdapter = new ReadYetFragment.BookAdapter(books);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setBooks(books);
            mAdapter.notifyDataSetChanged();

        }
    }

    private List<Book> getBooks(BookLab bookLab) {
        List<Book> books = bookLab.getBooksByStatus(getResources()
                        .getString(R.string.title_read_yet),
                BookDBSchema.BookTable.Cols.CATEGORY + " , " +
                        BookDBSchema.BookTable.Cols.END_DATE + " , " +
                        BookDBSchema.BookTable.Cols.START_DATE);


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
        return booksCategory;
    }

    private class BookHolder extends BaseHolder implements View.OnClickListener {

        private TextView count;
        private TextView bookName;
        private TextView author;
        private TextView pages;
        private TextView startDate;
        private TextView endDate;
        private LinearLayout pageLayout;

        private Book mBook;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_book, parent, false));
            itemView.setBackgroundColor(getResources().getColor(R.color.bookPaperDark));
            count = itemView.findViewById(R.id.book_count);
            bookName = itemView.findViewById(R.id.book_name);
            author = itemView.findViewById(R.id.author);
            pages = itemView.findViewById(R.id.book_pages);
            startDate = itemView.findViewById(R.id.details_up);
            endDate = itemView.findViewById(R.id.details_down);
            pageLayout = itemView.findViewById(R.id.page_layout);
            startDate.setVisibility(View.VISIBLE);
            endDate.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(this);
        }

        public void bind(Book book) {
            mBook = book;
            count.setText("" + 1);
            bookName.setText(mBook.getBookName());
            author.setText(mBook.getAuthor());

            if (mBook.getPages() != 0) {
                pages.setText("" + mBook.getPages());
                pageLayout.setVisibility(View.VISIBLE);
            } else {
                pageLayout.setVisibility(View.GONE);
            }

            if (showDate) {
                endDate.setVisibility(View.VISIBLE);

                if (!mBook.getStartDate().equals(DateHelper.unknownDate)
                        && !mBook.getStartDate().equals(DateHelper.undefinedDate))
                    startDate.setText("+ " + DateFormat.format("MMM dd, yyyy", mBook.getStartDate()));
                if (!mBook.getEndDate().equals(DateHelper.unknownDate)
                        && !mBook.getEndDate().equals(DateHelper.undefinedDate))

                    endDate.setText("- " + DateFormat.format("MMM dd, yyyy", mBook.getEndDate()));

            } else {
                startDate.setText("");
                if (!mBook.getStartDate().equals(DateHelper.unknownDate) &&
                        !mBook.getEndDate().equals(DateHelper.unknownDate)
                        && !mBook.getStartDate().equals(DateHelper.undefinedDate)
                        && !mBook.getEndDate().equals(DateHelper.undefinedDate)) {

                    long days = (mBook.getEndDate().getTime() - mBook.getStartDate().getTime())
                            / 1000 / 60 / 60 / 24;
                    if (days != 0)
                        startDate.setText("" + days);
                }
                endDate.setVisibility(View.GONE);

            }

        }

        @Override
        public void onClick(View v) {
            mCallbacks.onBookSelected(mBook);
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BaseHolder> {

        private List<Book> mBooks;

        public BookAdapter(List<Book> books) {
            mBooks = books;
        }

        @NonNull
        @Override
        public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            if (viewType == CATEGORY_VIEWTYPE) {
                return new Holders.CategoryHolder(layoutInflater, parent);
            } else {
                return new ReadYetFragment.BookHolder(layoutInflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
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
