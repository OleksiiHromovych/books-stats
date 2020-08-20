package com.hromovych.android.bookstats;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
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
                splitted_line_view.setText(splittedText[++line_index%splittedText.length]);
            }
        });

        ((Button) findViewById(R.id.import_splitted_line_prev_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitted_line_view.setText(splittedText[--line_index%splittedText.length]);
            }
        });

        splitSplittedTextBtn = (Button) findViewById(R.id.import_split_splitted_text);
        splitSplittedTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitSplittedText = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder("[");
                stringBuilder.append(Arrays.toString(((EditText) findViewById(R.id.import_split_splitted_symbol))
                        .getText().toString().split(" ~ ")));
                stringBuilder.append("]");
                for (String s : splittedText) {
                    splitSplittedText.add(s.split(stringBuilder.toString()));
                }

            }
        });

        ((Button) findViewById(R.id.import_add_item_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] strings = {"Author", "Name", "Description", "Date",};
                for (String s : strings)
                    appropriateFieldLayout.addView(createItemLayout(s, splitSplittedText.get(0)));
            }
        });

        final Button finishBtn = (Button) findViewById(R.id.import_finish_btn);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> spinnersChoices = new ArrayList<>();
                spinnersChoices.add(
                        ((Spinner) appropriateFieldLayout.getChildAt(0).
                                findViewById(R.id.import_item_choice_split_type_spinner)).getSelectedItem().toString());
                spinnersChoices.add(
                        ((Spinner) appropriateFieldLayout.getChildAt(1).findViewById(R.id.import_item_choice_split_type_spinner)).getSelectedItem().toString());

                String el = ((Spinner) appropriateFieldLayout.getChildAt(2).findViewById(R.id.import_item_choice_split_type_spinner)).getSelectedItem().toString();
                if(spinnersChoices.contains(el))
                    spinnersChoices.add("");
                else
                    spinnersChoices.add(el);

                String status = ((Spinner) findViewById(R.id.import_status_spinner)).getSelectedItem().toString();

                List<String> list = new ArrayList<>(Arrays.asList(splitSplittedText.get(0)));

                int author_index = list.indexOf(spinnersChoices.get(0));
                int name_index = list.indexOf(spinnersChoices.get(1));
                int description_index = list.indexOf(spinnersChoices.get(2));

                BookLab bookLab = BookLab.get(getApplicationContext());
                for (String[] strings : splitSplittedText) {
                    Book book = new Book();
                    book.setStatus(status);
                    book.setAuthor(strings[author_index].replaceAll("\\s+",""));
                    book.setBookName(strings[name_index].replaceAll("\\s+",""));
                    book.setDescription(strings[description_index].replaceAll("\\s+",""));
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

        Spinner spinner = (Spinner) layout.findViewById(R.id.import_item_choice_split_type_spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        return layout;
    }
}