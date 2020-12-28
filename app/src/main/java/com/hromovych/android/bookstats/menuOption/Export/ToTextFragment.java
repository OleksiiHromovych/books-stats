package com.hromovych.android.bookstats.menuOption.Export;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hromovych.android.bookstats.HelpersItems.Book;
import com.hromovych.android.bookstats.HelpersItems.DateHelper;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.BookDBSchema;
import com.hromovych.android.bookstats.database.BookLab;
import com.hromovych.android.bookstats.database.ValueConvector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hromovych.android.bookstats.HelpersItems.DateHelper.getYearStringFromDate;

public class ToTextFragment extends ExportToTextBasicFragment {

    public static final String BUNDLE_FIELDS_LIST_KEY = "bundle fields list key";
    public static final String DATE_UNKNOWN_VALUE = "0000";

    private ImageButton sendDataBtn;
    private ImageButton copyDataBtn;

    private CheckBox yetCheckBox;
    private CheckBox nowCheckBox;
    private CheckBox wantCheckBox;

    private LinearLayout criteriaLayout;

    private TextView fieldListTextView;
    private Button fieldChangeButton;
    private TextView checkboxesLabel;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_export_text, container, false);

        if (getArguments() != null)
            fieldsList = getArguments().getStringArrayList(BUNDLE_FIELDS_LIST_KEY);
        if (fieldsList == null)
            fieldsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.export_fields_list)).subList(0, 3));

        initViews(v);

        popupMenuElements = new ArrayList<>(Arrays.asList(getActivity().getResources().getStringArray(R.array.export_criteria_elements)));


        return v;
    }

    protected void initViews(View v) {
        sendDataBtn = v.findViewById(R.id.export_send_btn);
        copyDataBtn = v.findViewById(R.id.export_copy_to_clipboard_btn);

        sendDataBtn.setOnClickListener(exportDataOnCLickListener);
        copyDataBtn.setOnClickListener(exportDataOnCLickListener);

        checkboxesLabel = v.findViewById(R.id.export_text_checkboxes_label);
        yetCheckBox = v.findViewById(R.id.yet_checkbox);
        nowCheckBox = v.findViewById(R.id.now_checkbox);
        wantCheckBox = v.findViewById(R.id.want_checkbox);
        yetCheckBox.setOnClickListener(checkboxesListener);
        nowCheckBox.setOnClickListener(checkboxesListener);
        yetCheckBox.setOnClickListener(checkboxesListener);

        criteriaLayout = v.findViewById(R.id.export_criteria_layout);
        Button addCriteriaButton = v.findViewById(R.id.export_add_criteria_btn);

        addCriteriaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, popupMenuElements);
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
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageButton criteriaHelpButton = v.findViewById(R.id.export_text_criteria_help_button);
        criteriaHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertMessage(getString(R.string.exportCriteriaHelpTitle),
                        getString(R.string.exportCriteriaHelpText));
            }
        });
    }

    public final View.OnClickListener exportDataOnCLickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            try {

                switch (v.getId()) {
                    case R.id.export_send_btn:
                        sendByIntent(getBooksData());
                        break;
                    case R.id.export_copy_to_clipboard_btn:
                        copyToClipboard(getBooksData());
                        break;
                }
            } catch (DescriptionException e) {
                showAlertMessage(e.getMessage(), e.getDescription());
            }

        }
    };

    private void showPopupMenu(View v, final List<String> popupMenuElements) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);

        for (String s : popupMenuElements)
            popupMenu.getMenu().add(s);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final String itemName = item.getTitle().toString();
                View.OnClickListener deleteFromLayoutOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        criteriaLayout.removeView((View) v.getParent());
                        popupMenuElements.add(itemName);
                    }
                };
                if (itemName.equals(getString(R.string.book_end_date_title))) {
                    List<String> items = getYearListFromMilliseconds(BookLab.get(getActivity())
                            .getColumnItems(BookDBSchema.BookTable.Cols.END_DATE));

                    criteriaLayout.addView(createCriteriaView(item.getTitle().toString(),
                            getString(R.string.enter_date_hint), InputType.TYPE_CLASS_NUMBER,
                            new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_dropdown_item_1line, items),
                            deleteFromLayoutOnClickListener));
                } else if (itemName.equals(getString(R.string.book_category_title))) {
                    List<String> items = BookLab.get(getActivity())
                            .getColumnItems(BookDBSchema.BookTable.Cols.CATEGORY);
                    items.add(getString(R.string.without_category_book));

                    criteriaLayout.addView(createCriteriaView(item.getTitle().toString(),
                            getString(R.string.book_category_hint),
                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,
                            new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_dropdown_item_1line, items),
                            deleteFromLayoutOnClickListener));
                }
                popupMenuElements.remove(itemName);

                return true;
            }
        });
        popupMenu.show();

    }

    private List<String> getYearListFromMilliseconds(List<String> list) {
        Set<String> items = new HashSet<>();
        for (String s : list) {
            Date date = new Date(Long.parseLong(s));
            if (date.equals(DateHelper.undefinedDate) || date.equals(DateHelper.unknownDate))
                items.add(DATE_UNKNOWN_VALUE);
            else
                items.add(getYearStringFromDate(date));
        }
        List<String> sortedList = new ArrayList<>(items);
        Collections.sort(sortedList);
        return sortedList;
    }


    private <T> View createCriteriaView(String title, String hint, Integer inputType,
                                        ArrayAdapter<T> arrayAdapter, View.OnClickListener deleteFromLayoutOnClickListener) {
        final ConstraintLayout layout =
                (ConstraintLayout) getLayoutInflater().inflate(R.layout.list_item_export_criteria,
                        criteriaLayout, false);

        TextView view = layout.findViewById(R.id.export_criteria_item_textView);
        view.setText(title);
        AutoCompleteTextView autoCompleteTextView = layout.findViewById(R.id.export_criteria_item_editText);
        autoCompleteTextView.setHint(hint != null ? hint : getString(R.string.enter_criteria_hint));
        autoCompleteTextView.setInputType(inputType != null ? inputType : InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        if (arrayAdapter != null)
            autoCompleteTextView.setAdapter(arrayAdapter);

        ImageButton deleteButton = layout.findViewById(R.id.export_criteria_delete_btn);
        deleteButton.setOnClickListener(deleteFromLayoutOnClickListener);

        return layout;
    }


    private final View.OnClickListener checkboxesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkboxesLabel.setError(null);
        }
    };


    private String getBooksData() throws DescriptionException {
        StringBuilder data = new StringBuilder();
        BookLab bookLab = BookLab.get(getContext());
        int totalBooks = 0;
        for (String s : getCheckedCheckboxesTitle()) {
            data.append(s).append("\n");

            Map<String, String> whereClause = getWhereClauseArgsMap(s);
            List<Book> books = bookLab.getBooksByWhereArgsMap(whereClause);
            totalBooks += books.size();
            for (Book book : books) {
                data.append(bookToText(fieldsList, book, " - "));
            }

            data.append("\n");
        }
        if (totalBooks == 0)
            throw new DescriptionException(getContext(), R.string.empty_result_data_message,
                    R.string.empty_result_data_description);
        else
            Toast.makeText(getContext(),
                    getResources().getQuantityString(R.plurals.totalBooksExportCount, totalBooks, totalBooks),
                    Toast.LENGTH_SHORT).show();
        return data.toString();
    }

    private Map<String, String> getWhereClauseArgsMap(String s) throws DescriptionException {
        Map<String, String> map = getCriteriaValues();
        Map<String, String> whereClause = new LinkedHashMap<>();
        whereClause.put(BookDBSchema.BookTable.Cols.STATUS + " = ?",
                ValueConvector.ToConstant.toStatusConstant(getContext(), s));
        for (String key : map.keySet()) {
            if (key.equals(getString(R.string.book_end_date_title))) {
                if (map.get(key).equals(DATE_UNKNOWN_VALUE)) {
                    whereClause.put(BookDBSchema.BookTable.Cols.END_DATE + " = " +
                            DateHelper.unknownDate.getTime() +
                            " OR ?", Long.toString(DateHelper.undefinedDate.getTime()));
                } else {
                    whereClause.put(BookDBSchema.BookTable.Cols.END_DATE + " BETWEEN ?",
                            Long.toString(DateHelper.getSecondFromDate(Integer.parseInt(map.get(key)), 0, 1)));
                    whereClause.put("?",
                            Long.toString(DateHelper.getSecondFromDate(Integer.parseInt(map.get(key)), 11, 31)));
                }
            } else if (key.equals(getString(R.string.book_category_title))) {
                if (map.get(key).equals(getString(R.string.without_category_book)))
                    whereClause.put(BookDBSchema.BookTable.Cols.CATEGORY + " ISNULL OR " +
                            BookDBSchema.BookTable.Cols.CATEGORY + " is ?", "");
                else
                    whereClause.put(BookDBSchema.BookTable.Cols.CATEGORY + " = ?", map.get(key));
            }
        }
        return whereClause;
    }

    private List<String> getCheckedCheckboxesTitle() throws DescriptionException {
        List<String> checkedCheckboxesText = new ArrayList<>();
        for (CheckBox cb : new CheckBox[]{yetCheckBox, nowCheckBox, wantCheckBox})
            if (cb.isChecked()) {
                checkedCheckboxesText.add(cb.getText().toString());
            }
        if (checkedCheckboxesText.isEmpty()) {
            checkboxesLabel.setError(getString(R.string.ckeckboxes_empty_exception));
            throw new DescriptionException(getString(R.string.valueExceptionTitle), getString(R.string.checkboxes_exception_message));
        }
        return checkedCheckboxesText;
    }

    private Map<String, String> getCriteriaValues() throws DescriptionException {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < criteriaLayout.getChildCount(); i++) {
            View v = criteriaLayout.getChildAt(i);
            TextView textView = v.findViewById(R.id.export_criteria_item_textView);
            AutoCompleteTextView editText = v.findViewById(R.id.export_criteria_item_editText);
            String criteriaText = editText.getText().toString();
            String criteriaTitle = textView.getText().toString();
            if (criteriaText.isEmpty()) {
                editText.setError(getString(R.string.empty_field_exeption_title));
                throw new DescriptionException(getString(R.string.valueExceptionTitle), criteriaTitle + getString(R.string.criteria_title_empty_exception));
            }
            boolean presentInData = false;
            for (int j = 0; j < editText.getAdapter().getCount(); j++)
                if (editText.getAdapter().getItem(j).toString().equals(criteriaText)) {
                    presentInData = true;
                    break;
                }
            if (!presentInData) {
                editText.setError(getString(R.string.incorrect_data_exception_title));
                throw new DescriptionException(getString(R.string.valueExceptionTitle), getString(R.string.criteria_inccorect_value, criteriaText));
            }

            map.put(criteriaTitle, criteriaText);
        }
        return map;
    }

    public static class DescriptionException extends Exception {
        private final String description;

        public DescriptionException(String message, String description) {
            super(message);
            this.description = description;
        }

        public DescriptionException(Context context, int message, int description) {
            super(context.getString(message));
            this.description = context.getString(description);
        }

        public String getDescription() {
            return description;
        }

    }

}
