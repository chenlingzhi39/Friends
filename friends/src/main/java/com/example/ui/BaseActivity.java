package com.example.ui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.administrator.myapplication.R;
import com.example.util.RxBus;
import com.example.util.SPUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2015/10/30.
 */
public class BaseActivity extends AppCompatActivity {
    Toast mToast;
    public static String TAG = "bmob";
    ProgressDialog dialog,pd;
    String url;
    public BmobFile imageFile;
    File f;
    public Subscription subscription,sub;
    private Observable<Boolean> mFinishObservable;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.material_blue_500));*/
        setTheme((boolean)SPUtils.get(this,"settings","night_mode_key",false)? R.style.BaseAppNightTheme_AppNightTheme : R.style.BaseAppTheme_AppTheme);
        mFinishObservable = RxBus.get().register("recreate", Boolean.class);
        mFinishObservable.subscribe(new Action1<Boolean>() {
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
        if (mFinishObservable != null) {
            RxBus.get().unregister("finish", mFinishObservable);
        }
    }
}
