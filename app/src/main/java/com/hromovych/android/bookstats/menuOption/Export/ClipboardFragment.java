package com.hromovych.android.bookstats.menuOption.Export;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hromovych.android.bookstats.HelpersItems.Book;
import com.hromovych.android.bookstats.HelpersItems.BookLab;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.ValueConvector;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ClipboardFragment extends Fragment implements View.OnClickListener {

    private ImageButton sendDataBtn;
    private ImageButton copyDataBtn;

    private CheckBox yetCheckBox;
    private CheckBox nowCheckBox;
    private CheckBox wantCheckBox;

    public static ClipboardFragment newInstance() {

        return new ClipboardFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_export_clipboard, container, false);

        sendDataBtn = v.findViewById(R.id.export_send_btn);
        copyDataBtn = v.findViewById(R.id.export_copy_to_clipboard_btn);

        sendDataBtn.setOnClickListener(this);
        copyDataBtn.setOnClickListener(this);

        yetCheckBox = v.findViewById(R.id.yet_checkbox);
        nowCheckBox = v.findViewById(R.id.now_checkbox);
        wantCheckBox = v.findViewById(R.id.want_checkbox);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.export_send_btn:
                break;
            case R.id.export_copy_to_clipboard_btn:
                copyToClipboard();
                break;
        }
    }

    private void copyToClipboard() {
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Export books", getBooksData());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getContext(), "Copy to clipboard", Toast.LENGTH_SHORT).show();
    }

    private String getBooksData() {
        StringBuilder data = new StringBuilder();
        BookLab bookLab = BookLab.get(getContext());
        List<Book> books = new ArrayList<>();
        for (String s : getStatusRequestString().split(" OR "))
            books.addAll(bookLab.getBooksByStatus(s));
        for (Book book : books)
            data.append(bookToText(book));
        return data.toString();
    }

    private String getStatusRequestString() {
        StringBuilder result = new StringBuilder();
        for (CheckBox cb : new CheckBox[]{yetCheckBox, nowCheckBox, wantCheckBox})
            if (cb.isChecked())
                result.append(ValueConvector.ToConstant.toStatusConstant(getContext(), cb.getText().toString())).append(" OR ");
        Toast.makeText(getContext(), result.toString(), Toast.LENGTH_SHORT).show();
        return result.toString();
    }

    private String bookToText(Book book) {
        return getContext().getString(R.string.export_to_clipboard, book.getAuthor(), book.getBookName(), book.getEndDate().getYear());
    }
}
