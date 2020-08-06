package com.hromovych.android.bookstats.ui.wantRead;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.hromovych.android.bookstats.BookFragment;
import com.hromovych.android.bookstats.DateHelper;
import com.hromovych.android.bookstats.R;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class WantBookFragment extends BookFragment {

    public static final int BOOK_FRAGMENT_ID = R.id.want_read;

    public static WantBookFragment newInstance(UUID uuid) {
        WantBookFragment fragment = new WantBookFragment();
        fragment.setArguments(getInstanceBundle(uuid, R.layout.fragment_want_book));
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        mBookStatusSpinner.setSelection(2);

        mBook.setStartDate(DateHelper.undefinedDate);
        mBook.setEndDate(DateHelper.undefinedDate);

        Spinner prioritySpinner = v.findViewById(R.id.book_priority_spinner);
        List<String> choose = Arrays.asList(getResources().getStringArray(
                R.array.priority_spinner_list));

        if (!choose.contains(mBook.getType()))
            mBook.setType(choose.get(1));

        prioritySpinner.setSelection(choose.indexOf(mBook.getType()));
        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBook.setType(getResources().getStringArray(
                        R.array.priority_spinner_list)[position]);
                updateBook();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return v;
    }

}

