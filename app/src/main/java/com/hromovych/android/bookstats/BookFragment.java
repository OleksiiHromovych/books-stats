package com.hromovych.android.bookstats;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hromovych.android.bookstats.database.BookDBSchema;
import com.hromovych.android.bookstats.ui.readNow.BookNowFragment;
import com.hromovych.android.bookstats.ui.readYet.BookYetFragment;
import com.hromovych.android.bookstats.ui.wantRead.WantBookFragment;

import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class BookFragment extends Fragment {

    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_BOOK_LAYOUT = "book_layout";

    protected Book mBook;
    protected Spinner mBookStatusSpinner;
    private EditText mBookNameField;
    private EditText mBookAuthorField;
    private EditText mBookPagesField;
    private AutoCompleteTextView mBookCategoryField;
    private int book_layout;
    private Callbacks mCallbacks;

    public static BookFragment newInstance(UUID uuid) {

        BookFragment fragment = new BookFragment();
        fragment.setArguments(getInstanceBundle(uuid, R.layout.fragment_book));
        return fragment;
    }

    public static Bundle getInstanceBundle(UUID uuid, int layout) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_ID, uuid);
        args.putInt(ARG_BOOK_LAYOUT, layout);

        return args;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID bookId = (UUID) getArguments().getSerializable(ARG_BOOK_ID);
        book_layout = getArguments().getInt(ARG_BOOK_LAYOUT);


        mBook = BookLab.get(getActivity()).getBook(bookId);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        updateBook();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;

    }

    protected void updateBook() {
        BookLab.get(getActivity()).updateBook(mBook);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(book_layout, container, false);

        mBookNameField = v.findViewById(R.id.book_name);
        mBookNameField.setText(mBook.getBookName());
        mBookNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBook.setBookName(s.toString());
                updateBook();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBookAuthorField = v.findViewById(R.id.book_author);
        mBookAuthorField.setText(mBook.getAuthor());
        mBookAuthorField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBook.setAuthor(s.toString());
                updateBook();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBookPagesField = v.findViewById(R.id.book_pages);
        mBookPagesField.setText("" + mBook.getPages());
        mBookPagesField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBook.setPages(s.length() == 0 ? 0 : Integer.parseInt(s.toString()));
                updateBook();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBookCategoryField = v.findViewById(R.id.book_category);
        mBookCategoryField.setText(mBook.getCategory());
        mBookCategoryField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBook.setCategory(s.toString());
                updateBook();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        List<String> items = BookLab.get(getActivity())
                .getColumnItems(BookDBSchema.BookTable.Cols.CATEGORY);

        mBookCategoryField.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, items));

        mBookStatusSpinner = v.findViewById(R.id.book_status);
//        ArrayAdapter adapter = (ArrayAdapter) mBookStatusSpinner.getAdapter();
//        mBookStatusSpinner.setSelection(adapter.getPosition(mBook.getStatus()));
        mBookStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] choose = getResources().getStringArray(R.array.spinner_list);
                if (mBook.getStatus() == null) {
                    mBook.setStatus(choose[position]);
                    return;
                }
                if (mBook.getStatus().equals(choose[position])) {
                    return;
                }
                mBook.setStatus(choose[position]);
                int[] choose_id = {BookYetFragment.BOOK_FRAGMENT_ID,
                        BookNowFragment.BOOK_FRAGMENT_ID,
                        WantBookFragment.BOOK_FRAGMENT_ID};
                mCallbacks.changeFragmentByStatus(choose_id[position]);
                updateBook();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_book, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_book:
                BookLab.get(getActivity()).deleteBook(mBook);
                getActivity().setResult(RESULT_OK);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    interface Callbacks {
        void changeFragmentByStatus(Integer fragment_id);
    }

}
