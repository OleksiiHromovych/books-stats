package com.hromovych.android.bookstats.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.hromovych.android.bookstats.R;

public class PreferencesManager {

    public SharedPreferences mSharedPreferences;
    private final Context context;

    public PreferencesManager(Context context) {
        this.context = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFullDateFormat() {
        return getKey(R.string.preferences_list_item_date_full)
                .equals(mSharedPreferences.getString(
                        getKey(R.string.key_list_date_format), ""));
    }

    public boolean isSortByYear() {
        return getKey(R.string.preferences_list_item_sort_by_year)
                .equals(mSharedPreferences.getString(
                        getKey(R.string.key_list_sort_format), ""));
    }

    private String getKey(int id) {
        return context.getString(id);
    }
}
