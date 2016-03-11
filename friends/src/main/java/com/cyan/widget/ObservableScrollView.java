package com.cyan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
import com.cyan.listener.ScrollViewListener;
/**
 * Created by Administrator on 2016/1/29.
 */
public class ObservableScrollView extends ScrollView {
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    private ScrollViewListener scrollViewListener;
    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}
