package com.hromovych.android.bookstats.menuOption.export;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hromovych.android.bookstats.R;

public class ExportDataActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, ExportDataActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, ExportMenuFragment.newInstance())
                .commit();
    }


}
