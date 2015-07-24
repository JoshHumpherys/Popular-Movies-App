package com.example.android.project1.app;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/24/2015.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        Preference p = findPreference(getString(R.string.pref_sort_key));

        p.setOnPreferenceChangeListener(this);

        onPreferenceChange(p, PreferenceManager.getDefaultSharedPreferences(p.getContext())
                .getString(p.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {
            ListPreference lp = (ListPreference) preference;
            preference.setSummary(lp.getEntries()[lp.findIndexOfValue(newValue.toString())]);
        } else {
            preference.setSummary(newValue.toString());
        }
        return true;
    }
}
