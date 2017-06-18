package com.papermelody.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.papermelody.R;
import com.papermelody.activity.PositonCalibrationActivity;


public class SettingsPlayPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_play_menu);
        initPreference();
    }

    public void initPreference() {
        findPreference("key_calibration").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                //ToastUtil.showShort("okkkkkkkkkkkkkkkkkk");
                Intent intent = new Intent(getActivity(), PositonCalibrationActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }
}