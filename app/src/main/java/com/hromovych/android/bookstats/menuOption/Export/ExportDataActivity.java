package com.hromovych.android.bookstats.menuOption.Export;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hromovych.android.bookstats.R;

public class ExportDataActivity extends AppCompatActivity implements View.OnClickListener {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ExportDataActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        Button exportText = (Button) findViewById(R.id.export_as_text_btn);
        Button exportBD = (Button) findViewById(R.id.export_as_bd_btn);

        exportText.setOnClickListener(this);
        exportBD.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        switch (v.getId()){
            case R.id.export_as_text_btn:
                fragment = ClipboardFragment.newInstance();
                break;
            case R.id.export_as_bd_btn:
                return;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.import_activity_container, fragment)
                .commit();
    }
}
