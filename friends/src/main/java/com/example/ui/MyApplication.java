package com.example.ui;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.bean.MyBmobInstallation;
import com.example.bean.User;
import com.example.util.SPUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by Administrator on 2015/11/18.
 */
public class MyApplication extends Application {
    private static MyApplication myApplication;
    private User myUser;
    public static String APPID = "9245da2bae59a43d2932e1324875137a";
    public static MyApplication getInstance(){
        return myApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        Bmob.initialize(getApplicationContext(), APPID);
        BmobUpdateAgent.initAppVersion(this);
        BmobUpdateAgent.update(this);
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        if((boolean)SPUtils.get(this,"message_key",false))
        // 启动推送服务
        BmobPush.startWork(this, APPID);
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration config =new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new LruMemoryCache(5*1024*1024))
                .memoryCacheSize(10*1024*1024)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);

    }
    public DisplayImageOptions getOptions(){
        return new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
    /**
     * 获取本地用户
     */
    public User getCurrentUser() {
        if(myUser==null)
        myUser = BmobUser.getCurrentUser(this, User.class);
        return myUser;
    }
    public void setCurrentUser(){
        myUser = BmobUser.getCurrentUser(this, User.class);
    }
    public void clearCurrentUser(){
        myUser=null;
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
