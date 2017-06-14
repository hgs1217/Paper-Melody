package com.papermelody.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.papermelody.R;
import com.papermelody.activity.AboutActivity;
import com.papermelody.activity.SettingsDevActivity;
import com.papermelody.activity.SettingsPlayActivity;
import com.papermelody.activity.SettingsPrivacyActivity;
import com.papermelody.activity.TutorialActivity;


public class SettingsPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_menu);
        initPreference();
    }


    private void initPreference() {
        findPreference("btn_play_setting").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), SettingsPlayActivity.class);
                startActivity(intent);
                return true;
            }
        });


        findPreference("btn_developer").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), SettingsDevActivity.class);
                startActivity(intent);
                return true;
            }
        });


        findPreference("btn_privacy").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), SettingsPrivacyActivity.class);
                startActivity(intent);
                return true;
            }
        });


        findPreference("btn_about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                return true;
            }
        });


        findPreference("btn_viewTutorial").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), TutorialActivity.class);
                startActivity(intent);
                return true;
            }
        });

    }
}