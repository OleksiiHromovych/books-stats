package com.hromovych.android.bookstats;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ImportDataActivity extends AppCompatActivity {
    private Button clipBoardBtn;
    private Button splitTextBtn;
    private Button splitSplittedTextBtn;

    private String importText = "";
    private String[] splittedText;
    private List<String[]> splitSplittedText;


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ImportDataActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

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
        splitTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splittedText = importText.split(((EditText) findViewById(R.id.import_split_symbol))
                        .getText().toString());
                ((TextView) findViewById(R.id.import_splitted_line_view)).setText(splittedText[0]);
            }
        });


        final TextView import_choice_split_type_text1 =
                (TextView) findViewById(R.id.import_choice_split_type_text1);

        final TextView import_choice_split_type_text2 =
                (TextView) findViewById(R.id.import_choice_split_type_text2);

        splitSplittedTextBtn = (Button) findViewById(R.id.import_split_splitted_text);
        splitSplittedTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitSplittedText = new ArrayList<>();
                for (String s : splittedText) {
                    splitSplittedText.add(s.split((
                            (EditText) findViewById(R.id.import_split_splitted_symbol))
                            .getText().toString()));
                }
                import_choice_split_type_text1.setText(splitSplittedText.get(0)[0]);
                import_choice_split_type_text2.setText(splitSplittedText.get(0)[1]);

            }
        });
        final Button finishBtn = (Button) findViewById(R.id.import_finish_btn);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> spinnersChoices = new ArrayList<>();

                spinnersChoices.add(
                        ((Spinner) findViewById(R.id.import_choice_split_type_spinner1)).getSelectedItem().toString());
                spinnersChoices.add(
                        ((Spinner) findViewById(R.id.import_choice_split_type_spinner2)).getSelectedItem().toString());
                String status = ((Spinner) findViewById(R.id.import_status_spinner)).getSelectedItem().toString();
                int author_index = spinnersChoices.indexOf(
                        getResources().getString(R.string.book_author_title));
                int name_index = spinnersChoices.indexOf(
                        getResources().getString(R.string.book_name_title));
                BookLab bookLab = BookLab.get(getApplicationContext());
                for (String[] strings : splitSplittedText) {
                    Book book = new Book();
                    book.setStatus(status);
                    book.setAuthor(strings[author_index]);
                    book.setBookName(strings[name_index]);
                    bookLab.addBook(book);
                }
                finish();
            }
        });


    }
}