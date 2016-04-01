package com.cyan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.cyan.listener.InputWindowListener;

/**
 * Created by Administrator on 2016/4/1.
 */
public class IMMListenerRelativeLayout extends RelativeLayout {
    private InputWindowListener listener;
   private boolean first=true;
    public IMMListenerRelativeLayout(Context context) {
        super(context);
    }

    public IMMListenerRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IMMListenerRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(!first)
        if (oldh > h) {
            listener.show();
        } else{
            listener.hide();
        }
        if (first)first=false;
    }

    public void setListener(InputWindowListener listener) {
        this.listener = listener;
    }

}
