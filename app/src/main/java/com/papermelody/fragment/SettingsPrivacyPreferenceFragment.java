package com.papermelody.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.papermelody.R;


public class SettingsPrivacyPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_privacy_menu);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
