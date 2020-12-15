package com.hromovych.android.bookstats.menuOption.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.hromovych.android.bookstats.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey);
        setSummary(getString(R.string.key_list_date_format));
        setSummary(getString(R.string.key_list_sort_format));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (findPreference(key) instanceof DropDownPreference) {
            setSummary(key);
        }
    }

    private void setSummary(String key) {
        DropDownPreference preference = findPreference(key);
        String optionalValue = getPreferenceScreen().getSharedPreferences().getString(key, "");
        preference.setSummary(optionalValue);
    }
}
