package com.hromovych.android.bookstats.ui.readYet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hromovych.android.bookstats.HelpersItems.BookFragment;
import com.hromovych.android.bookstats.HelpersItems.DateHelper;
import com.hromovych.android.bookstats.HelpersItems.DatePickerFragment;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.ValueConvector;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BookYetFragment extends BookFragment {

    public static final int BOOK_FRAGMENT_ID = R.id.read_yet;

    private static final int REQUEST_START_DATE = 0;
    private static final int REQUEST_END_DATE = 1;
    private static final String DIALOG_DATE = "DialogDate";

    private Button mBookStartDate;
    private Button mBookEndDate;
    private CheckBox mStartDateCheckBox;
    private Date mUnknownDate;


    public static BookYetFragment newInstance(UUID uuid) {
        BookYetFragment fragment = new BookYetFragment();
        fragment.setArguments(getInstanceBundle(uuid, R.layout.fragment_book_yet));
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        mBookStatusSpinner.setSelection(0);

        if (mBook.getStartDate().equals(DateHelper.undefinedDate))
            mBook.setStartDate(DateHelper.today);

        if (mBook.getEndDate().equals(DateHelper.undefinedDate))
            mBook.setEndDate(DateHelper.today);

        mUnknownDate = DateHelper.unknownDate;

        mStartDateCheckBox = v.findViewById(R.id.book_start_date_checkbox);
        mStartDateCheckBox.setChecked(mBook.getStartDate().equals(mUnknownDate));
        mStartDateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBook.setStartDate(mUnknownDate);
                    mBookStartDate.setEnabled(false);
                } else {
                    mBook.setStartDate(DateHelper.today);
                    mBookStartDate.setEnabled(true);

                }
                updateStartDate();
                updateBook();
            }
        });


        mBookStartDate = v.findViewById(R.id.book_start_date);
        updateStartDate();
        mBookStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mBook.getStartDate());
                dialog.setTargetFragment(BookYetFragment.this, REQUEST_START_DATE);
                dialog.show(getFragmentManager(), DIALOG_DATE);


            }
        });

        mStartDateCheckBox = v.findViewById(R.id.book_end_date_checkbox);
        mStartDateCheckBox.setChecked(mBook.getEndDate().equals(mUnknownDate));
        mStartDateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBook.setEndDate(mUnknownDate);
                    mBookEndDate.setEnabled(false);
                } else {
                    mBook.setEndDate(new Date());
                    mBookEndDate.setEnabled(true);

                }
                updateEndDate();
                updateBook();
            }
        });

        mBookEndDate = v.findViewById(R.id.book_end_date);
        updateEndDate();
        mBookEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mBook.getEndDate());
                dialog.setTargetFragment(BookYetFragment.this, REQUEST_END_DATE);
                dialog.show(getFragmentManager(), DIALOG_DATE);

            }
        });


        Spinner typeSpinner = v.findViewById(R.id.book_type_spinner);
        List<String> choose = Arrays.asList(getResources().getStringArray(
                R.array.type_spinner_list));

        if (!choose.contains(ValueConvector.FromConstant.fromTypeConstant(getContext(),
                mBook.getType())))
            mBook.setType(ValueConvector.ToConstant.toTypeConstant(getContext(), choose.get(0)));

        typeSpinner.setSelection(choose.indexOf(ValueConvector.FromConstant.fromTypeConstant(getContext(),
                mBook.getType())));
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBook.setType(ValueConvector.ToConstant.toTypeConstant(getContext(),
                        getResources().getStringArray(R.array.type_spinner_list)[position]));
                updateBook();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    @Override
    protected String getConstant() {
        return ValueConvector.Constants.READ_YET;
    }

    private void updateStartDate() {
        if (mBook.getStartDate().equals(mUnknownDate))
            mBookStartDate.setText("");
        else mBookStartDate.setText(new SimpleDateFormat("E dd:MM:yyyy", Locale.getDefault())
                .format(mBook.getStartDate()));
    }

    private void updateEndDate() {
        if (mBook.getEndDate().equals(mUnknownDate))
            mBookEndDate.setText("");
        else mBookEndDate.setText(new SimpleDateFormat("E dd:MM:yyyy", Locale.getDefault())
                .format(mBook.getEndDate()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getContext(), R.string.error_title, Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case REQUEST_START_DATE:
                Date lastDate = mBook.getStartDate();
                final Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                date.setHours(lastDate.getHours());
                date.setMinutes(lastDate.getMinutes());
                Date checkEndDate = mBook.getEndDate();
                if (date.compareTo(checkEndDate) > 0
                        && !checkEndDate.equals(DateHelper.unknownDate)
                        && !checkEndDate.equals(DateHelper.undefinedDate)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.date_request_exception_title);
                    builder.setMessage(R.string.date_start_request_exception_text);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBook.setStartDate(date);
                            updateBook();
                            updateStartDate();
                        }
                    });

                    builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.show();
                } else {
                    mBook.setStartDate(date);
                    updateBook();
                    updateStartDate();
                }
                break;

            case REQUEST_END_DATE:
                Date lastEndDate = mBook.getEndDate();
                final Date endDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                endDate.setHours(lastEndDate.getHours());
                endDate.setMinutes(lastEndDate.getMinutes());
                Date checkStartDate = mBook.getStartDate();
                if (endDate.compareTo(checkStartDate) < 0
                        && !checkStartDate.equals(DateHelper.unknownDate)
                        && !checkStartDate.equals(DateHelper.undefinedDate)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.date_request_exception_title);
                    builder.setMessage(R.string.date_end_request_exception_text);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBook.setEndDate(endDate);
                            updateBook();
                            updateEndDate();
                        }
                    });

                    builder.setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.show();
                } else {
                    mBook.setEndDate(endDate);
                    updateBook();
                    updateEndDate();
                }


            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

