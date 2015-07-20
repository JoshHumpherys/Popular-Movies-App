package com.example.android.project1.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import app.project1.android.example.com.popularmoviesapp.R;

/**
 * Created by Admin-HHE on 7/13/2015.
 */
public class SettingsActivity extends PreferenceActivity
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
        if(preference instanceof ListPreference) {
            ListPreference lp = (ListPreference) preference;
            preference.setSummary(lp.getEntries()[lp.findIndexOfValue(newValue.toString())]);
        }
        else {
            preference.setSummary(newValue.toString());
        }
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(this, MainActivity.class));
//    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
