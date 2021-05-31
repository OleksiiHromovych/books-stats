package com.hromovych.android.bookstats.helpersItems;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookLab;
import com.hromovych.android.bookstats.menuOption.Export.ExportToTextBasicFragment;
import com.hromovych.android.bookstats.menuOption.Export.ExportedFieldsDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ExportSelectedBookFragment extends ExportToTextBasicFragment {
    private static final String ARG_BOOK_ID = "book_id";
    private ImageButton sendDataBtn;
    private ImageButton copyDataBtn;

    private TextView fieldListTextView;
    private Button fieldChangeButton;

    private TextView previewTextView;

    private ArrayList<String> fieldsList;
    private Book mBook;

    public static ExportSelectedBookFragment newInstance(UUID uuid) {

        ExportSelectedBookFragment fragment = new ExportSelectedBookFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_ID, uuid);

        fragment.setArguments(args);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_export_selected_book, container, false);

        if (fieldsList == null)
            fieldsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.export_fields_list)).subList(0, 2));
        UUID bookId = (UUID) getArguments().getSerializable(ARG_BOOK_ID);
        mBook = BookLab.get(getActivity()).getBook(bookId);

        initViews(v);

        mCallBack.setFields(fieldsList);

        return v;
    }

    private void initViews(View v) {
        sendDataBtn = v.findViewById(R.id.export_send_btn);
        copyDataBtn = v.findViewById(R.id.export_copy_to_clipboard_btn);

        sendDataBtn.setOnClickListener(exportDataOnCLickListener);
        copyDataBtn.setOnClickListener(exportDataOnCLickListener);

        fieldListTextView = v.findViewById(R.id.exported_fields_textView);
        fieldChangeButton = v.findViewById(R.id.export_fields_change_btn);

        fieldChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new ExportedFieldsDialogFragment(mCallBack,
                        fieldsList);
                dialogFragment.show(getActivity().getSupportFragmentManager(), null);
            }
        });

        previewTextView = v.findViewById(R.id.preview_book_info_view);
    }

    public final View.OnClickListener exportDataOnCLickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.export_send_btn:
                    sendByIntent(getBookData());
                    break;
                case R.id.export_copy_to_clipboard_btn:
                    copyToClipboard(getBookData());
                    break;
            }
        }
    };

    private String getBookData() {
        return bookToText(fieldsList, mBook, " - ");
    }

    private final ExportedFieldsDialogFragment.CallBack mCallBack = new ExportedFieldsDialogFragment.CallBack() {
        @Override
        public void setFields(ArrayList<String> fields) {
            fieldsList = fields;
            fieldListTextView.setText(join(" - ", fieldsList));
            previewTextView.setText(getBookData());
        }
    };
}
