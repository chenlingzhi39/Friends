package com.cyan.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cyan.App.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/10/30.
 */
public class ActivityUtil {
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
    public static float[] getBitmapConfiguration(Bitmap bitmap, ImageView imageView, float screenRadio) {
        int screenWidth=ActivityUtil.getScreenSize()[0];
        float rawWidth=0;
        float rawHeight=0;
        float width=0;
        float height=0;
        if(bitmap == null) {
            // rawWidth = sourceWidth;
            // rawHeight = sourceHeigth;
            width=(float)(screenWidth / screenRadio);
            height=(float)width;
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            rawWidth=bitmap.getWidth();
            rawHeight=bitmap.getHeight();
            if(rawHeight > 10 * rawWidth) {
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            } else {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            float radio=rawHeight / rawWidth;

            width=(float)(screenWidth / screenRadio);
            height=(float)(radio * width);
        }
        return new float[]{width, height};
    }
    public static int[] getScreenSize() {
        int[] screens;
        // if (Constants.screenWidth > 0) {
        // return screens;
        // }
        DisplayMetrics dm=new DisplayMetrics();
        dm= MyApplication.getInstance().getResources().getDisplayMetrics();
        screens=new int[]{dm.widthPixels, dm.heightPixels};
        return screens;
    }
    public static void showKeyboard(final EditText editText){
        //自动弹出软键盘
        Timer timer = new Timer();

        timer.schedule(new TimerTask()

                       {

                           public void run()

                           {

                               InputMethodManager inputManager =

                                       (InputMethodManager) editText.getContext().getSystemService(
                                               Context.INPUT_METHOD_SERVICE);

                               inputManager.showSoftInput(editText, 0);

                           }

                       },

                500);
    }
}
