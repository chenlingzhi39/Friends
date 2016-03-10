package com.example.ui;

import android.app.Application;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.example.bean.User;
import com.example.util.SPUtils;
import com.example.util.StorageUtils;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;

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
        if((boolean)SPUtils.get(this,"settings","message_key",false))
        // 启动推送服务
        BmobPush.startWork(this, APPID);

        GlideBuilder builder=new GlideBuilder(this);
        builder.setMemoryCache(new LruResourceCache(5 * 1024 * 1024));
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
        builder.setDiskCache(
                new ExternalCacheDiskCacheFactory(this, StorageUtils.getCacheDirectory(getApplicationContext()).getPath(),10*1024*1024 ));
      /*  ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration config =new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new LruMemoryCache(5*1024*1024))
                .memoryCacheSize(10*1024*1024)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);*/

    }
   /* public DisplayImageOptions getOptions(){
        return new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }*/
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

}
