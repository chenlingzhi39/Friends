package com.cyan.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cyan.app.AppManager;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.community.R;
import com.cyan.manager.SystemBarTintManager;
import com.cyan.util.RxBus;
import com.cyan.util.SPUtils;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2015/10/30.
 */
public class BaseActivity extends AppCompatActivity {
    /**
     * 布局的id
     */
    protected int mContentViewId;
    /**
     * 菜单的id
     */
    private int mMenuId;
    /**
     * Toolbar标题
     */
    private int mToolbarTitle;
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
        AppManager.getAppManager().orderActivity(getClass().getName(),false);
        setTheme((boolean) SPUtils.get(this, "settings", "night_mode_key", false) ? R.style.BaseAppNightTheme_AppNightTheme : R.style.BaseAppTheme_AppTheme);
        super.onCreate(savedInstanceState);
        if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
            ActivityFragmentInject annotation = getClass()
                    .getAnnotation(ActivityFragmentInject.class);
            mContentViewId = annotation.contentViewId();
            mMenuId = annotation.menuId();
            mToolbarTitle = annotation.toolbarTitle();
        } else {
            throw new RuntimeException(
                    "Class must add annotations of ActivityFragmentInitParams.class");
        }
        setContentView(mContentViewId);
        initToolbar();
        if(mToolbarTitle!=-1)
        setToolbarTitle(mToolbarTitle);
        mReCreateObservable = RxBus.get().register("recreate", Boolean.class);
        mReCreateObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean themeChange) {
                Log.i("classname", BaseActivity.this.getClass().getName());
                //if(themeChange&&!BaseActivity.this.getClass().getName().equals("com.example.ui.SettingsActivity")) finish();
                recreate();
            }
        });
        if(BaseActivity.this.getClass().getName().equals("com.cyan.ui.MainActivity")||BaseActivity.this.getClass().getName().equals("com.cyan.ui.UserInfoActivity")){}else{
            Log.i("baseactivity","setstatus");
            SystemBarTintManager systemBarTintManager=new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setStatusBarTintResource((boolean) SPUtils.get(this, "settings", "night_mode_key", false) ?R.color.primary_dark_night:R.color.primary);
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
        if (!TextUtils.isEmpty(msg)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), msg,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(msg);
            }
            mToast.show();
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
                Log.i("base","finish");
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
        } if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
        if (mReCreateObservable != null) {
            RxBus.get().unregister("recreate", mReCreateObservable);
        }
        try {
            if(AppManager.getAppManager().getLastNavActivity()!=null)
            AppManager.getAppManager().orderActivity(AppManager.getAppManager().getLastNavActivity().getName(),true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AppManager.getAppManager().orderActivity(BaseActivity.this.getClass().getName(),false);
    }
}
