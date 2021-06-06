package com.hromovych.android.bookstats.menuOption.import_option;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hromovych.android.bookstats.R;

public class ImportDataActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, ImportDataActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, ImportMenuFragment.newInstance())
                .commit();
    }
}