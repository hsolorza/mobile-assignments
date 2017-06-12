package com.example.android.weatherwithpreferences;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.preference.EditTextPreference;
import android.support.v4.app.LoaderManager;

import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.android.weatherwithpreferences.utils.OpenWeatherMapUtils;

/**
 * Created by soloh on 6/5/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.prefs);
        EditTextPreference userPref = (EditTextPreference)findPreference(getString(R.string.pref_location_key));
        userPref.setSummary(userPref.getText());
    }

//    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        System.out.println("In the settings changed!");
        if (key.equals(getString(R.string.pref_location_key))) {
            EditTextPreference userPref = (EditTextPreference)findPreference(key);
            userPref.setSummary(userPref.getText());
        }
        String unit = sharedPreferences.getString("pref_sort", "");
        String location = sharedPreferences.getString("pref_location", "");
        String search = OpenWeatherMapUtils.buildForecastURL(location, unit);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
