package com.hromovych.android.bookstats.menuOption.Import;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.hromovych.android.bookstats.R;

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