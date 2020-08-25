package com.hromovych.android.bookstats;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

public class ImportDataActivity extends AppCompatActivity {
    private Button clipBoardBtn;
    private Button splitTextBtn;
    private Button splitSplittedTextBtn;
    private LinearLayout appropriateFieldLayout;


    private String importText = "";
    private String[] splittedText;
    private List<String[]> splitSplittedText;
    private int line_index = 0;


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ImportDataActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        appropriateFieldLayout = (LinearLayout) findViewById(R.id.import_appropriate_field);
        clipBoardBtn = (Button) findViewById(R.id.import_clipboard_btn);

        clipBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importText = ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE))
                        .getText().toString();
                ((LinearLayout) findViewById(R.id.import_main_layout)).setVisibility(View.VISIBLE);

            }
        });

        splitTextBtn = (Button) findViewById(R.id.import_split_text);
        final TextView splitted_line_view = (TextView) findViewById(R.id.import_splitted_line_view);
        splitTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splittedText = importText.split(((EditText) findViewById(R.id.import_split_symbol))
                        .getText().toString());
                splitted_line_view.setText(splittedText[line_index]);
            }
        });

        ((Button) findViewById(R.id.import_splitted_line_next_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitted_line_view.setText(splittedText[(++line_index + splittedText.length)
                        % splittedText.length]);
            }
        });

        ((Button) findViewById(R.id.import_splitted_line_prev_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitted_line_view.setText(splittedText[(--line_index + splittedText.length)
                        % splittedText.length]);
            }
        });

        splitSplittedTextBtn = (Button) findViewById(R.id.import_split_splitted_text);
        splitSplittedTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitSplittedText = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder("");
                for (String s : (((EditText) findViewById(R.id.import_split_splitted_symbol))
                        .getText().toString().split(" ~ "))) {
                    stringBuilder.append(s);
                    stringBuilder.append("|");
                }
                stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("|"));
                for (String s : splittedText) {
                    splitSplittedText.add(s.split(stringBuilder.toString()));
                }

            }
        });

        ((Button) findViewById(R.id.import_add_item_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);

                String[] strings = {"Author", "Name", "Description",};
                if (((Spinner) findViewById(R.id.import_status_spinner)).getSelectedItem().toString()
                        .equals(getString(R.string.title_read_yet)))
                    strings = new String[]{"Author", "Name", "Description", "Date",};

                for (String s : strings)
                    popupMenu.getMenu().add(s);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().toString().equals("Date")) {
                            appropriateFieldLayout.addView(createItemLayout("Date",
                                    new String[]{"2017", "2018", "2019", "2020"}));
                        } else {
                            appropriateFieldLayout.addView(createItemLayout(item.getTitle().toString(),
                                    splitSplittedText.get(line_index)));
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        final Button finishBtn = (Button) findViewById(R.id.import_finish_btn);
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

                String status = ((Spinner) findViewById(R.id.import_status_spinner)).getSelectedItem().toString();

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

                BookLab bookLab = BookLab.get(getApplicationContext());
                for (String[] strings : splitSplittedText) {
                    Book book = new Book();
                    book.setStatus(status);
                    if (author_index != -1)
                        book.setAuthor(strings[author_index].replaceAll("^\\s+|\\s+$", ""));
                    if (name_index != -1)
                        book.setBookName(strings[name_index].replaceAll("^\\s+|\\s+$", ""));
                    if (description_index != -1)
                        book.setDescription(strings[description_index].replaceAll("^\\s+|\\s+$", ""));
                    if (!date.equals(null) && status.equals(getString(R.string.title_read_yet))) {
                        book.setEndDate(new GregorianCalendar(Integer.parseInt(date), 0, 1).
                                getTime());
                    }
                    bookLab.addBook(book);
                }
                finish();
            }
        });

    }

    private LinearLayout createItemLayout(String name, String[] items) {
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_import,
                appropriateFieldLayout, false);
        TextView textView = (TextView) layout.findViewById(R.id.import_item_choice_split_type_text);
        textView.setText(name);
        List<String> a = new ArrayList<>(Arrays.asList(items));
        a.add("");
        items = new String[a.size()];
        a.toArray(items);
        Spinner spinner = (Spinner) layout.findViewById(R.id.import_item_choice_split_type_spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        return layout;
    }
}