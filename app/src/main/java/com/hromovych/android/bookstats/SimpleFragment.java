package com.hromovych.android.bookstats;

import androidx.fragment.app.Fragment;

import com.hromovych.android.bookstats.database.ValueConvector;


public class SimpleFragment extends Fragment {

    protected String getStatusConstant(String s) {
        return ValueConvector.ToConstant.toStatusConstant(getContext(), s);
    }

    protected String getPriorityConstant(String s) {
        return ValueConvector.ToConstant.toPriorityConstant(getContext(), s);
    }

    protected String getTypeConstant(String s) {
        return ValueConvector.ToConstant.toPriorityConstant(getContext(), s);
    }
}