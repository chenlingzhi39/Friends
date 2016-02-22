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
import cn.bmob.v3.BmobInstallation;

/**
 * Created by Administrator on 2016/2/1.
 */
public class SettingsFragment extends PreferenceFragment{
    SwitchPreference message,network;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("settings");
        addPreferencesFromResource(R.xml.preferences);
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
            {
                BmobInstallation.getCurrentInstallation(MyApplication.getInstance()).save();
                BmobPush.startWork(MyApplication.getInstance(), MyApplication.APPID);
           }
                else
            {
                BmobInstallation.getCurrentInstallation(MyApplication.getInstance()).delete(MyApplication.getInstance());
            MyApplication.getInstance().stopService(new Intent("cn.bmob.push.lib.service.PushService").setPackage(getActivity().getPackageName()));}
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
