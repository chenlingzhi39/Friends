package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.example.administrator.myapplication.R;
import com.example.ui.MyApplication;
import com.jenzz.materialpreference.SwitchPreference;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.push.BmobPush;

/**
 * Created by Administrator on 2016/2/1.
 */
public class SettingsFragment extends PreferenceFragment{
    SwitchPreference message,network;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceManager().setSharedPreferencesName("settings");
        message=(SwitchPreference)findPreference("message_key");
        network=(SwitchPreference)findPreference("network_key");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if(preference.getKey().equals("clear_key")){
            Log.i("clear","cache");
            ImageLoader.getInstance().clearMemoryCache();
            ImageLoader.getInstance().clearDiscCache();
        }
        if(preference.getKey().equals("message_key")){
            if(message.isChecked())
                BmobPush.startWork(getActivity(), MyApplication.APPID);
                else
            getActivity().stopService(new Intent("cn.bmob.push.lib.service.PushService"));
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
