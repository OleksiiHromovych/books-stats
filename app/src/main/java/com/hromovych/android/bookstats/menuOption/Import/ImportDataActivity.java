package com.hromovych.android.bookstats.menuOption.Import;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hromovych.android.bookstats.Book;
import com.hromovych.android.bookstats.BookLab;
import com.hromovych.android.bookstats.DateHelper;
import com.hromovych.android.bookstats.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

public class ImportDataActivity extends AppCompatActivity {

    private static final String EXTRA_DB_PATH = "extra_db_path";

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ImportDataActivity.class);
        return intent;
    }

    public static Intent newIntent(Context context, String path) {
        Intent intent = new Intent(context, ImportDataActivity.class);
        intent.putExtra(EXTRA_DB_PATH, path);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        final String path = getIntent().getStringExtra(EXTRA_DB_PATH);
        final Button importFromClipboard = (Button) findViewById(R.id.import_from_clipboard_btn);
        final Button importFromFile = (Button) findViewById(R.id.import_from_file_btn);

        importFromClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .add(R.id.import_activity_container, ClipboardFragment.newInstance())
                        .commit();
            }
        });
        importFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path == null)
                    return;
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .add(R.id.import_activity_container, FromFileFragment.newInstance(path))
                        .commit();
            }
        });

        if (path != null)
            importFromFile.performClick();
    }
}