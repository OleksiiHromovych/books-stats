package com.hromovych.android.bookstats.HelpersItems;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookDBSchema;
import com.hromovych.android.bookstats.database.ValueConvector;
import com.hromovych.android.bookstats.ui.readNow.BookNowFragment;
import com.hromovych.android.bookstats.ui.readYet.BookYetFragment;
import com.hromovych.android.bookstats.ui.wantRead.WantBookFragment;

import java.util.List;
import java.util.UUID;


public class BookFragment extends Fragment {

    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_BOOK_LAYOUT = "book_layout";

    protected Book mBook;
    protected Spinner mBookStatusSpinner;
    private EditText mBookNameField;
    private EditText mBookAuthorField;
    private EditText mBookPagesField;
    private AutoCompleteTextView mBookCategoryField;
    private TextView mBookDescriptionField;
    protected Spinner mBookLabelSpinner;
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
                if (s.length() < 50) {
                    mBook.setBookName(s.toString());
                    updateBook();
                } else {
                    Toast.makeText(getContext(), "Error: Book name so long",
                            Toast.LENGTH_SHORT).show();
                    mBookNameField.setText(mBook.getBookName());
                }
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
                if (s.length() < 50) {
                    mBook.setAuthor(s.toString());
                    updateBook();
                } else {
                    Toast.makeText(getContext(), "Error: Author name so long",
                            Toast.LENGTH_SHORT).show();
                    mBookAuthorField.setText(mBook.getAuthor());
                }
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
                int pages = s.length() == 0 ? 0 : Integer.parseInt(s.toString());
                if (pages < 10000) {
                    mBook.setPages(pages);
                    updateBook();
                }else{
                    Toast.makeText(getContext(), "Error: " + pages + " is so big", Toast.LENGTH_SHORT).show();
                    mBookPagesField.setText("" + mBook.getPages());
                }
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
                if (s.length() < 25) {
                    mBook.setCategory(s.toString());
                    updateBook();
                } else {
                    Toast.makeText(getContext(), "Error: Category length so long",
                            Toast.LENGTH_SHORT).show();
                    mBookCategoryField.setText(mBook.getCategory());
                }
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
        mBookStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] choose = getResources().getStringArray(R.array.status_spinner_list);
                if (mBook.getStatus() == null) {
                    mBook.setStatus(ValueConvector.ToConstant.toStatusConstant(getContext(),
                            choose[position]));
                    return;
                }
                if (mBook.getStatus().equals(ValueConvector.ToConstant.toStatusConstant(getContext(),
                        choose[position]))) {
                    return;
                }
                mBook.setStatus(ValueConvector.ToConstant.toStatusConstant(getContext(),
                        choose[position]));
                int[] choose_id = {BookYetFragment.BOOK_FRAGMENT_ID,
                        BookNowFragment.BOOK_FRAGMENT_ID,
                        WantBookFragment.BOOK_FRAGMENT_ID};
                mCallbacks.changeFragmentByStatus(choose_id[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBookDescriptionField = v.findViewById(R.id.book_description);
        mBookDescriptionField.setText(mBook.getDescription());
        mBookDescriptionField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity(), R.style.DatePickerDialog);
                dialog.setContentView(R.layout.dialog_description);
                Button okButton = dialog.findViewById(R.id.dialog_ok_button);
                final EditText edit = dialog.findViewById(R.id.description_edit_text);
                edit.setText(mBook.getDescription());

                edit.setSelection(edit.getText().length());
                edit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBook.setDescription(edit.getText().toString());
                        mBookDescriptionField.setText(edit.getText().toString());
                        updateBook();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        mBookLabelSpinner = (Spinner) v.findViewById(R.id.book_label);

        final Labels labels = new Labels(getContext(),
                getConstant());

        final List<String> list =
                labels.getLabelsNames();

        mBookLabelSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                list
        ));

        mBookLabelSpinner.setSelection(mBook.getLabel() != null && list.contains(labels.getLabelName(mBook.getLabel()))?
                list.indexOf(labels.getLabelName(mBook.getLabel())) : 0);

        mBookLabelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBook.setLabel(labels.getLabelConstantName(list.get(position)));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Confirm book delete !");
                builder.setMessage("You are about to delete all book info. Do you really want to proceed ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BookLab.get(getActivity()).deleteBook(mBook);
                        updateBook();
                        getActivity().finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected String getConstant(){
        return "Error";
    }

    interface Callbacks {
        void changeFragmentByStatus(Integer fragment_id);
    }
}
