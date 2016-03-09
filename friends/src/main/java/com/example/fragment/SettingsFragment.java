package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.administrator.myapplication.R;
import com.example.bean.MyBmobInstallation;
import com.example.ui.MyApplication;
import com.example.util.RxBus;
import com.example.util.SPUtils;
import com.jenzz.materialpreference.SwitchPreference;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/2/1.
 */
public class SettingsFragment extends PreferenceFragment {
    SwitchPreference message, network,night_mode;

    public static SettingsFragment newInstance(){
        SettingsFragment settingsFragment=new SettingsFragment();
        return settingsFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("settings");
        addPreferencesFromResource(R.xml.preferences);
        message = (SwitchPreference) findPreference("message_key");
        network = (SwitchPreference) findPreference("network_key");
        night_mode=(SwitchPreference)findPreference("night_mode_key");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch(preference.getKey()) {
            case "clear_key":
                Log.i("clear", "cache");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(getActivity()).clearDiskCache();
                    }
                }).start();
                Glide.get(getActivity()).clearMemory();
         break;
                case "message_key":
                if (message.isChecked()) {
                    BmobInstallation.getCurrentInstallation(MyApplication.getInstance()).save(MyApplication.getInstance(), new SaveListener() {
                        @Override
                        public void onSuccess() {
                            refreshInstalllation(MyApplication.getInstance().getCurrentUser().getObjectId());
                            BmobPush.startWork(MyApplication.getInstance(), MyApplication.APPID);

                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }

                    });

                } else {
                    BmobInstallation.getCurrentInstallation(MyApplication.getInstance()).delete(getActivity(), (String) SPUtils.get(MyApplication.getInstance(), "settings", "installation", ""), new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("delete", "success");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.i("fail", s);
                        }
                    });
                    MyApplication.getInstance().stopService(new Intent("cn.bmob.push.lib.service.PushService").setPackage(getActivity().getPackageName()));
                }
                    break;
            case "night_mode_key":
                //getActivity().setTheme(night_mode.isChecked()?R.style.BaseAppNightTheme_AppNightTheme : R.style.BaseAppTheme_AppTheme);
                RxBus.get().post("finish", true);
                break;
            }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void refreshInstalllation(final String userId) {
        Log.i("refresh", "Installation");
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
                    SPUtils.put(MyApplication.getInstance(),"settings","installation", mbi.getObjectId());
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
