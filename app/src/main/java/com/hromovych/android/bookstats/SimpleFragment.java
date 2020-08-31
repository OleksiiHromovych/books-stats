package com.hromovych.android.bookstats;

import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class SimpleFragment extends Fragment {

    protected String getStatusConstant(String s) {
        if (s.equals(getString(R.string.title_read_now)))
            return Constants.Status.READ_NOW;
        else if (s.equals(getString(R.string.title_read_yet)))
            return Constants.Status.READ_YET;
        else if (s.equals(getString(R.string.title_want_read)))
            return Constants.Status.WANT_READ;
        else {
            Toast.makeText(getContext(), "Error. Unknown status name", Toast.LENGTH_SHORT).show();
            return s;
        }
    }

    protected String getPriorityConstant(String s) {
        if (s.equals(getString(R.string.priority_book_1)))
            return Constants.Priority.HIGH;
        else if (s.equals(getString(R.string.priority_book_2)))
            return Constants.Priority.MEDIUM;
        else if (s.equals(getString(R.string.priority_book_3)))
            return Constants.Priority.LOW;
        else {
            Toast.makeText(getContext(), "Error. Unknown priority name", Toast.LENGTH_SHORT).show();
            return s;
        }
    }

    protected String getTypeConstant(String s) {
        if (s.equals(getString(R.string.type_book_1)))
            return Constants.Type.PAPER;
        else if (s.equals(getString(R.string.type_book_2)))
            return Constants.Type.ELECTRONIC;
        else if (s.equals(getString(R.string.type_book_3)))
            return Constants.Type.AUDIO;
        else {
            Toast.makeText(getContext(), "Error. Unknown status name", Toast.LENGTH_SHORT).show();
            return s;
        }
    }
}