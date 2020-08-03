package com.hromovych.android.bookstats.ui.readYet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hromovych.android.bookstats.BookFragment;
import com.hromovych.android.bookstats.DatePickerFragment;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.UnknownDate;

import java.text.SimpleDateFormat;
import java.util.Date;
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
        mUnknownDate = new UnknownDate().getUnknownDate();


        mStartDateCheckBox = v.findViewById(R.id.book_start_date_checkbox);
        mStartDateCheckBox.setChecked(mBook.getStartDate().equals(mUnknownDate));
        mStartDateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBook.setStartDate(mUnknownDate);
                    mBookStartDate.setEnabled(false);
                } else {
                    mBook.setStartDate(new Date());
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

        return v;
    }

    private void updateStartDate() {
        if (!mBook.getStartDate().equals(mUnknownDate))
            mBookStartDate.setText(new SimpleDateFormat("E dd:MM:yyyy", Locale.getDefault())
                    .format(mBook.getStartDate()));
    }

    private void updateEndDate() {
        if (!mBook.getEndDate().equals(mUnknownDate))
            mBookEndDate.setText(new SimpleDateFormat("E dd:MM:yyyy", Locale.getDefault())
                    .format(mBook.getEndDate()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case REQUEST_START_DATE:
                Date lastDate = mBook.getStartDate();
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                date.setHours(lastDate.getHours());
                date.setMinutes(lastDate.getMinutes());

                mBook.setStartDate(date);
                updateBook();
                updateStartDate();
                break;
            case REQUEST_END_DATE:
                Date lastEndDate = mBook.getEndDate();
                Date endDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                endDate.setHours(lastEndDate.getHours());
                endDate.setMinutes(lastEndDate.getMinutes());

                mBook.setEndDate(endDate);
                updateBook();
                updateEndDate();
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

