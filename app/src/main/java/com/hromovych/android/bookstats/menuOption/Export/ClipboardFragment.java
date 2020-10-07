package com.hromovych.android.bookstats.menuOption.Export;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hromovych.android.bookstats.R;

public class ClipboardFragment extends Fragment implements View.OnClickListener {

    private ImageButton sendDataBtn;
    private ImageButton copyDataBtn;

    private CheckBox yetCheckBox;
    private CheckBox nowCheckBox;
    private CheckBox wantCheckBox;

    public static ClipboardFragment newInstance() {

        return new ClipboardFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_export_clipboard, container, false);

        sendDataBtn = (ImageButton) v.findViewById(R.id.export_send_btn);
        copyDataBtn = (ImageButton) v.findViewById(R.id.export_copy_to_clipboard_btn);

        yetCheckBox = (CheckBox) v.findViewById(R.id.yet_checkbox);
        nowCheckBox = (CheckBox) v.findViewById(R.id.now_checkbox);
        wantCheckBox = (CheckBox) v.findViewById(R.id.want_checkbox);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.export_send_btn:
                break;
            case R.id.export_copy_to_clipboard_btn:
                break;
        }
    }

    private String getBooksData() {
        String data = "";

        return data;
    }

    private String getStatusRequestString(){
        String result = "";
        return result;
    }
}
