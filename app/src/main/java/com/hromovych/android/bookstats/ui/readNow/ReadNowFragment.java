package com.hromovych.android.bookstats.ui.readNow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
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

import com.google.android.material.snackbar.Snackbar;
import com.hromovych.android.bookstats.HelpersItems.Book;
import com.hromovych.android.bookstats.HelpersItems.Callbacks;
import com.hromovych.android.bookstats.HelpersItems.DateHelper;
import com.hromovych.android.bookstats.HelpersItems.Holders;
import com.hromovych.android.bookstats.HelpersItems.Labels;
import com.hromovych.android.bookstats.HelpersItems.PreferencesHelper;
import com.hromovych.android.bookstats.HelpersItems.SimpleFragment;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookLab;
import com.hromovych.android.bookstats.database.ValueConvector;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ReadNowFragment extends SimpleFragment {


    private RecyclerView mRecyclerView;
    private BookAdapter mAdapter;
    private Callbacks mCallbacks;

    private boolean showDate;

    private final String status = ValueConvector.Constants.READ_NOW;

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

        showDate = new PreferencesHelper(getContext()).isFullDateFormat();

        return view;
    }

    private final ItemTouchHelper.SimpleCallback mItemTouchHelperCallback =
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
                    List<Book> books = bookLab.getBooksByStatus(status);

                    final Book book = books.get(viewHolder.getAdapterPosition());
                    final Book oldBook = bookLab.getBook(book.getId());
                    if (direction == ItemTouchHelper.LEFT) {
                        book.setStatus(getStatusConstant(getString(R.string.title_read_yet)));
                        if (book.getStartDate().equals(DateHelper.undefinedDate))
                            book.setStartDate(DateHelper.today);

                        if (book.getEndDate().equals(DateHelper.undefinedDate))
                            book.setEndDate(DateHelper.today);

                    } else if (direction == ItemTouchHelper.RIGHT) {
                        // Type is priority
                        book.setType(getPriorityConstant(getResources().getStringArray(R.array.priority_spinner_list)[1]));
                        book.setStatus(getStatusConstant(getString(R.string.title_want_read)));
                        book.setStartDate(DateHelper.undefinedDate);
                        book.setEndDate(DateHelper.undefinedDate);
                    }
                    book.setLabel(Labels.NONE_VALUE);
                    bookLab.updateBook(book);
                    updateUI();

                    final NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    final int id = navController.getCurrentDestination().getId();

                    displaySnackbar(getString(R.string.swipe_element_text), getString(R.string.undo_title), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bookLab.updateBook(oldBook);

                            navController.navigate(id);

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
        List<Book> books = bookLab.getBooksByStatus(getStatusConstant(getString(R.string.title_read_now)));

        if (mAdapter == null) {
            mAdapter = new BookAdapter(books);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setBooks(books);
            mAdapter.notifyDataSetChanged();
        }
        int count = bookLab.getBooksByStatus(getStatusConstant(getString(R.string.title_read_now))).size();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(
                getResources().getQuantityString(R.plurals.fragment_count_books_subtitile,
                        count, count));

    }

    private class BookHolder extends Holders.BookHolder
            implements View.OnClickListener {

        private final TextView startDate;
        private final int itemColor = getResources().getColor(R.color.bookPaper);

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_book, parent, false));

            itemView.setBackgroundColor(itemColor);
            startDate = itemView.findViewById(R.id.details_up);
            startDate.setVisibility(View.VISIBLE);
        }

        public void bind(Book book, int pos) {
            super.bind(book, pos);

            if (!mBook.getStartDate().equals(DateHelper.unknownDate)
                    && !mBook.getStartDate().equals(DateHelper.undefinedDate)) {

                if (showDate) {
                    startDate.setText(String.format("+ %s", DateFormat.format("MMM dd, yyyy", mBook.getStartDate()).toString()));

                } else {
                    long days = (DateHelper.today.getTime() - mBook.getStartDate().getTime()) / 1000 / 60 / 60 / 24;
                    startDate.setText(String.valueOf(days));
                }
            }
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

        public void setBooks(List<Book> books) {
            mBooks = books;
        }
    }
}
