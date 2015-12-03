package com.example.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2015/10/30.
 */
public class ActivityUtils {
    public static String TAG="bmob";
   public static void saveBitmap(Bitmap bitmap,File f,Context context) {
        f = new File(Environment.getExternalStorageDirectory()+"/friends/", "head.jpg");
        Toast.makeText(context, f.getPath(), Toast.LENGTH_SHORT).show();
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
    public void showToast(String text,Toast mToast,Context context) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(context, text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }
}
