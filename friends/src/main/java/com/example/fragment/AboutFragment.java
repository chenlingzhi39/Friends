package com.example.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.example.administrator.myapplication.R;

/**
 * Created by Administrator on 2016/2/29.
 */
public class AboutFragment extends PreferenceFragment {
    public static AboutFragment newInstance(){
        AboutFragment aboutFragment=new AboutFragment();
        return aboutFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("about");
        addPreferencesFromResource(R.xml.about);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case "version":

                break;
            case "source":
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/chenlingzhi39/Friends.git"));
                PackageManager pm = getActivity().getPackageManager();
                ResolveInfo ri = pm.resolveActivity(intent, 0);
                if (ri != null) {
                    if (!(getActivity() instanceof Activity)) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    getActivity().startActivity(intent);
                }
                    break;
                }
                return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }
