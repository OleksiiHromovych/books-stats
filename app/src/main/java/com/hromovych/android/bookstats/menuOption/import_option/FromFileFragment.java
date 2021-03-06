package com.hromovych.android.bookstats.menuOption.import_option;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookLab;

public class FromFileFragment extends Fragment {
    public static final String ARG_PATH = "arg_path";
    private String path;

    private Button import_from_file_btn;

    public static FromFileFragment newInstance(String path) {
        FromFileFragment fragment = new FromFileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString(ARG_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_from_file, container, false);
        import_from_file_btn = v.findViewById(R.id.import_from_file_btn);
        import_from_file_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportFromFile(path);
                getActivity().finish();
            }
        });
        if (path == null)
            import_from_file_btn.setVisibility(View.GONE);

        return v;
    }

    private void exportFromFile(String path) {
        SQLiteDatabase db =
                SQLiteDatabase.openDatabase(path,null, SQLiteDatabase.OPEN_READONLY);
        BookLab.get(getActivity()).extendFromBase(db);
    }
}