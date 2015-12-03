package com.example.ui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.myapplication.R;
import com.example.manager.SystemBarTintManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2015/10/30.
 */
public class BaseActivity extends AppCompatActivity{
    Toast mToast;
    public static String TAG = "bmob";
    ProgressDialog dialog;
    String url;
    public BmobFile imageFile;
    File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.material_blue_500));

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
    public  void saveBitmap(Bitmap bitmap) {
        f = new File(Environment.getExternalStorageDirectory()+"/friends/", "head.jpg");
        Toast.makeText(getApplicationContext(), f.getPath(), Toast.LENGTH_SHORT).show();
        if(!f.getParentFile().exists()){
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

    /**
     * 创建操作
     * insertObject
     *
     * @return void
     * @throws
     */
    public void insertObject(final BmobObject obj) {
        obj.save(getApplicationContext(), new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                showToast("-->创建数据成功：" + obj.getObjectId());
               finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                showToast("-->创建数据失败：" + arg0 + ",msg = " + arg1);
            }
        });
    }



}
