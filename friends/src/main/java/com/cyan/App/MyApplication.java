package com.cyan.App;

import android.app.Application;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.cyan.bean.MyBmobInstallation;
import com.cyan.bean.User;
import com.cyan.util.SPUtils;
import com.cyan.util.StorageUtils;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

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
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        if ((boolean) SPUtils.get(this, "settings", "message_key", false))
            // 启动推送服务
        {
            refreshInstallation(MyApplication.getInstance().getCurrentUser().getObjectId());
            BmobPush.startWork(this, APPID);
       }
        GlideBuilder builder = new GlideBuilder(this);
        builder.setMemoryCache(new LruResourceCache(5 * 1024 * 1024));
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
        builder.setDiskCache(
                new ExternalCacheDiskCacheFactory(this, StorageUtils.getCacheDirectory(getApplicationContext()).getPath(), 10 * 1024 * 1024));
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
    private void refreshInstallation(final String userId) {
        BmobQuery<MyBmobInstallation> query = new BmobQuery<MyBmobInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(MyApplication.getInstance()));
        Log.i("objectId", BmobInstallation.getCurrentInstallation(MyApplication.getInstance()).getObjectId() + "");
        query.findObjects(this, new FindListener<MyBmobInstallation>() {

            @Override
            public void onSuccess(List<MyBmobInstallation> object) {
                // TODO Auto-generated method stub
                if (object.size() > 0) {
                    final MyBmobInstallation mbi = object.get(0);
                    Log.i("userId", userId);
                    mbi.setUid(userId);
                    Log.i("objectId", mbi.getObjectId());
                    SPUtils.put(MyApplication.getInstance(),"settings" ,"installation", mbi.getObjectId());
                    mbi.update(MyApplication.getInstance(), new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            Log.i("objectId", mbi.getObjectId() + "");
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
