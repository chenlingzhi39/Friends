package com.example.listener;

import com.example.widget.ObservableScrollView;

/**
 * Created by Administrator on 2016/1/29.
 */
public interface ScrollViewListener {
    void onScrollChanged(ObservableScrollView observableScrollView,int x,int y,int oldx,int oldy);
}
