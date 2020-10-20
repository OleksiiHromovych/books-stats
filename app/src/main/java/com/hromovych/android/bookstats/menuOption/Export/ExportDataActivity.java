package com.hromovych.android.bookstats.menuOption.Export;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.hromovych.android.bookstats.R;

public class ExportDataActivity extends AppCompatActivity implements View.OnClickListener {

    public static Intent newIntent(Context context) {
        return new Intent(context, ExportDataActivity.class);
    }

    private Button exportText;
    private Button exportBD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        exportText = findViewById(R.id.export_as_text_btn);
        exportBD = findViewById(R.id.export_as_bd_btn);

        exportText.setOnClickListener(this);
        exportBD.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        switch (v.getId()){
            case R.id.export_as_text_btn:
                fragment = ToTextFragment.newInstance();
                break;
            case R.id.export_as_bd_btn:
                fragment = ToFileFragment.newInstance();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
        exportText.setVisibility(View.GONE);
        exportBD.setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.export_activity_container, fragment)
                .commit();

    }
}
