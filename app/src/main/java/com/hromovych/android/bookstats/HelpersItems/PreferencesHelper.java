package com.hromovych.android.bookstats.HelpersItems;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.hromovych.android.bookstats.R;

public class PreferencesHelper {

    private static final String ARRAY_JOINT_DELIMITER = "_";

    public SharedPreferences mSharedPreferences;
    private final Context context;

    public PreferencesHelper(Context context) {
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

    public void putExpendedYetArray(Boolean[] array) {
        putExpendedArray(array, getKey(R.string.YET_EXPENDED_ARRAY_KEY));
    }

    public Boolean[] getExpendedYetArray() {
        return getExpendedArray(getKey(R.string.YET_EXPENDED_ARRAY_KEY));
    }

    private void putExpendedArray(Boolean[] array, String key) {
        mSharedPreferences.edit().putString(key, arrayToString(array)).apply();
    }

    private Boolean[] getExpendedArray(String key) {
        return arrayFromString(mSharedPreferences.getString(key, ""));
    }

    private String arrayToString(Boolean[] array) {
        return TextUtils.join(ARRAY_JOINT_DELIMITER, array);
    }

    private Boolean[] arrayFromString(String text) {
        String[] strings = text.split(ARRAY_JOINT_DELIMITER);
        Boolean[] result = new Boolean[strings.length];
        for (int i = 0; i < strings.length; i++) {
            result[i] = Boolean.parseBoolean(strings[i]);
        }
        return result;
    }

    public boolean isFirstRun() {
        return mSharedPreferences.getBoolean(getKey(R.string.FIRST_RUN_KEY), true);
    }

    public void putIsFirstRun(boolean isFirstRun) {
        mSharedPreferences.edit().putBoolean(getKey(R.string.FIRST_RUN_KEY), isFirstRun).apply();
    }
}
