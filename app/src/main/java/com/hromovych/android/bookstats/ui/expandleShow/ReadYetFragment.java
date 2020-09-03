package com.hromovych.android.bookstats.ui.expandleShow;

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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.google.android.material.snackbar.Snackbar;
import com.hromovych.android.bookstats.Book;
import com.hromovych.android.bookstats.BookLab;
import com.hromovych.android.bookstats.Callbacks;
import com.hromovych.android.bookstats.DateHelper;
import com.hromovych.android.bookstats.MainActivity;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.SimpleFragment;
import com.hromovych.android.bookstats.database.BookDBSchema;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ReadYetFragment extends SimpleFragment {
    private RecyclerView mRecyclerView;
    private GroupAdapter mAdapter;

    private boolean showDate;
    private boolean sortByDate;
    private static final String BOOK_CATEGORY_TEXT = "book_category_text";
    private static final int BOOK_VIEWTYPE = 0;
    private static final int CATEGORY_VIEWTYPE = 1;

    private Callbacks mCallbacks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_yet, container, false);

        mRecyclerView = view.findViewById(R.id.read_yet_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mRecyclerView);


        showDate = getActivity().getSharedPreferences(MainActivity.GET_SHARED_PREFERENCES,
                Context.MODE_PRIVATE).getBoolean(MainActivity.SHOW_DATE_PREFERENCES, true);
        sortByDate = getActivity().getSharedPreferences(MainActivity.GET_SHARED_PREFERENCES,
                Context.MODE_PRIVATE).getBoolean(MainActivity.SORT_BY_DATE, true);
        updateUI();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
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
                    if (!(viewHolder instanceof BookViewHolder))
                        return 0;
                    return super.getMovementFlags(recyclerView, viewHolder);
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    final BookLab bookLab = BookLab.get(getActivity());
                    List<Group> books = getGroups(bookLab);
                    final Group book = books.get(viewHolder.getAdapterPosition());
//                    final Book oldBook = bookLab.getBook(book.getId());
//                    book.setStatus(getStatusConstant(getResources().getString(R.string.title_read_now)));
//                    if (book.getStartDate().equals(DateHelper.undefinedDate))
//                        book.setStartDate(DateHelper.today);
//                    book.setEndDate(DateHelper.undefinedDate);
//                    bookLab.updateBook(book);
//                    updateUI();

                    displaySnackbar("Swipe element " + viewHolder.getAdapterPosition(), "Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            bookLab.updateBook(oldBook);
//                            updateUI();
                        }
                    });

                }
            };

    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
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


    public void updateUI() {
        BookLab bookLab = BookLab.get(getActivity());
        List<Group> groups = getGroups(bookLab);

        if (mAdapter == null) {
            mAdapter = new GroupAdapter(getContext(), groups);
            mRecyclerView.setAdapter(mAdapter);
//        } else {
//            mAdapter.setBooks(books);
//            mAdapter.notifyParentDataSetChanged(true);

        }
    }

    private List<Book> getBooks(BookLab bookLab) {
        if (sortByDate) {

            List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getResources()
                            .getString(R.string.title_read_yet)),
                    BookDBSchema.BookTable.Cols.END_DATE + " , " +
                            BookDBSchema.BookTable.Cols.START_DATE);

            return books;
//            List<Book> booksDate = new ArrayList<>();
//            int lastDate = 0;
//            for (Book book : books) {
//                int date = book.getEndDate().getYear();
//                if (date != lastDate &&
//                        date != DateHelper.undefinedDate.getYear() &&
//                        date != DateHelper.unknownDate.getYear()) {
//                    lastDate = date;
//                    Book bookDate = new Book();
//                    bookDate.setEndDate(new GregorianCalendar(date + 1900, 0, 1).
//                            getTime());
//                    bookDate.setStatus(Holders.BOOK_DATE_TEXT);
//                    booksDate.add(bookDate);
//                }
//                booksDate.add(book);
//
//            }
//            return booksDate;

        } else {
            List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getResources()
                            .getString(R.string.title_read_yet)),
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
    }

    private List<Group> getGroups(BookLab bookLab) {
        List<Group> groups = new ArrayList<>();

        if (sortByDate) {
            List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getResources()
                            .getString(R.string.title_read_yet)),
                    BookDBSchema.BookTable.Cols.END_DATE + " , " +
                            BookDBSchema.BookTable.Cols.START_DATE);

            List<Book> booksDate = new ArrayList<>();
            int lastDate = 0;
            for (Book book : books) {
                int date = book.getEndDate().getYear();
                if (date != lastDate &&
                        date != DateHelper.undefinedDate.getYear() &&
                        date != DateHelper.unknownDate.getYear()) {
                    Group group;
                    if (lastDate == 0) {
                        if (booksDate.isEmpty()) {
                            lastDate = date;
                            continue;
                        }
                        group = new Group("Unknown date", booksDate);
                    } else
                        group = new Group(Integer.toString(lastDate + 1900), booksDate);
                    booksDate = new ArrayList<>();
                    lastDate = date;
                    groups.add(group);
                }
                booksDate.add(book);
            }
            if (!booksDate.isEmpty()) {
                Group group = new Group(Integer.toString(lastDate + 1900), booksDate);
                groups.add(group);
            }

        } else {
            List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getResources()
                            .getString(R.string.title_read_yet)),
                    BookDBSchema.BookTable.Cols.CATEGORY + " , " +
                            BookDBSchema.BookTable.Cols.END_DATE + " , " +
                            BookDBSchema.BookTable.Cols.START_DATE);


            List<Book> booksCategory = new ArrayList<>();
            String lastCategory = "";
            for (Book book : books) {
                String category = book.getCategory();
                if (category != null && !category.equals(lastCategory)) {
                    Group group;
                    if (lastCategory.isEmpty()) {
                        if (booksCategory.isEmpty()) {
                            lastCategory = category;
                            continue;
                        }
                        group = new Group("Without category", booksCategory);  //#TODO: ru string
                    } else
                        group = new Group(lastCategory, booksCategory);
                    booksCategory = new ArrayList<>();
                    groups.add(group);
                    lastCategory = category;
                }
                booksCategory.add(book);

            }
            if (!booksCategory.isEmpty()) {
                Group group = new Group(lastCategory, booksCategory);
                groups.add(group);
            }

        }
        return groups;
    }

    public class Group implements Parent<Book> {

        private List<Book> groupItems;
        private String title;

        public Group(String title, List<Book> books) {
            groupItems = books;
            this.title = title;
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
            return false;
        }
    }

    public class GroupViewHolder extends ParentViewHolder {

        private TextView mGroupTextView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            mGroupTextView = itemView.findViewById(R.id.group_book_category);
        }

        public void bind(Group groupItem) {
            mGroupTextView.setText(groupItem.getTitle());
        }
    }

    public class BookViewHolder extends ChildViewHolder implements View.OnClickListener {
        private TextView count;
        private TextView bookName;
        private TextView author;
        private TextView pages;
        private TextView startDate;
        private TextView endDate;
        private LinearLayout pageLayout;

        private Book mBook;

        public BookViewHolder(View itemView) {
            super(itemView);
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

    public class GroupAdapter extends ExpandableRecyclerAdapter<Group, Book, GroupViewHolder, BookViewHolder> {
        private LayoutInflater mLayoutInflater;

        public GroupAdapter(Context context, @NonNull List<Group> parentList) {
            super(parentList);
            mLayoutInflater = LayoutInflater.from(context);

        }

        @NonNull
        @Override
        public GroupViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
            View groupView = mLayoutInflater.inflate(R.layout.list_item_group, parentViewGroup,
                    false);
            return new GroupViewHolder(groupView);
        }

        @NonNull
        @Override
        public BookViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
            View groupView = mLayoutInflater.inflate(R.layout.list_item_book, childViewGroup,
                    false);
            return new BookViewHolder(groupView);
        }

        @Override
        public void onBindParentViewHolder(@NonNull GroupViewHolder parentViewHolder, int parentPosition, @NonNull Group parent) {
            parentViewHolder.bind(parent);
        }

        @Override
        public void onBindChildViewHolder(@NonNull BookViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Book child) {
            childViewHolder.bind(child);
        }
    }
}
