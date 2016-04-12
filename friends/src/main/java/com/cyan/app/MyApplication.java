package com.cyan.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.cyan.bean.MyBmobInstallation;
import com.cyan.bean.User;
import com.cyan.common.Constants;
import com.cyan.community.BuildConfig;
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
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;

/**
 * Created by Administrator on 2015/11/18.
 */
public class MyApplication extends Application {
    private static MyApplication myApplication;
    private User myUser;
    public static String APPID = "9245da2bae59a43d2932e1324875137a";
    private DaoSession mDaoSession;
    private SQLiteDatabase db;
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
        if ((boolean) SPUtils.get(this, "settings", "message_key", false)||SPUtils.get(this, "settings", "message_key", false)==null)
            // 启动推送服务
        {SPUtils.put(this, "settings", "message_key", true);
            if(MyApplication.getInstance().getCurrentUser()!=null)
            refreshInstallation(MyApplication.getInstance().getCurrentUser().getObjectId());
            else refreshInstallation("0");
            BmobPush.startWork(this, APPID);
       }
        GlideBuilder builder = new GlideBuilder(this);
        builder.setMemoryCache(new LruResourceCache(5 * 1024 * 1024));
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
        builder.setDiskCache(
                new ExternalCacheDiskCacheFactory(this, StorageUtils.getCacheDirectory(getApplicationContext()).getPath(), 10 * 1024 * 1024));
        setupDatabase();
    }
    private void setupDatabase() {
        // // 官方推荐将获取 DaoMaster 对象的方法放到 Application 层，这样将避免多次创建生成 Session 对象
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, Constants.DB_NAME, null);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
        // 在 QueryBuilder 类中内置两个 Flag 用于方便输出执行的 SQL 语句与传递参数的值
        QueryBuilder.LOG_SQL = BuildConfig.DEBUG;
        QueryBuilder.LOG_VALUES = BuildConfig.DEBUG;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
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
