package com.cyan.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2016/1/29.
 */
public class AlphaView extends View{
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap,int width,int height) {

        int bitmapWidth =bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        Log.i("Width",width+"");
        Log.i("Height",height+"");
        float bitmapScale = (float) bitmapWidth / (float) bitmapHeight;
        float scale = (float) width / (float) height;
        if(bitmapScale>scale){
            bitmap=Bitmap.createScaledBitmap(bitmap,(int)((float)height*bitmapScale),height,true);
            this.bitmap=Bitmap.createBitmap(bitmap,(bitmap.getWidth()-getMeasuredWidth())/2 ,bitmap.getHeight()-getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
        }else{
            bitmap=Bitmap.createScaledBitmap(bitmap,width,(int)((float)width/bitmapScale),true);
            this.bitmap=Bitmap.createBitmap(bitmap, 0, bitmap.getHeight()-getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
        }
        bitmap.recycle();
        invalidate();
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
        invalidate();
    }

    private int alpha=0;
    public AlphaView(Context context)
    {
        super(context);
        setBackgroundColor(Color.WHITE);
    }

    public AlphaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setAlpha(alpha);
        if(bitmap!=null){
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
      else{canvas.drawColor(Color.TRANSPARENT);}
     }

}
