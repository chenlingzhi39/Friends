package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.example.administrator.myapplication.R;
import com.example.bean.MyBmobInstallation;
import com.example.ui.MyApplication;
import com.jenzz.materialpreference.SwitchPreference;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

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
            {   refreshInstalllation(MyApplication.getInstance().getCurrentUser().getObjectId());
                BmobInstallation.getCurrentInstallation(MyApplication.getInstance()).save();
                BmobPush.startWork(MyApplication.getInstance(), MyApplication.APPID);
           }
                else
            {
                refreshInstalllation("0");
            MyApplication.getInstance().stopService(new Intent("cn.bmob.push.lib.service.PushService").setPackage(getActivity().getPackageName()));}
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
               private void refreshInstalllation(final String userId) {
                Log.i("refresh","Installation");
                BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
                query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(MyApplication.getInstance()));
                query.findObjects(MyApplication.getInstance(), new FindListener<MyBmobInstallation>() {

                    @Override
                    public void onSuccess(List<MyBmobInstallation> object) {
                        // TODO Auto-generated method stub
                        if (object.size() > 0) {
                            MyBmobInstallation mbi = object.get(0);
                            Log.i("userId", userId);
                            mbi.setUid(userId);
                            mbi.update(MyApplication.getInstance(), new UpdateListener() {

                                @Override
                                public void onSuccess() {
                                    // TODO Auto-generated method stub
                                    // 使用推送服务时的初始化操作
                                    Log.i("bmob", "设备信息更新成功");
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    // TODO Auto-generated method stub
                                    Log.i("bmob", "设备信息更新失败:" + msg);
                                }
                            });
                        } else {
                        }
                    }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
            }
        });
    }
}
