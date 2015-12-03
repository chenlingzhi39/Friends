package com.example.administrator.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import android.view.animation.OvershootInterpolator;

/**
 * Created by Administrator on 2015/8/13.
 */
public class SplashView extends View {

    //大圆(里面包含很多小圆)的半径
    private float mRotationRadius = 90;
    //每个小圆的半径
    private float mCircleRatius = 18;
    //小圆圈的颜色列表
    private int[] mCircleColors;
    //大圆和小圆的旋转时间
    private long mRotationDuration = 1200;
    //第二部分动画的执行总时间(包括两个时间，各占1/2)
    private long mSplashDuration = 1200;
    //整体的背景颜色
    private int mSplashBgColor = Color.WHITE;

    /**
     * 参数，保存了一些绘制状态，被动态地改变
     *
     * @param context
     */
    //空心圆初始半径
    private float mHoleRadius = 0f;
    //当前大圆旋转角度(弧度)
    private float mCurrentRotationAngle = 0f;
    //当前大圆的半径
    private float nCurrentRotationRatius = mRotationRadius;
    //绘制圆的画笔
    private Paint mPaint = new Paint();
    //绘制背景的画笔
    private Paint mPaintBackground = new Paint();
    //屏幕正中心点坐标
    private float mCenterX;
    private float mCenterY;
    //屏幕对角线一半
    private float mDiagonalDist;
    private SplashState mState = null;

    private abstract class SplashState {
        public abstract void drawState(Canvas canvas);

        public abstract void cancel();
    }

    public SplashView(Context context) {
        super(context);
        //初始化数据
        init(context);
    }

    private void init(Context context) {
        mCircleColors = context.getResources().getIntArray(R.array.splash_circle_colors);
        mPaint.setAntiAlias(true);
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setColor(mSplashBgColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        mDiagonalDist = (float) Math.sqrt(w * w + h * h) / 2;
    }

    //进入主界面---开启后面的两个动画
    public void splashAndDisappear() {
        RotationState rs = (RotationState) mState;
        rs.cancel();
        mState = new MergingState();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设计模式--模板模式
        //这里就做一个简单的绘制事件分发
        if (mState == null) {
            mState = new RotationState();

        }
        mState.drawState(canvas);
    }

    private void drawBackground(Canvas canvas) {
        if (mHoleRadius > 0f) {
            float strokeWidth = mDiagonalDist - mHoleRadius;
            mPaintBackground.setStrokeWidth(strokeWidth);
            float radius=mHoleRadius+strokeWidth/2;
            canvas.drawCircle(mCenterX, mCenterY, radius, mPaintBackground);

        } else {
            canvas.drawColor(mSplashBgColor);
        }
    }

    private void drawCircles(Canvas canvas) {
        float rotationAngle = (float) (2 * Math.PI / mCircleColors.length);
        for (int i = 0; i < mCircleColors.length; i++) {
            double a = mCurrentRotationAngle + rotationAngle * i;
            float cx = (float) (nCurrentRotationRatius * Math.cos(a) + mCenterX);
            float cy = (float) (nCurrentRotationRatius * Math.sin(a) + mCenterY);
            mPaint.setColor(mCircleColors[i]);
            canvas.drawCircle(cx, cy, mCircleRatius, mPaint);
        }
    }

    //旋转动画
    private class RotationState extends SplashState {
        private ValueAnimator mAnimator;

        public RotationState() {
            mAnimator = ValueAnimator.ofFloat(0, (float) Math.PI * 2);
            mAnimator.setDuration(mRotationDuration);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotationAngle = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.start();
        }

        ;

        @Override
        public void drawState(Canvas canvas) {
            //执行旋转动画
            drawBackground(canvas);
            drawCircles(canvas);
        }

        @Override
        public void cancel() {
            mAnimator.cancel();
        }
    }

    //集合动画
    private class MergingState extends SplashState {
        private ValueAnimator mAnimator;

        public MergingState() {
            mAnimator = ValueAnimator.ofFloat(0, mRotationRadius);
            mAnimator.setDuration(mRotationDuration / 2);
            mAnimator.setInterpolator(new OvershootInterpolator(10));
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    nCurrentRotationRatius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                  mState=new ExpandingState();

                    invalidate();
                }

            });
            mAnimator.reverse();

        }

        @Override
        public void drawState(Canvas canvas) {
            drawBackground(canvas);
            drawCircles(canvas);

        }

        @Override
        public void cancel() {
            mAnimator.cancel();
        }
    }

    //扩散动画
    private class ExpandingState extends SplashState {
        private ValueAnimator mAnimator;
        public ExpandingState(){
            mAnimator = ValueAnimator.ofFloat(0, mDiagonalDist);
            mAnimator.setDuration(mRotationDuration / 2);

            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mHoleRadius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
         mAnimator.start();
        }
        @Override
        public void drawState(Canvas canvas) {
           drawBackground(canvas);
        }

        @Override
        public void cancel() {

        }
    }
}
