package com.example.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.administrator.myapplication.R;

/**
 * Created by Administrator on 2016/2/1.
 */
public class SettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
