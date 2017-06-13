package com.papermelody.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.papermelody.R;


public class SettingsPlayPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_play_menu);
    }
}

