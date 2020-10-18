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
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ToTextFragment extends Fragment {

    public static final String BUNDLE_FIELDS_LIST_KEY = "bundle fields list key";

    private ImageButton sendDataBtn;
    private ImageButton copyDataBtn;

    private CheckBox yetCheckBox;
    private CheckBox nowCheckBox;
    private CheckBox wantCheckBox;

    private LinearLayout criteriaLayout;

    public TextView fieldListTextView;
    private Button fieldChangeButton;

    private List<String> popupMenuElements;
    private ArrayList<String> fieldsList;

    public static ToTextFragment newInstance() {

        return new ToTextFragment();
    }

    public static ToTextFragment newInstance(ArrayList<String> strings) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(BUNDLE_FIELDS_LIST_KEY, strings);
        ToTextFragment fragment = new ToTextFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_export_text, container, false);

        if (getArguments() != null)
            fieldsList = getArguments().getStringArrayList(BUNDLE_FIELDS_LIST_KEY);
        if (fieldsList == null)
            fieldsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.export_fields_list)).subList(0, 3));

        sendDataBtn = v.findViewById(R.id.export_send_btn);
        copyDataBtn = v.findViewById(R.id.export_copy_to_clipboard_btn);

        sendDataBtn.setOnClickListener(exportDataOnCLickListener);
        copyDataBtn.setOnClickListener(exportDataOnCLickListener);

        yetCheckBox = v.findViewById(R.id.yet_checkbox);
        nowCheckBox = v.findViewById(R.id.now_checkbox);
        wantCheckBox = v.findViewById(R.id.want_checkbox);

        criteriaLayout = v.findViewById(R.id.export_criteria_layout);
        Button addCriteriaButton = v.findViewById(R.id.export_add_criteria_btn);

        addCriteriaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        fieldListTextView = v.findViewById(R.id.exported_fields_textView);
        fieldListTextView.setText(join(" - ", fieldsList));
        fieldChangeButton = v.findViewById(R.id.export_fields_change_btn);

        fieldChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.export_activity_container,
                                ExportedFieldsFragment.newInstance(fieldsList))
                        .commit();
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

    private int getDateFormatString(Date date) {
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

        for (String s : getCheckedCheckboxesTitle()) {
            data.append(s).append("\n");

            Map<String, String> whereClause = getWhereClauseArgsMap(s);
            for (Book book : bookLab.getBooksByWhereArgsMap(whereClause))
                data.append(bookToText(book));

            data.append("\n");
        }

        return data.toString();
    }

    private Map<String, String> getWhereClauseArgsMap(String s) {
        Map<String, String> map = getCriteriaValues();
        Map<String, String> whereClause = new LinkedHashMap<>();
        whereClause.put(BookDBSchema.BookTable.Cols.STATUS + " = ?",
                ValueConvector.ToConstant.toStatusConstant(getContext(), s));
        for (String key : map.keySet()) {
            if (key.equals(getString(R.string.book_end_date_title))) {
                whereClause.put(BookDBSchema.BookTable.Cols.END_DATE + " BETWEEN ?",
                        Long.toString(getSecondFromDate(Integer.parseInt(map.get(key)), 0, 1)));
                whereClause.put("?",
                        Long.toString(getSecondFromDate(Integer.parseInt(map.get(key)), 11, 31)));
            } else if (key.equals(getString(R.string.book_category_title))) {
                whereClause.put(BookDBSchema.BookTable.Cols.CATEGORY + " = ?", map.get(key));
            }
        }
        return whereClause;
    }

    private List<String> getCheckedCheckboxesTitle() {
        List<String> checkedCheckboxesText = new ArrayList<>();
        for (CheckBox cb : new CheckBox[]{yetCheckBox, nowCheckBox, wantCheckBox})
            if (cb.isChecked()) {
                checkedCheckboxesText.add(cb.getText().toString());
            }
        return checkedCheckboxesText;
    }

    private Map<String, String> getCriteriaValues() {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < criteriaLayout.getChildCount(); i++) {
            View v = criteriaLayout.getChildAt(i);
            TextView textView = v.findViewById(R.id.export_criteria_item_textView);
            EditText editText = v.findViewById(R.id.export_criteria_item_editText);
            map.put(textView.getText().toString(), editText.getText().toString());
        }
        return map;
    }

    private long getSecondFromDate(int year, int month, int day) {
        return new GregorianCalendar(year, month, day).
                getTimeInMillis();
    }

    private String bookToText(Book book) {
        return book.getAuthor() + " - " + book.getBookName() + "  " + getDateFormatString(book.getEndDate()) + "\n";
//        return getContext().getString(R.string.export_to_clipboard, book.getAuthor(), book.getBookName(), book.getEndDate().getYear());
    }

    public String join(String joinText, Iterable<String> iterable) {
        StringBuilder result = new StringBuilder();
        for (String s : iterable) {
            result.append(s).append(joinText);
        }
        if (result.length() > 0)
            result.delete(result.length() - joinText.length(), result.length());
        return result.toString();
    }

}
