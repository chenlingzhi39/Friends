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

package com.example.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.widget.action.Action;
import com.example.widget.action.CircleAction;
import com.example.widget.action.DeepEraserAction;
import com.example.widget.action.EraserAction;
import com.example.widget.action.LineAction;
import com.example.widget.action.RectAction;
import com.example.widget.action.TextAction;

import java.util.LinkedList;

public class DoodleView extends View {
    public abstract class Mode {
        public static final int NONE_MODE = 0;
        public static final int LINE_MODE = 1;
        public static final int RECT_MODE = 2;
        public static final int CIRCLE_MODE = 3;
        public static final int ERASER_MODE = 4;
        public static final int DEEP_ERASER_MODE = 5;
        public static final int TEXT_MODE = 6;
    }

    private Bitmap background = null; // 背景图片
    private Bitmap surfaceBitmap = null; // 实际绘画的图片

    // private boolean ifLoop = true; // 绘画线程结束标志位
    private LinkedList<Action> actionList = null; // 绘画动作的列表
    private Action curAction = null;
    // private SurfaceHolder holder = null;
    // private Paint paint = null;

    private static final int DEFAULT_TEXT_COLOR = Color.YELLOW;
    private static final int DEFAULT_LINE_SIZE = 5;
    private static final int DEFAULT_TEXT_SIZE = 35;
    private static final int DEFAULT_LINE_COLOR = Color.YELLOW;
    private int curMode = 0;
    private int curIndex = 0; // actionList的最大绘画index，用于前进后退
    private int color = DEFAULT_TEXT_COLOR; // 画笔的颜色
    private float size = DEFAULT_LINE_SIZE; // 画笔的大小
    private int backgroundColor = Color.WHITE;

    private static final int DEFAULT_DRAW_MODE = 1;

    Handler mHandler = new Handler();
    private int mCurAdjustState;
    private Runnable mCurRunnable;
    private static final int NOT_DRAGING = 0;
    private static final int MOVING = 9;
    private static final int MOVING_FINISHED = 10;
    private static final int RESIZING = 11;
    private static final int DRAGING = 12;
    private static final int ADJUSTING_DELAY = 1000;
    private static final int DRAGING_DELEGATE = 5;
    private float dragStartX;
    private float dragStartY;
    private float dragCurX;
    private float dragCurY;
    private float dragActionStartX;
    private float dragActionStartY;
    private float dragActionEndX;
    private float dragActionEndY;
    private float dragOriLen;
    private float originTextSize;

    // 放大缩小使用的变量
    private int multiMode = 0;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private PointF startPoint = new PointF();
    private PointF endPoint = new PointF();
    private PointF midPoint = new PointF();
    private float oldDis = 0;
    private float newDis = 0;
    private boolean ifEnableDragAndZoom = false;
    private static final float POINTER_DIS_LIMIT_MIN = 230;
    private static final float POINTER_DIS_LIMIT_MAX = 280;

    private float scale = 1;
    private float totalScale = 1;
    private Matrix tempMatrix = new Matrix();
    private Matrix saveMatrix = new Matrix();
    private float xTranslateDis = 0;
    private float yTranslateDis = 0;
    private float xMoveDis = 0;
    private float yMoveDis = 0;

    // private static final int MAX_SIZE = 10;
    private static final String LOG_TAG = "Ragnarok";

    private int backgroundOriginWidth = 0;
    private int backgroundOriginHeight = 0;

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public DoodleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    public DoodleView(Context context) {
        super(context);
        this.background = background;
        actionList = new LinkedList<Action>();
        this.curIndex = actionList.size();
        this.setFocusable(true);
        ifEnableDragAndZoom = false;
        scale = 1;
        curMode = DEFAULT_DRAW_MODE;
/*
        if(background!=null)
        {surfaceBitmap = Bitmap.createBitmap(background.getWidth(),
                background.getHeight(), Bitmap.Config.ARGB_8888);
        this.backgroundOriginHeight = background.getHeight();
        this.backgroundOriginWidth = background.getWidth();}
*/

    }

    public int getMode() {
        return curMode;
    }

    public void setMode(int mode) {
        this.curMode = mode;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setEnableDragAndZoom(boolean ifEnableDragAndZoom) {
        this.ifEnableDragAndZoom = ifEnableDragAndZoom;
    }

    public void setBrushSize(float size) {
        this.size = size;
    }

    public void setBackground(Bitmap background) {
        this.background = background;
    }

    public void clear() {
        this.actionList.clear();
        this.curIndex = this.actionList.size();
        invalidate();
    }

    private void addAction(Action action) {
        actionList.add(action);
        curIndex = curIndex + 1 > actionList.size() ? actionList.size()
                : curIndex + 1;
    }

    public void rollBack() {
        curIndex = curIndex - 1 < 0 ? 0 : curIndex - 1;
        if (actionList.size() > 0)
            actionList.removeLast();
        invalidate();
    }

    public boolean ifClear() {
        return actionList.size() > 0 ? false : true;
    }

    public void goForward() {
        curIndex = curIndex + 1 > actionList.size() ? actionList.size()
                : curIndex + 1;
        invalidate();
    }

    public void Draw(Canvas canvas) {
        canvas.drawColor(backgroundColor);
        canvas.setMatrix(saveMatrix);
        if (background != null) {
            canvas.drawBitmap(background, 0, 0, null);
            this.surfaceBitmap = Bitmap.createBitmap(background.getWidth(),
                    background.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas surfaceCanvas = new Canvas(surfaceBitmap);

            surfaceCanvas.drawColor(Color.TRANSPARENT);

            for (int i = 0; i < this.curIndex; i++) {
                this.actionList.get(i).draw(surfaceCanvas);
            }

            if (this.curAction != null)
                curAction.draw(surfaceCanvas);

            canvas.drawBitmap(surfaceBitmap, 0, 0, null);
        } else {

            for (int i = 0; i < this.curIndex; i++) {
                this.actionList.get(i).draw(canvas);
            }

            if (this.curAction != null)
                curAction.draw(canvas);
        }
    }

    private void setCurAction(float x, float y, float size, int color) {
        if (curAction != null) {
            mHandler.removeCallbacks(mRunnable);
            mRunnable.run();
        }
        switch (this.curMode) {
            case DoodleView.Mode.CIRCLE_MODE:
                curAction = new CircleAction(x, y, size, color);
                break;
            case DoodleView.Mode.LINE_MODE:
                curAction = new LineAction(x, y, size, color);
                Log.d(LOG_TAG, "curAction is line mode");
                break;
            case DoodleView.Mode.RECT_MODE:
                curAction = new RectAction(x, y, size, color);
                break;
            case DoodleView.Mode.ERASER_MODE:
                curAction = new EraserAction(x, y, size, color);
                break;
            case DoodleView.Mode.DEEP_ERASER_MODE:
                curAction = new DeepEraserAction(x, y, size, this.backgroundColor);
                break;
            default:
                curAction = null;
                Log.d(LOG_TAG, "curAction is null");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();

        float drawX = x;
        float drawY = y;

        if (mCurRunnable != null) {
            if (event.getPointerCount() == 1) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mHandler.removeCallbacks(mRunnable);
                        if (mCurAdjustState == RESIZING) {
                            return true;
                        }
//					mCurAdjustState = DRAGING;
                        dragCurX = dragActionStartX = drawX;
                        dragCurY = dragActionStartY = drawY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mCurAdjustState == RESIZING) {
                            return true;
                        } else if (mCurAdjustState != DRAGING
                                && (Math.abs(drawX - dragActionStartX) > DRAGING_DELEGATE
                                || Math.abs(drawY - dragActionStartY) > DRAGING_DELEGATE)) {
                            mCurAdjustState = DRAGING;
                        } else if (mCurAdjustState != DRAGING) {
                            return true;
                        }
                        curAction.replace(drawX - dragCurX, drawY - dragCurY);
                        dragCurX = drawX;
                        dragCurY = drawY;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
//					if( mCurAdjustState==DRAGING )
//						curAction.replace(drawX - dragCurX, drawY - dragCurY);
                        mCurAdjustState = 0;
                        cancelLongPress();
                        mHandler.postDelayed(mRunnable, ADJUSTING_DELAY);
                        break;
                }
            } else if (event.getPointerCount() == 2) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mCurAdjustState = RESIZING;
                        float x0 = event.getX(0);
                        float x1 = event.getX(1);
                        float y0 = event.getY(0);
                        float y1 = event.getY(1);
                        dragStartX = drawX;
                        dragStartY = drawY;
                        dragActionStartX = curAction.startX;
                        dragActionStartY = curAction.startY;
                        dragActionEndX = curAction.endX;
                        dragActionEndY = curAction.endY;
                        dragOriLen = getDistance(x0, y0, x1, y1);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mCurAdjustState != RESIZING) {
                            mCurAdjustState = RESIZING;
                            x0 = event.getX(0);
                            x1 = event.getX(1);
                            y0 = event.getY(0);
                            y1 = event.getY(1);
                            dragStartX = drawX;
                            dragStartY = drawY;
                            dragActionStartX = curAction.startX;
                            dragActionStartY = curAction.startY;
                            dragActionEndX = curAction.endX;
                            dragActionEndY = curAction.endY;
                            dragOriLen = getDistance(x0, y0, x1, y1);
                            if (curAction instanceof TextAction) {
                                originTextSize = ((TextAction) curAction).getTextSize();
                            }
                        } else {
                            x0 = event.getX(0);
                            x1 = event.getX(1);
                            y0 = event.getY(0);
                            y1 = event.getY(1);
                            float curLen = getDistance(x0, y0, x1, y1);
                            float scale = curLen / dragOriLen;
                            scaleAction(scale);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        cancelLongPress();
//					curAction.replace(drawX - dragCurX, drawY - dragCurY);
                        mCurAdjustState = 0;
                        mHandler.postDelayed(mRunnable, ADJUSTING_DELAY);
                    default:
                        break;
                }
            } else {
                Log.e(LOG_TAG, "Too many pointer..");
                cancelLongPress();
            }
            invalidate();
            return true;
        } else {
            if (event.getPointerCount() == 1) {
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        this.setCurAction(drawX, drawY, this.size, this.color);
//					Log.d(LOG_TAG,
//							"action down, pointer count is "
//									+ event.getPointerCount());
                        mCurAdjustState = MOVING;
                        if (this.ifEnableDragAndZoom) {
                            this.multiMode = DRAG;
                            startPoint.set(x, y);
                            tempMatrix.set(saveMatrix);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (curAction != null) {
                            curAction.move(drawX, drawY);
//						Log.d(LOG_TAG, "move to " + x + ", " + y);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (curAction == null)
                            break;
                        curAction.move(drawX, drawY);
                        if (curAction instanceof RectAction || curAction instanceof CircleAction) {
                            mCurRunnable = mRunnable;
                            mHandler.postDelayed(mRunnable, ADJUSTING_DELAY);
                        } else {
                            addToActionList();
                        }
                        break;
                }
                invalidate();
                return true;
            } else if (event.getPointerCount() >= 2 && ifEnableDragAndZoom) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        if (this.multiMode == DRAG) {
                            xMoveDis = xMoveDis + xTranslateDis;
                            yMoveDis = yMoveDis + yTranslateDis;
                            Log.d(LOG_TAG, "xMoveDis = " + xMoveDis
                                    + ", yMoveDis = " + yMoveDis);
                        } else if (this.multiMode == ZOOM) {
                            totalScale = totalScale * scale;
                            Log.d(LOG_TAG, "totalScale = " + totalScale);
                        }
                        this.multiMode = NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d(LOG_TAG, "action_pointer_down");
                        oldDis = getDistance(
                                new PointF(event.getX(0), event.getY(0)),
                                new PointF(event.getX(1), event.getY(1)));
                        Log.d(LOG_TAG, "oldDis is " + oldDis);
                        if (POINTER_DIS_LIMIT_MIN < oldDis
                                && oldDis > POINTER_DIS_LIMIT_MAX) {
                            this.multiMode = ZOOM;
                            Log.d(LOG_TAG, "set to zoom mode");
                            tempMatrix.set(saveMatrix);
                            midPoint = getMidPoint(midPoint, new PointF(x, y));
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (this.multiMode == DRAG) {
                            // Log.d(LOG_TAG, "drag the view");
                            xTranslateDis = x - startPoint.x;
                            yTranslateDis = y - startPoint.y;
                            Log.d(LOG_TAG, "xTranslateDis=" + xTranslateDis
                                    + ", yTranslateDis=" + yTranslateDis);

                            saveMatrix.set(tempMatrix);
                            saveMatrix.postTranslate(x - startPoint.x, y
                                    - startPoint.y);
                        } else if (this.multiMode == ZOOM) {
                            newDis = getDistance(
                                    new PointF(event.getX(0), event.getY(0)),
                                    new PointF(event.getX(1), event.getY(1)));
                            Log.d(LOG_TAG, "newDis is " + newDis);
                            if (POINTER_DIS_LIMIT_MIN < newDis
                                    && newDis > POINTER_DIS_LIMIT_MAX) {
                                scale = newDis / oldDis;
                                saveMatrix.set(tempMatrix);
                                saveMatrix.postScale(scale, scale, midPoint.x,
                                        midPoint.y);
                                Log.d(LOG_TAG, "scale is " + scale);
                            }
                        }
                        break;
                }
                invalidate();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            addToActionList();
            cancelLongPress();
            mCurRunnable = null;
        }
    };

    private void addToActionList() {
        if (curAction != null) {
            // Log.d(LOG_TAG, "move to " + x + ", " + y);
            curAction.finishAdjusting();
            this.addAction(curAction);
            curAction = null;
            mCurAdjustState = 0;
            invalidate();
        }

    }

    private PointF getDrawPoint(float x, float y, float xMoveDis,
                                float yMoveDis, PointF zoomMidPoint, float scale) {
        float drawX = x - xMoveDis;
        float drawY = y - yMoveDis;

        float dis = getDistance(new PointF(x, y), zoomMidPoint);
        Log.d("dis", "dis = " + dis);
        float scaleDis = dis * scale;
        Log.d("dis", "scaleDis = " + scaleDis);

        // if (scale != 1) {
        // drawX = drawX + scaleDis;
        // drawY = drawY + scaleDis;
        // }
        Log.d("dis", "zoomMidPoint.x = " + zoomMidPoint.x
                + ", zoomMidPoint.y = " + zoomMidPoint.y);
        return new PointF(drawX, drawY);
    }

    private float getDistance(PointF p1, PointF p2) {
        float xDis = Math.abs(p1.x - p2.x);
        float yDis = Math.abs(p2.y - p1.y);

        return (float) Math.sqrt(xDis * xDis + yDis * yDis);
    }

    private float getDistance(float x0, float y0, float x1, float y1) {
        return (float) Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
    }

    private void scaleAction(float scale) {
        if (curAction instanceof TextAction) {
            ((TextAction) curAction).setTextSize(originTextSize * scale);
        } else {
            float left = dragActionEndY - dragActionStartY;
            float up = dragActionEndX - dragActionStartX;
            left = left * (1 - scale);
            up = up * (1 - scale);
            float sx = dragActionStartX + up;
            float ex = dragActionEndX - up;
            float sy = dragActionStartY + left;
            float ey = dragActionEndY - left;
            curAction.resize(sx, sy, ex, ey);
        }
    }

    private PointF getMidPoint(PointF p1, PointF p2) {
        return new PointF((p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Draw(canvas);
    }

    public void addTextAction(String text) {
        curAction = new TextAction(text, DEFAULT_TEXT_SIZE, DEFAULT_TEXT_COLOR, getWidth() / 2, getHeight() / 2);
        invalidate();
        mHandler.postDelayed(mRunnable, ADJUSTING_DELAY * 2);
        mCurAdjustState = 0;
        mCurRunnable = mRunnable;
    }
}
