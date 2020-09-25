package com.hromovych.android.bookstats.ui.wantRead;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.hromovych.android.bookstats.HelpersItems.Book;
import com.hromovych.android.bookstats.HelpersItems.BookLab;
import com.hromovych.android.bookstats.HelpersItems.Callbacks;
import com.hromovych.android.bookstats.HelpersItems.DateHelper;
import com.hromovych.android.bookstats.HelpersItems.Holders;
import com.hromovych.android.bookstats.HelpersItems.Labels;
import com.hromovych.android.bookstats.HelpersItems.SimpleFragment;
import com.hromovych.android.bookstats.MainActivity;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookDBSchema;
import com.hromovych.android.bookstats.database.ValueConvector;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class WantReadFragment extends SimpleFragment {


    private RecyclerView mRecyclerView;
    private GroupAdapter mAdapter;
    private Callbacks mCallbacks;

    private boolean sortByDate;

    private static final String BOOK_CATEGORY_TEXT = "book_category_text";


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

        sortByDate = getActivity().getSharedPreferences(MainActivity.GET_SHARED_PREFERENCES,
                Context.MODE_PRIVATE).getBoolean(MainActivity.SORT_BY_DATE, true);
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
                public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    if (!(viewHolder instanceof BookViewHolder))
                        return 0;
                    return super.getMovementFlags(recyclerView, viewHolder);
                }

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    final BookLab bookLab = BookLab.get(getActivity());
                    List<Holders.Group> books = getGroups(bookLab);
                    ChildViewHolder childViewHolder = (ChildViewHolder) viewHolder;
                    final Book book = books.get(childViewHolder.getParentAdapterPosition())
                            .getChildList().get(childViewHolder.getChildAdapterPosition());

                    final Book oldBook = bookLab.getBook(book.getId());
                    book.setStatus(getStatusConstant(getResources().getString(R.string.title_read_now)));
                    if (book.getStartDate().equals(DateHelper.undefinedDate))
                        book.setStartDate(DateHelper.today);
                    book.setEndDate(DateHelper.undefinedDate);
                    book.setType(getTypeConstant(getResources().getStringArray(R.array.type_spinner_list)[0]));
                    book.setLabel(Labels.NONE_VALUE);
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
        List<Holders.Group> groups = getGroups(bookLab);

        if (mAdapter == null) {
            mAdapter = new GroupAdapter(getContext(), groups);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setParentList(groups, true);
            mAdapter.notifyParentDataSetChanged(true);

        }

        int count = bookLab.getBooksByStatus(getStatusConstant(getString(R.string.title_want_read))).size();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(
                getResources().getQuantityString(R.plurals.fragment_count_books_subtitile,
                        count, count));

    }
    private List<Holders.Group> getGroups(BookLab bookLab) {
        List<Holders.Group> groups = new ArrayList<>();

        if (sortByDate) {
            List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getResources()
                            .getString(R.string.title_want_read)),
                    BookDBSchema.BookTable.Cols.AUTHOR);
            groups.add(new Holders.Group("Date", books));

        } else {
            List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getResources()
                            .getString(R.string.title_want_read)),
                    BookDBSchema.BookTable.Cols.CATEGORY + " , " +
                            BookDBSchema.BookTable.Cols.AUTHOR);

            List<Book> booksCategory = new ArrayList<>();
            String lastCategory = "";
            for (Book book : books) {
                String category = book.getCategory();
                if (category != null && !category.equals(lastCategory)) {
                    Holders.Group group;
                    if (lastCategory.isEmpty()) {
                        if (booksCategory.isEmpty()) {
                            lastCategory = category;
                            booksCategory.add(book);
                            continue;
                        }
                        group = new Holders.Group("Without category", booksCategory);  //#TODO: ru string
                    } else
                        group = new Holders.Group(lastCategory, booksCategory);
                    booksCategory = new ArrayList<>();
                    groups.add(group);
                    lastCategory = category;
                }
                booksCategory.add(book);

            }
            if (!booksCategory.isEmpty()) {
                Holders.Group group = new Holders.Group(lastCategory, booksCategory);
                groups.add(group);
            }

        }
        return groups;
    }

    private List<Book> getBooks(BookLab bookLab) {
        if (sortByDate) {
            List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getResources()
                            .getString(R.string.title_want_read)),
                    BookDBSchema.BookTable.Cols.AUTHOR);
            return books;
        } else {
            List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getResources()
                            .getString(R.string.title_want_read)),
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
            return booksCategory;
        }
    }

    private class BookViewHolder extends Holders.BookHolder implements View.OnClickListener {

        private TextView priority;

        public BookViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_book, parent, false));
            itemView.setBackgroundColor(getResources().getColor(R.color.bookPaperLight));
            priority = itemView.findViewById(R.id.details_up);
            priority.setVisibility(View.VISIBLE);
        }

        public void bind(Book book, int pos) {
            super.bind(book, pos);
            priority.setText(mBook.getType());

            if (mBook.getLabel() != null) {
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setStroke(10, new Labels(getContext(), ValueConvector.Constants.WANT_READ).getLabelColor(
                        mBook.getLabel()));
                shape.setColor(getResources().getColor(R.color.bookPaperLight));
                itemView.setBackground(shape);
            } else {
                itemView.setBackgroundColor(getResources().getColor(R.color.bookPaperLight));
            }

        }

        @Override
        public void onClick(View v) {
            mCallbacks.onBookSelected(mBook);
        }
    }
    public class GroupAdapter extends ExpandableRecyclerAdapter<Holders.Group, Book, Holders.GroupViewHolder, BookViewHolder> {
        private LayoutInflater mLayoutInflater;

        public GroupAdapter(Context context, @NonNull List<Holders.Group> parentList) {
            super(parentList);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public void setParentList(@NonNull List<Holders.Group> parentList, boolean preserveExpansionState) {
            super.setParentList(parentList, preserveExpansionState);

        }

        @NonNull
        @Override
        public Holders.GroupViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
            View groupView = mLayoutInflater.inflate(R.layout.list_item_group, parentViewGroup,
                    false);
            return new Holders.GroupViewHolder(groupView);
        }

        @NonNull
        @Override
        public BookViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
            View groupView = mLayoutInflater.inflate(R.layout.list_item_book, childViewGroup,
                    false);
            return new BookViewHolder(mLayoutInflater, childViewGroup);
        }


        @Override
        public void onBindParentViewHolder(@NonNull Holders.GroupViewHolder parentViewHolder, int parentPosition, @NonNull Holders.Group parent) {
            parentViewHolder.bind(parent);
        }

        @Override
        public void onBindChildViewHolder(@NonNull BookViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Book child) {
            childViewHolder.bind(child, childPosition);
        }
    }


}
