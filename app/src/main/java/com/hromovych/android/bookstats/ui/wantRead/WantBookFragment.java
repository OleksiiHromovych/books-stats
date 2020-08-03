package com.hromovych.android.bookstats.ui.wantRead;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hromovych.android.bookstats.BookFragment;
import com.hromovych.android.bookstats.R;

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

        return v;
    }

}

