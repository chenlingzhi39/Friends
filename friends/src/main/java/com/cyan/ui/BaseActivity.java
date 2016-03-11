package com.cyan.ui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.community.R;
import com.cyan.util.RxBus;
import com.cyan.util.SPUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.bmob.v3.datatype.BmobFile;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2015/10/30.
 */
public class BaseActivity<T> extends AppCompatActivity {

    /**
     * 标示该activity是否可滑动退出,默认false
     */
    protected boolean mEnableSlidr;

    /**
     * 布局的id
     */
    protected int mContentViewId;




    private Class mClass;

    /**
     * 菜单的id
     */
    private int mMenuId;

    /**
     * Toolbar标题
     */
    private int mToolbarTitle;
    private ArrayList<T> list;
    Toast mToast;
    public static String TAG = "bmob";
    ProgressDialog dialog,pd;
    String url;
    public BmobFile imageFile;
    File f;
    public Subscription subscription,sub;
    private Observable<Boolean> mReCreateObservable;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
            ActivityFragmentInject annotation = getClass()
                    .getAnnotation(ActivityFragmentInject.class);
            mContentViewId = annotation.contentViewId();
            mEnableSlidr = annotation.enableSlidr();
            mMenuId = annotation.menuId();
            mToolbarTitle = annotation.toolbarTitle();
        } else {
            throw new RuntimeException(
                    "Class must add annotations of ActivityFragmentInitParams.class");
        }
        setContentView(mContentViewId);
        initToolbar();
        setToolbarTitle(mToolbarTitle);
        setTheme((boolean)SPUtils.get(this,"settings","night_mode_key",false)? R.style.BaseAppNightTheme_AppNightTheme : R.style.BaseAppTheme_AppTheme);
        mReCreateObservable = RxBus.get().register("recreate", Boolean.class);
        mReCreateObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean themeChange) {
                Log.i("classname",BaseActivity.this.getClass().getName());
             //if(themeChange&&!BaseActivity.this.getClass().getName().equals("com.example.ui.SettingsActivity")) finish();
             recreate();
            }
        });
    }
    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }
    protected void setToolbarTitle(int strId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(strId);
        }
    }
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }
    public  void saveBitmap(Bitmap bitmap,String path,String filename) {
        f = new File(path, filename);
        Toast.makeText(getApplicationContext(), f.getPath(), Toast.LENGTH_SHORT).show();
        if(!f.getParentFile().exists()||!f.getParentFile().isDirectory()){
            f.getParentFile().mkdirs();
        }
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");

        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if(mMenuId!=-1)
        getMenuInflater().inflate(mMenuId, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (mReCreateObservable != null) {
            RxBus.get().unregister("recreate", mReCreateObservable);
        }
    }
}
