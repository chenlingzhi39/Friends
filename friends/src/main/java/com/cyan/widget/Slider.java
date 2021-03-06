/*
 * Copyright 2015 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyan.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsoluteLayout;
import android.widget.PopupWindow;

import com.cyan.community.R;
import com.cyan.util.AnimationUtils2;
import com.cyan.util.LayoutUtils;
import com.cyan.util.MathUtils;
import com.cyan.util.SimpleHandler;
import com.vector.VectorDrawable;


public class Slider extends View {

    private static char[] CHARACTERS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private static final int BUBBLE_WIDTH = 26;
    private static final int BUBBLE_HEIGHT = 32;

    private Context mContext;

    private Paint mPaint;
    private Paint mBgPaint;

    private RectF mLeftRectF = new RectF();
    private RectF mRightRectF = new RectF();

    private PopupWindow mPopup;
    private BubbleView mBubble;

    private int mStart;
    private int mEnd;
    private int mProgress;
    private float mPercent;
    private int mDrawProgress;
    private float mDrawPercent;
    private int mTargetProgress;

    private float mThickness;
    private float mRadius;

    private float mCharWidth;
    private float mCharHeight;

    private int mBubbleWidth;
    private int mBubbleHeight;
    private int mBubbleMinWidth;
    private int mBubbleMinHeight;

    private int mPopupX;
    private int mPopupY;
    private int mPopupWidth;

    private int[] mTemp = new int[2];

    private boolean mReverse = false;

    private boolean mShowBubble;

    private float mDrawBubbleScale = 0f;

    private ValueAnimator mProgressAnimation;
    private ValueAnimator mBubbleScaleAnimation;

    private OnSetProgressListener mListener;

    private CheckForShowBubble mCheckForShowBubble;

    public Slider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Slider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressWarnings("deprecation")
    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);

        mBubbleMinWidth = LayoutUtils.dp2pix(context, BUBBLE_WIDTH);
        mBubbleMinHeight = LayoutUtils.dp2pix(context, BUBBLE_HEIGHT);

        mBubble = new BubbleView(context, textPaint);
        mBubble.setScaleX(0.0f);
        mBubble.setScaleY(0.0f);
        AbsoluteLayout absoluteLayout = new AbsoluteLayout(context);
        absoluteLayout.addView(mBubble);
        absoluteLayout.setBackgroundDrawable(null);
        mPopup = new PopupWindow(absoluteLayout);
        mPopup.setOutsideTouchable(false);
        mPopup.setTouchable(false);
        mPopup.setFocusable(false);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Slider);
        textPaint.setColor(a.getColor(R.styleable.Slider_textColor, Color.WHITE));
        textPaint.setTextSize(a.getDimensionPixelSize(R.styleable.Slider_textSize, 12));

        updateTextSize();

        setRange(a.getInteger(R.styleable.Slider_start, 0), a.getInteger(R.styleable.Slider_end, 0));
        setProgress(a.getInteger(R.styleable.Slider_slider_progress, 0));
        mThickness = a.getDimension(R.styleable.Slider_thickness, 2);
        mRadius = a.getDimension(R.styleable.Slider_radius, 6);
        setColor(a.getColor(R.styleable.Slider_color, Color.BLACK));

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(a.getBoolean(R.styleable.Slider_dark, false) ? 0x4dffffff : 0x42000000);

        a.recycle();

        mProgressAnimation = new ValueAnimator();
        mProgressAnimation.setInterpolator(AnimationUtils2.FAST_SLOW_INTERPOLATOR);
        mProgressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                mDrawPercent = value;
                mDrawProgress = Math.round(MathUtils.lerp((float) mStart, mEnd, value));
                updateBubblePosition();
                mBubble.setProgress(mDrawProgress);
                invalidate();
            }
        });

        mBubbleScaleAnimation = new ValueAnimator();
        mBubbleScaleAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                mDrawBubbleScale = value;
                mBubble.setScaleX(value);
                mBubble.setScaleY(value);
                invalidate();
            }
        });
    }

    private void updateTextSize() {
        int length = CHARACTERS.length;
        float[] widths = new float[length];
        mPaint.getTextWidths(CHARACTERS, 0, length, widths);
        mCharWidth = 0.0f;
        for (float f : widths) {
            mCharWidth = Math.max(mCharWidth, f);
        }

        Paint.FontMetrics fm = mPaint.getFontMetrics();
        mCharHeight = fm.bottom - fm.top;
    }

    private void updateBubbleSize() {
        int oldWidth = mBubbleWidth;
        int oldHeight = mBubbleHeight;
        mBubbleWidth = (int) Math.max(mBubbleMinWidth,
                Integer.toString(mEnd).length() * mCharWidth + LayoutUtils.dp2pix(mContext, 8));
        mBubbleHeight = (int) Math.max(mBubbleMinHeight,
                mCharHeight + LayoutUtils.dp2pix(mContext, 8));

        if (oldWidth != mBubbleWidth && oldHeight != mBubbleHeight) {
            //noinspection deprecation
            AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) mBubble.getLayoutParams();
            lp.width = mBubbleWidth;
            lp.height = mBubbleHeight;
            mBubble.setLayoutParams(lp);
        }
    }

    private void updatePopup() {
        int width = getWidth();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        getLocationInWindow(mTemp);

        mPopupWidth = (int) (width - mRadius - mRadius + mBubbleWidth);
        int popupHeight = mBubbleHeight;
        mPopupX = (int) (mTemp[0] + mRadius - (mBubbleWidth / 2));
        mPopupY = (int) (mTemp[1] - popupHeight + paddingTop +
                ((getHeight() - paddingTop - paddingBottom) / 2) -
                mRadius -LayoutUtils.dp2pix(mContext, 2));

        mPopup.update(mPopupX, mPopupY, mPopupWidth, popupHeight, false);
    }

    private void updateBubblePosition() {
        float x = ((mPopupWidth - mBubbleWidth) * mDrawPercent);
        mBubble.setX(x);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        updatePopup();
        updateBubblePosition();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mPopup.showAtLocation(this, Gravity.TOP | Gravity.LEFT, mPopupX, mPopupY);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mPopup.dismiss();
    }

    private void startProgressAnimation(float percent) {
        float currentValue;
        if (mProgressAnimation.isRunning()) {
            mProgressAnimation.setCurrentPlayTime(mProgressAnimation.getCurrentPlayTime());
            Object value = mProgressAnimation.getAnimatedValue();
            if (value instanceof Float) {
                currentValue = (float) value;
            } else {
                currentValue = mDrawPercent;
            }
        } else {
            currentValue = mDrawPercent;
        }
        mProgressAnimation.cancel();
        mProgressAnimation.setFloatValues(currentValue, percent);
        mProgressAnimation.setDuration(Math.min(500, (long) (50 * getWidth() * Math.abs(currentValue - percent))));
        mProgressAnimation.start();
    }

    private void startShowBubbleAnimation() {
        mBubbleScaleAnimation.cancel();
        mBubbleScaleAnimation.setFloatValues(mDrawBubbleScale, 1.0f);
        mBubbleScaleAnimation.setInterpolator(AnimationUtils2.FAST_SLOW_INTERPOLATOR);
        mBubbleScaleAnimation.setDuration((long) (300 * Math.abs(mDrawBubbleScale - 1.0f)));
        mBubbleScaleAnimation.start();
    }

    private void startHideBubbleAnimation() {
        mBubbleScaleAnimation.cancel();
        mBubbleScaleAnimation.setFloatValues(mDrawBubbleScale, 0.0f);
        mBubbleScaleAnimation.setInterpolator(AnimationUtils2.SLOW_FAST_INTERPOLATOR);
        mBubbleScaleAnimation.setDuration((long) (300 * Math.abs(mDrawBubbleScale - 0.0f)));
        mBubbleScaleAnimation.start();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        mBubble.setColor(color);
        invalidate();
    }

    public void setRange(int start, int end) {
        mStart = start;
        mEnd = end;
        setProgress(mProgress);

        updateBubbleSize();
    }

    public void setProgress(int progress) {
        progress = MathUtils.clamp(progress, mStart, mEnd);
        if (mProgress != progress) {
            int oldProgress = mProgress;
            mProgress = progress;
            mPercent = MathUtils.delerp(mStart, mEnd, mProgress);
            mTargetProgress = progress;

            if (mProgressAnimation == null) {
                // For init
                mDrawPercent = mPercent;
                mDrawProgress = mProgress;
                updateBubblePosition();
                mBubble.setProgress(mDrawProgress);
            } else {
                startProgressAnimation(mPercent);
            }

            if (mListener != null) {
                mListener.onSetProgress(this, progress, oldProgress, false, true);
            }
            invalidate();
        }
    }

    public int getProgress() {
        return mProgress;
    }

    public void setReverse(boolean reverse) {
        if (mReverse != reverse) {
            mReverse = reverse;
            invalidate();
        }
    }

    public void setOnSetProgressListener(OnSetProgressListener listener) {
        mListener = listener;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if (width < LayoutUtils.dp2pix(mContext, 24)) {
            canvas.drawRect(0, 0, width, getHeight(), mPaint);
        } else {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int paddingRight = getPaddingRight();
            int paddingBottom = getPaddingBottom();
            float thickness = mThickness;
            float radius = mRadius;
            float halfThickness = thickness / 2;

            int saved = canvas.save();

            canvas.translate(0, paddingTop + ((height - paddingTop - paddingBottom) / 2));

            float currentX = paddingLeft + radius + (width - radius - radius - paddingLeft - paddingRight) *
                    (mReverse ? (1.0f - mDrawPercent) : mDrawPercent);

            mLeftRectF.set(paddingLeft + radius, -halfThickness, currentX, halfThickness);
            mRightRectF.set(currentX, -halfThickness, width - paddingRight - radius, halfThickness);

            // Draw bar
            if (mReverse) {
                canvas.drawRect(mRightRectF, mPaint);
                canvas.drawRect(mLeftRectF, mBgPaint);
            } else {
                canvas.drawRect(mLeftRectF, mPaint);
                canvas.drawRect(mRightRectF, mBgPaint);
            }

            // Draw controller
            float scale = 1.0f - mDrawBubbleScale;
            if (scale != 0.0f) {
                canvas.scale(scale, scale, currentX, 0);
                canvas.drawCircle(currentX, 0, radius, mPaint);
            }

            canvas.restoreToCount(saved);
        }
    }

    private void setShowBubble(boolean showBubble) {
        if (mShowBubble != showBubble) {
            mShowBubble = showBubble;
            if (showBubble) {
                startShowBubbleAnimation();
            } else {
                startHideBubbleAnimation();
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int paddingLeft = getPaddingLeft();
                int paddingRight = getPaddingRight();
                float radius = mRadius;
                float x = event.getX();
                int progress = Math.round(MathUtils.lerp((float) mStart, (float) mEnd,
                        MathUtils.clamp((mReverse ? (getWidth() - paddingLeft - radius - x) : (x - radius - paddingLeft)) /
                                (getWidth() - radius - radius - paddingLeft - paddingRight), 0.0f, 1.0f)));
                float percent = MathUtils.delerp(mStart, mEnd, progress);

                // ACTION_CANCEL not changed
                if (action == MotionEvent.ACTION_CANCEL) {
                    progress = mProgress;
                    percent = mPercent;
                }

                if (mTargetProgress != progress) {
                    mTargetProgress = progress;
                    startProgressAnimation(percent);

                    if (mListener != null) {
                        mListener.onSetProgress(this, progress, progress, true, false);
                    }
                }

                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    SimpleHandler.getInstance().removeCallbacks(mCheckForShowBubble);
                    setShowBubble(false);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    if (mCheckForShowBubble == null) {
                        mCheckForShowBubble = new CheckForShowBubble();
                    }
                    SimpleHandler.getInstance().postDelayed(mCheckForShowBubble, ViewConfiguration.getTapTimeout());
                }

                if (action == MotionEvent.ACTION_UP) {
                    if (mProgress != progress) {
                        int oldProgress = mProgress;
                        mProgress = progress;
                        mPercent = mDrawPercent;

                        if (mListener != null) {
                            mListener.onSetProgress(this, progress, oldProgress, true, true);
                        }
                    }
                }
                break;
        }

        return true;
    }

    @SuppressLint("ViewConstructor")
    private static class BubbleView extends View {

        private static final float TEXT_CENTER = (float) BUBBLE_WIDTH / 2.0f / BUBBLE_HEIGHT;

        private VectorDrawable mVectorDrawable;

        private Paint mTextPaint;

        private String mProgressStr = "";

        private Rect mRect = new Rect();

        @SuppressWarnings("deprecation")
        public BubbleView(Context context, Paint paint) {
            super(context);
            mVectorDrawable = VectorDrawable.create(context, R.drawable.slider_bubble);
            setBackgroundDrawable(mVectorDrawable);
            mTextPaint = paint;
        }

        public void setColor(int color) {
            Object obj = mVectorDrawable.getTargetByName("bubble");
            if (obj instanceof VectorDrawable.VFullPath) {
                ((VectorDrawable.VFullPath) obj).setFillColor(color);
            }
        }

        public void setProgress(int progress) {
            String str = Integer.toString(progress);
            if (!str.equals(mProgressStr)) {
                mProgressStr = str;
                mTextPaint.getTextBounds(str, 0, str.length(), mRect);
                invalidate();
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            setPivotX(w / 2);
            setPivotY(h);
        }

        @Override
        protected void onDraw(@NonNull Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            int x = width / 2;
            int y = (int) ((height * TEXT_CENTER) + (mRect.height() / 2));
            canvas.drawText(mProgressStr, x, y, mTextPaint);
        }
    }

    public interface OnSetProgressListener {

        void onSetProgress(Slider slider, int newProgress, int oldProgress, boolean byUser, boolean confirm);
    }

    private final class CheckForShowBubble implements Runnable {

        @Override
        public void run() {
            setShowBubble(true);
        }
    }
}
