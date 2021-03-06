package com.hromovych.android.bookstats.ui.readYet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookDBSchema;
import com.hromovych.android.bookstats.database.BookLab;
import com.hromovych.android.bookstats.database.ValueConvector;
import com.hromovych.android.bookstats.helpersItems.Book;
import com.hromovych.android.bookstats.helpersItems.Callbacks;
import com.hromovych.android.bookstats.helpersItems.DateHelper;
import com.hromovych.android.bookstats.helpersItems.Holders;
import com.hromovych.android.bookstats.helpersItems.Holders.Group;
import com.hromovych.android.bookstats.helpersItems.Holders.GroupViewHolder;
import com.hromovych.android.bookstats.helpersItems.Labels;
import com.hromovych.android.bookstats.helpersItems.PreferencesHelper;
import com.hromovych.android.bookstats.helpersItems.SimpleFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ReadYetFragment extends SimpleFragment {
    private RecyclerView mRecyclerView;
    private GroupAdapter mAdapter;

    private boolean isFullDateFormat;
    private boolean sortByYear;

    private Callbacks mCallbacks;

    private final String status = ValueConvector.Constants.READ_YET;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books_list, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mRecyclerView);


        PreferencesHelper pm = new PreferencesHelper(getContext());
        isFullDateFormat = pm.isFullDateFormat();
        sortByYear = pm.isSortByYear();

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

    private final ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
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
                    ChildViewHolder childViewHolder = (ChildViewHolder) viewHolder;
                    final Book book = books.get(childViewHolder.getParentAdapterPosition())
                            .getChildList().get(childViewHolder.getChildAdapterPosition());
                    final Book oldBook = bookLab.getBook(book.getId());
                    book.setStatus(getStatusConstant(getResources().getString(R.string.title_read_now)));
                    if (book.getStartDate().equals(DateHelper.undefinedDate))
                        book.setStartDate(DateHelper.today);
                    book.setEndDate(DateHelper.undefinedDate);
                    book.setLabel(Labels.NONE_VALUE);
                    bookLab.updateBook(book);
                    updateUI();

                    final NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    final int id = navController.getCurrentDestination().getId();

                    displaySnackbar(R.string.swipe_element_text, R.string.undo_title, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bookLab.updateBook(oldBook);

                            navController.navigate(id);
                        }
                    });

                }
            };

    private void displaySnackbar(int text, int actionName, View.OnClickListener action) {
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
        } else {
            mAdapter.setParentList(groups, true);
            mAdapter.notifyParentDataSetChanged(true);

        }
        int count = bookLab.getBooksByStatus(getStatusConstant(getString(R.string.title_read_yet))).size();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(
                getResources().getQuantityString(R.plurals.fragment_count_books_subtitile,
                        count, count));
    }

    private List<Group> getGroups(BookLab bookLab) {
        List<Group> groups = new ArrayList<>();

        Boolean[] expendedStatus = getExpendedGroupArray(bookLab);

        if (sortByYear) {
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
                            booksDate.add(book);
                            continue;
                        }
                        group = new Group(getString(R.string.unknown_date), booksDate,
                                expendedStatus[groups.size()]); //it give next expended group status
                    } else
                        group = new Group(Integer.toString(lastDate + 1900), booksDate,
                                expendedStatus[groups.size()]);
                    booksDate = new ArrayList<>();
                    lastDate = date;
                    groups.add(group);
                }
                booksDate.add(book);
            }
            if (!booksDate.isEmpty()) {
                String groupTitle = lastDate == 0 ? getString(R.string.unknown_date) :
                        Integer.toString(lastDate + 1900);

                Group group = new Group(groupTitle, booksDate,
                        expendedStatus[groups.size()]);
                groups.add(group);
            }

        } else {
            List<Book> books = bookLab.getBooksByStatus(status,
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
                            booksCategory.add(book);
                            continue;
                        }
                        group = new Group(getString(R.string.without_category_book), booksCategory,
                                expendedStatus[groups.size()]);
                    } else
                        group = new Group(lastCategory, booksCategory, expendedStatus[groups.size()]);
                    booksCategory = new ArrayList<>();
                    groups.add(group);
                    lastCategory = category;
                }
                booksCategory.add(book);

            }
            if (!booksCategory.isEmpty()) {
                if (lastCategory.isEmpty())
                    lastCategory = getString(R.string.without_category_book);

                Group group = new Group(lastCategory, booksCategory, expendedStatus[groups.size()]);
                groups.add(group);
            }

        }
        return groups;
    }

    private Boolean[] getExpendedGroupArray(BookLab bookLab) {
        PreferencesHelper helper = new PreferencesHelper(getContext());

        Boolean[] expendedStatus = helper.getExpendedYetArray();
        List<String> titles = sortByYear ? bookLab.getColumnItems(BookDBSchema.BookTable.Cols.START_DATE)
                : bookLab.getColumnItems(BookDBSchema.BookTable.Cols.CATEGORY);

        if (titles.size() != expendedStatus.length) {
            expendedStatus = new Boolean[titles.size()];
            Arrays.fill(expendedStatus, false);
            helper.putExpendedYetArray(expendedStatus);
        }
        return expendedStatus;
    }

    public class BookViewHolder extends Holders.BookHolder {
        private final TextView startDate;
        private final TextView endDate;

        private final int itemColor = getResources().getColor(R.color.bookPaperDark);

        public BookViewHolder(View itemView) {
            super(itemView);
            itemView.setBackgroundColor(itemColor);

            startDate = itemView.findViewById(R.id.details_up);
            endDate = itemView.findViewById(R.id.details_down);
            startDate.setVisibility(View.VISIBLE);
            endDate.setVisibility(View.VISIBLE);
        }

        public void bind(Book book, int childPosition) {
            super.bind(book, childPosition);

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
                        startDate.setText("<1");
                }
                endDate.setVisibility(View.GONE);

            }
            String label = mBook.getLabel();
            if (label != null && !label.equals(Labels.NONE_VALUE)) {
                Labels labels = new Labels(getContext(), status);

                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setStroke(getResources().getDimensionPixelOffset(R.dimen.label_stroke_size),
                        labels.getLabelColor(label));
                shape.setColor(itemColor);
                itemView.setBackground(shape);
            } else {
                itemView.setBackgroundColor(itemColor);
            }

        }

        @Override
        public void onClick(View v) {
            mCallbacks.onBookSelected(mBook);
        }
    }

    public class GroupAdapter extends ExpandableRecyclerAdapter<Group, Book, GroupViewHolder, BookViewHolder> {
        private final LayoutInflater mLayoutInflater;

        public GroupAdapter(Context context, @NonNull List<Group> parentList) {
            super(parentList);
            mLayoutInflater = LayoutInflater.from(context);

        }

        @Override
        public void setParentList(@NonNull List<Group> parentList, boolean preserveExpansionState) {
            super.setParentList(parentList, preserveExpansionState);

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
        public void onBindParentViewHolder(@NonNull GroupViewHolder parentViewHolder, final int parentPosition, @NonNull Group parent) {
            parentViewHolder.bind(parent);
            parentViewHolder.setOnClickListener(v -> {
                PreferencesHelper preferencesHelper = new PreferencesHelper(getContext());
                Boolean[] expendedArray = preferencesHelper.getExpendedYetArray();
                Log.d("TAG", "onBindParentViewHolder: " + parentPosition + " " + expendedArray.length);
                expendedArray[parentPosition] = !expendedArray[parentPosition];
                preferencesHelper.putExpendedYetArray(expendedArray);
            });
        }

        @Override
        public void onBindChildViewHolder(@NonNull BookViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Book child) {
            childViewHolder.bind(child, childPosition);
        }
    }
}
