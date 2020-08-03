package com.hromovych.android.bookstats.ui.readNow;

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


public class BookNowFragment extends BookFragment {

    public static final int BOOK_FRAGMENT_ID = R.id.read_now;

    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_DATE = "DialogDate";

    private Button mBookStartDate;
    private CheckBox mCheckBox;
    private Date mUnknownDate;

    public static BookNowFragment newInstance(UUID uuid) {
        BookNowFragment fragment = new BookNowFragment();
        fragment.setArguments(getInstanceBundle(uuid, R.layout.fragment_book_now));
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        mBookStatusSpinner.setSelection(1);
        mUnknownDate = new UnknownDate().getUnknownDate();

        mCheckBox = v.findViewById(R.id.book_start_date_checkbox);
        mCheckBox.setChecked(mBook.getStartDate().equals(mUnknownDate));
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBook.setStartDate(mUnknownDate);
                    mBookStartDate.setEnabled(false);
                } else {
                    mBook.setStartDate(new Date());
                    mBookStartDate.setEnabled(true);

                }
                updateDate();
                updateBook();
            }
        });

        mBookStartDate = v.findViewById(R.id.book_start_date);
        updateDate();
        mBookStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mBook.getStartDate());
                dialog.setTargetFragment(BookNowFragment.this, REQUEST_DATE);
                dialog.show(getFragmentManager(), DIALOG_DATE);


            }
        });

        return v;
    }


    private void updateDate() {
        if (!mBook.getStartDate().equals(mUnknownDate))
            mBookStartDate.setText(new SimpleDateFormat("E dd:MM:yyyy", Locale.getDefault())
                    .format(mBook.getStartDate()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case REQUEST_DATE:
                Date lastDate = mBook.getStartDate();
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                date.setHours(lastDate.getHours());
                date.setMinutes(lastDate.getMinutes());

                mBook.setStartDate(date);
                updateBook();
                updateDate();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

