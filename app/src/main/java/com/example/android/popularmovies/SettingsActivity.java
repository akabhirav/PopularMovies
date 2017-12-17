package com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class PopularMoviesPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        private ListPreference sortBy;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            sortBy = (ListPreference) findPreference(getString(R.string.pref_sort_type_key));
            bindPreferenceSummaryToValue(sortBy);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.equals(sortBy)) {
                String entryString = newValue.toString();
                int index = sortBy.findIndexOfValue(entryString);
                String summary = sortBy.getEntries()[index].toString();
                preference.setSummary(summary);
            }
            return true;
        }
    }
}
