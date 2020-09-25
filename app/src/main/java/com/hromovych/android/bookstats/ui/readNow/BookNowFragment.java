package com.hromovych.android.bookstats.ui.readNow;

import android.app.Activity;
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
        mBook.setEndDate(DateHelper.undefinedDate);
        if (mBook.getStartDate().equals(DateHelper.undefinedDate))
            mBook.setStartDate(DateHelper.today);

        mUnknownDate = DateHelper.unknownDate;

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

        Spinner typeSpinner = v.findViewById(R.id.book_type_spinner);
        List<String> choose = Arrays.asList(getResources().getStringArray(
                R.array.type_spinner_list));

        if (!choose.contains(ValueConvector.FromConstant.fromTypeConstant(getContext(),
                mBook.getType())))
            mBook.setType(ValueConvector.ToConstant.toTypeConstant(getContext(),
                    choose.get(0)));

        typeSpinner.setSelection(choose.indexOf(ValueConvector.FromConstant.fromTypeConstant(getContext(),
                mBook.getType())));
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBook.setType(ValueConvector.ToConstant.toTypeConstant(getContext(),
                        getResources().getStringArray(
                        R.array.type_spinner_list)[position]));
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
        return ValueConvector.Constants.READ_NOW;
    }


    private void updateDate() {
        if (mBook.getStartDate().equals(mUnknownDate))
            mBookStartDate.setText("");
        else mBookStartDate.setText(new SimpleDateFormat("E dd:MM:yyyy", Locale.getDefault())
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

