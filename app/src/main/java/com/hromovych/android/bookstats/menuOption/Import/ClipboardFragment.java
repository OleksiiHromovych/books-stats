package com.hromovych.android.bookstats.menuOption.Import;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.hromovych.android.bookstats.HelpersItems.Book;
import com.hromovych.android.bookstats.HelpersItems.BookLab;
import com.hromovych.android.bookstats.HelpersItems.DateHelper;
import com.hromovych.android.bookstats.R;
import com.hromovych.android.bookstats.database.ValueConvector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ClipboardFragment extends Fragment {


    private Button clipBoardBtn;
    private Button splitTextBtn;
    private Button splitSplittedTextBtn;
    private LinearLayout appropriateFieldLayout;

    //    Layout for hide and show consequent
    private LinearLayout firstState;
    private LinearLayout secondState;
    private LinearLayout thirtyState;

    private String importText = "";
    private String[] splittedText;
    private List<String[]> splitSplittedText;
    private int line_index = 0;
    private boolean dateSet = false;

    public static ClipboardFragment newInstance() {
        ClipboardFragment fragment = new ClipboardFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_import_clipboard, container, false);
        firstState = v.findViewById(R.id.import_first_show_state);
        secondState = v.findViewById(R.id.import_second_hide_stage);
        thirtyState = v.findViewById(R.id.import_thirty_show_state);
        final Button finishBtn = v.findViewById(R.id.import_finish_btn);

        firstState.setVisibility(View.GONE);
        secondState.setVisibility(View.GONE);
        thirtyState.setVisibility(View.GONE);
        finishBtn.setVisibility(View.GONE);


        appropriateFieldLayout = v.findViewById(R.id.import_appropriate_field);
//        start btn, get text from clipboard
        clipBoardBtn = v.findViewById(R.id.import_clipboard_btn);
        clipBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{importText = ((ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE))
                        .getText().toString();}
                catch (NullPointerException e){
                    showMessage(e.toString());
                    return;
                }
                firstState.setVisibility(View.VISIBLE);
                showMessage("Success import from clip board");
            }
        });

        splitTextBtn = v.findViewById(R.id.import_split_text);
        final TextView splitted_line_view = v.findViewById(R.id.import_splitted_line_view);
        splitTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splittedText = importText.split(((EditText) firstState.findViewById(R.id.import_split_symbol))
                        .getText().toString());
                splitted_line_view.setText(splittedText[line_index]);
                secondState.setVisibility(View.VISIBLE);
                showMessage("Success split text");
            }
        });

        v.findViewById(R.id.import_splitted_line_next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitted_line_view.setText(splittedText[(++line_index + splittedText.length)
                        % splittedText.length]);
            }
        });

        v.findViewById(R.id.import_splitted_line_prev_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitted_line_view.setText(splittedText[(--line_index + splittedText.length)
                        % splittedText.length]);
            }
        });

        splitSplittedTextBtn = v.findViewById(R.id.import_split_splitted_text);
        splitSplittedTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitSplittedText = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : (((EditText) secondState.findViewById(R.id.import_split_splitted_symbol))
                        .getText().toString().split(" ~ "))) {
                    stringBuilder.append(s);
                    stringBuilder.append("|");
                }
                stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("|"));
                for (String s : splittedText) {
                    splitSplittedText.add(s.split(stringBuilder.toString()));
                }
                thirtyState.setVisibility(View.VISIBLE);

            }
        });

        final List<String> popupMenuElement =
                new ArrayList<String>(Arrays.asList("Author", "Name", "Description"));
        thirtyState.findViewById(R.id.import_add_item_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                if (((Spinner) thirtyState.findViewById(R.id.import_status_spinner)).getSelectedItem().toString()
                        .equals(getString(R.string.title_read_yet)) && !dateSet) {
                    popupMenuElement.add("Date");
                    dateSet = true;
                }
                for (String s : popupMenuElement)
                    popupMenu.getMenu().add(s);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String item_text = item.getTitle().toString();
                        if (item_text.equals("Date"))
                            appropriateFieldLayout.addView(createItemLayout("Date",
                                    new String[]{"2017", "2018", "2019", "2020"}));
                        else
                            appropriateFieldLayout.addView(createItemLayout(item_text,
                                    splitSplittedText.get(line_index)));
                        popupMenuElement.remove(item_text);
                        return true;
                    }
                });
                popupMenu.show();
                finishBtn.setVisibility(View.VISIBLE);
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String[]> spinnersChoices = new ArrayList<>();
                for (int i = 0; i < appropriateFieldLayout.getChildCount(); i++)
                    spinnersChoices.add(new String[]{
                            ((TextView) appropriateFieldLayout.getChildAt(i).
                                    findViewById(R.id.import_item_choice_split_type_text)).getText().toString(),
                            ((Spinner) appropriateFieldLayout.getChildAt(i).
                                    findViewById(R.id.import_item_choice_split_type_spinner)).getSelectedItem().toString()});

                String status = ((Spinner) thirtyState.findViewById(R.id.import_status_spinner)).getSelectedItem().toString();
                status = ValueConvector.ToConstant.toStatusConstant(getContext(), status);

                List<String> list = new ArrayList<>(Arrays.asList(splitSplittedText.get(line_index)));
                int author_index = -1, name_index = -1, description_index = -1;
                String date = null;

                for (int i = 0; i < spinnersChoices.size(); i++) {
                    String[] strings = spinnersChoices.get(i);
                    switch (strings[0]) {
                        case "Author":
                            author_index = list.indexOf(strings[1]);
                            break;
                        case "Name":
                            name_index = list.indexOf(strings[1]);
                            break;
                        case "Description":
                            description_index = list.indexOf(strings[1]);
                            break;
                        case "Date":
                            date = strings[1];
                            break;
                    }
                }

                BookLab bookLab = BookLab.get(getActivity());
                for (String[] strings : splitSplittedText) {
                    Book book = new Book();
                    try {
                        if (author_index != -1)
                            book.setAuthor(strings[author_index].replaceAll("^\\s+|\\s+$", ""));
                        if (name_index != -1)
                            book.setBookName(strings[name_index].replaceAll("^\\s+|\\s+$", ""));
                        if (description_index != -1)
                            book.setDescription(strings[description_index].replaceAll("^\\s+|\\s+$", ""));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(), Arrays.toString(strings) + " not import",
                                Toast.LENGTH_SHORT).show();
                        continue;
                    }
                    if (date != null && status.equals(ValueConvector.Constants.READ_YET)) {
                        book.setEndDate(new GregorianCalendar(Integer.parseInt(date), 0, 1).
                                getTime());
                        book.setStartDate(DateHelper.unknownDate);
                    }

                    book.setStatus(status);
                    bookLab.addBook(book);
                }
                getActivity().finish();
            }
        });

        return v;

    }

    private LinearLayout createItemLayout(String name, String[] items) {
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_import,
                appropriateFieldLayout, false);
        TextView textView = layout.findViewById(R.id.import_item_choice_split_type_text);
        textView.setText(name);
        List<String> a = new ArrayList<>(Arrays.asList(items));
        a.add("");
        items = new String[a.size()];
        a.toArray(items);
        Spinner spinner = layout.findViewById(R.id.import_item_choice_split_type_spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        return layout;
    }

    private void showMessage(String text){
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}