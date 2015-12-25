package com.example.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.common.Constants;
import com.example.util.ContextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/24.
 */
public class TuyaView extends View {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;// 用来画图形的，例如可以用他画一个三角形等等
    private Paint mBitmapPaint;// 画布的画笔
    private Paint mPaint;// 真实的画笔
    private float mX, mY;// 临时点坐标
    private static final float TOUCH_TOLERANCE = 4;// 路径纪录长度，例如在滑动中，点xy的坐标超过临时点坐标的这个值，就画线

    // 保存Path路径的集合,用List集合来模拟栈
    private static List<DrawPath> savePath;
    // 记录Path路径的对象
    private DrawPath dp;

    private boolean isInit = false;// 用来标记保证只被初始化一次
    public static final int LINE_MODE = 1;
    public static final int ERASER_MODE = 4;
    public static int curMode = 1;
    private int size = 5;
    private int color = Color.BLACK;
    private int bgColor = Color.WHITE;
    // 布局高和宽
    private int screenWidth = 500, screenHeight = 500;

    public TuyaView(Context context) {
        super(context);
    }

    public TuyaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TuyaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMode(int mode) {
        switch (mode) {
            case 4:
                mPaint = new Paint();
                mPaint.setColor(bgColor);
                mPaint.setAntiAlias(true);
                mPaint.setDither(true);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(this.size);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeCap(Paint.Cap.SQUARE);


                break;
            case 1:
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setDither(true);
                mPaint.setColor(this.color);
                mPaint.setStrokeWidth(this.size);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                break;
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void init(int w, int h) {
        // 保证只被初始化一次就够了
        if (!isInit) {
            screenWidth = w;
            screenHeight = h;

            mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                    Bitmap.Config.ARGB_8888);

            // 保存一次一次绘制出来的图形
            mCanvas = new Canvas(mBitmap);

            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
            mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
            mPaint.setStrokeWidth(size);// 画笔宽度

            savePath = new ArrayList<DrawPath>();
            isInit = true;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        int color = Color.WHITE;
        canvas.drawColor(color);

        // 将前面已经画过得显示出来
        canvas.drawBitmap(mBitmap, 0, 0, null);
        if (mPath != null) {
            // 实时的显示
            canvas.drawPath(mPath, mPaint);
        }
    }

    // 布局的大小改变时，就会调用该方法
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // 布局的大小改变时，就会调用该方法，在这里获取到view的高和宽
        screenWidth = w;
        screenHeight = h;

        // view初始化
        init(screenWidth, screenHeight);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 撤销上一步画线<br />
     * 撤销的核心思想就是将画布清空，将保存下来的Path路径最后一个移除掉，重新将路径画在画布上面。
     */
    public void undo() {
        if (savePath != null && savePath.size() > 0) {
            savePath.remove(savePath.size() - 1);
            redrawOnBitmap();
        }
    }

    /**
     * 重做,就是清空所有的画线<br />
     * 核心思想就是，清空Path路径后，进行重新绘制
     */
    public void redo() {
        if (savePath != null && savePath.size() > 0) {
            savePath.clear();
            redrawOnBitmap();

        }
    }

    public void clear() {
        savePath.clear();
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
        invalidate();
    }

    // 按下
    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    // 正在滑动中
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(mY - y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // 从（mX,mY）到（x,y）画一条贝塞尔曲线，更平滑(直接用mPath.lineTo也是可以的)
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    // 滑动太手
    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        // 将一条完整的路径保存下来(相当于入栈操作)
        savePath.add(dp);
        mPath = null;// 重新置空
    }

    // 重新绘制Path中的画线路径
    private void redrawOnBitmap() {
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布

        for (DrawPath drawPath : savePath) {
            mCanvas.drawPath(drawPath.path, drawPath.paint);
        }

        invalidate();// 刷新
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 每次down下去重新new一个Path
                mPath = new Path();
                // 每一次记录的路径对象是不一样的
                dp = new DrawPath();
                dp.path = mPath;
                dp.paint = mPaint;
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }

        return true;
    }

    /**
     * 保存图片
     */
    public boolean saveToSDCard(String filePath) {
        if (!ContextUtils.hasSdCard()) {
            return false;
        }

        try {
            File file = new File(filePath);
            createParentDirs(file);

            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            // FileUtils.saveFileByBitmap(filePath, mBitmap);
        } catch (Exception e) {
            Log.e(Constants.TAG, "", e);
            return false;
        }

        return true;
    }

    // 如果父目录不存在，则创建之
    private static void createParentDirs(File file) {
        File parentPath = file.getParentFile();
        if (!parentPath.exists() || !parentPath.isDirectory()) {
            parentPath.mkdirs();
        }
    }

    /**
     * 每条路径封装类
     *
     * @author xuan
     */
    private class DrawPath {
        public Path path;// 路径
        public Paint paint;// 画笔
    }

}

