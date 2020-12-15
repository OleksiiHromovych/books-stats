package com.hromovych.android.bookstats.ui.abandoned;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hromovych.android.bookstats.HelpersItems.SimpleActivity;
import com.hromovych.android.bookstats.R;

public class AbandonedActivity extends SimpleActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setTitle(R.string.title_abandoned);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new AbandonedListFragment())
                .commit();
    }
}
