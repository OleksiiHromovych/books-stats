package com.hromovych.android.bookstats.menuOption.Export;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hromovych.android.bookstats.HelpersItems.Book;
import com.hromovych.android.bookstats.HelpersItems.BookLab;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookDBSchema;
import com.hromovych.android.bookstats.database.ValueConvector;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ClipboardFragment extends Fragment {

    private ImageButton sendDataBtn;
    private ImageButton copyDataBtn;

    private CheckBox yetCheckBox;
    private CheckBox nowCheckBox;
    private CheckBox wantCheckBox;

    private Button addCriteriaButton;
    private LinearLayout criteriaLayout;

    private List<String> popupMenuElements;

    public static ClipboardFragment newInstance() {

        return new ClipboardFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_export_clipboard, container, false);

        sendDataBtn = v.findViewById(R.id.export_send_btn);
        copyDataBtn = v.findViewById(R.id.export_copy_to_clipboard_btn);

        sendDataBtn.setOnClickListener(exportDataOnCLickListener);
        copyDataBtn.setOnClickListener(exportDataOnCLickListener);

        yetCheckBox = v.findViewById(R.id.yet_checkbox);
        nowCheckBox = v.findViewById(R.id.now_checkbox);
        wantCheckBox = v.findViewById(R.id.want_checkbox);

        criteriaLayout = v.findViewById(R.id.export_criteria_layout);
        addCriteriaButton = v.findViewById(R.id.export_add_criteria_btn);

        addCriteriaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        popupMenuElements = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.export_criteria_elements)));


        return v;
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);

        for (String s : popupMenuElements)
            popupMenu.getMenu().add(s);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String itemName = item.getTitle().toString();
                if (itemName.equals(getString(R.string.book_end_date_title))) {

                    criteriaLayout.addView(createCriteriaView(item.getTitle().toString(),
                            "Enter Date", InputType.TYPE_CLASS_NUMBER,
                            null));
                } else if (itemName.equals(getString(R.string.book_category_title))) {
                    List<String> items = BookLab.get(getActivity())
                            .getColumnItems(BookDBSchema.BookTable.Cols.CATEGORY);

                    criteriaLayout.addView(createCriteriaView(item.getTitle().toString(),
                            getString(R.string.book_category_hint),
                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,
                            new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_dropdown_item_1line, items)));
                }
                popupMenuElements.remove(itemName);

                return true;
            }
        });
        popupMenu.show();

    }

    private int extractYearFromDate(Date date) {
        return Integer.parseInt(DateFormat.format("yyyy", date).toString());
    }

    private View createCriteriaView(String title, String hint, Integer inputType,
                                    ArrayAdapter<String> arrayAdapter) {
        RelativeLayout layout =
                (RelativeLayout) getLayoutInflater().inflate(R.layout.list_item_export_criteria,
                        criteriaLayout, false);

        TextView view = layout.findViewById(R.id.export_criteria_item_textView);
        view.setText(title);
        AutoCompleteTextView autoCompleteTextView = layout.findViewById(R.id.export_criteria_item_editText);
        autoCompleteTextView.setHint(hint != null ? hint : "Enter criteria");
        autoCompleteTextView.setInputType(inputType != null ? inputType : InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        if (arrayAdapter != null)
            autoCompleteTextView.setAdapter(arrayAdapter);

        return layout;
    }

    View.OnClickListener exportDataOnCLickListener = new View.OnClickListener() {

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
    };


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
            if (cb.isChecked()) {
                if (result.length() > 0)
                    result.append(" OR ");
                result.append(ValueConvector.ToConstant.toStatusConstant(getContext(), cb.getText().toString()));
            }
        return result.toString();
    }

    private String bookToText(Book book) {
        return book.getAuthor() + " - " + book.getBookName() + "  " + (book.getEndDate().getYear() + 1900) + "\n";
//        return getContext().getString(R.string.export_to_clipboard, book.getAuthor(), book.getBookName(), book.getEndDate().getYear());
    }
}
