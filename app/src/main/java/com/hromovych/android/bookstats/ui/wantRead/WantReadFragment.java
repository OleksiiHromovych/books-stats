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
import com.hromovych.android.bookstats.helpersItems.Labels;
import com.hromovych.android.bookstats.helpersItems.SimpleFragment;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class WantReadFragment extends SimpleFragment {


    private RecyclerView mRecyclerView;
    private GroupAdapter mAdapter;
    private Callbacks mCallbacks;

    private final String status = ValueConvector.Constants.WANT_READ;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books_list, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new ItemTouchHelper(mItemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        updateUI();


        return view;
    }


    private final ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
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

    public void displaySnackbar(int text, int actionName, View.OnClickListener action) {
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

//        if (sortByDate) {
//            List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getResources()
//                            .getString(R.string.title_want_read)),
//                    BookDBSchema.BookTable.Cols.AUTHOR);
//            groups.add(new Holders.Group(getString(R.string.button_ok), books));    TODO

//        } else {
        {
            List<Book> books = bookLab.getBooksByStatus(status,
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
                        group = new Holders.Group(getString(R.string.without_category_book), booksCategory);
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

    private class BookViewHolder extends Holders.BookHolder implements View.OnClickListener {

        private final TextView priority;
        private final int itemColor = getResources().getColor(R.color.bookPaperLight);

        public BookViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_book, parent, false));
            itemView.setBackgroundColor(itemColor);
            priority = itemView.findViewById(R.id.details_up);
            priority.setVisibility(View.VISIBLE);
        }

        public void bind(Book book, int pos) {
            super.bind(book, pos);
            priority.setText(mBook.getType());

            String label = mBook.getLabel();
            if (label != null && !label.equals(Labels.NONE_VALUE)) {
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setStroke(getResources().getDimensionPixelOffset(R.dimen.label_stroke_size),
                        new Labels(getContext(), status).getLabelColor(label));
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
    public class GroupAdapter extends ExpandableRecyclerAdapter<Holders.Group, Book, Holders.GroupViewHolder, BookViewHolder> {
        private final LayoutInflater mLayoutInflater;

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
