package com.example.ui;

import android.app.Application;
import android.graphics.Bitmap;

import com.example.bean.User;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2015/11/18.
 */
public class MyApplication extends Application {
    private static MyApplication myApplication;
    public static MyApplication getInstance(){

        return myApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
       myApplication = this;
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
        User myUser = BmobUser.getCurrentUser(this, User.class);

        return myUser;
    }
}
