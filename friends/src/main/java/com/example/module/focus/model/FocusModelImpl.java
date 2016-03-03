package com.example.module.focus.model;

import android.content.Context;

import com.example.bean.Focus;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/2/26.
 */
public class FocusModelImpl implements FocusModel{
    @Override
    public void loadFocus(Context context, BmobQuery<Focus> query, final LoadFocusListener loadFocusListener) {
        query.setLimit(10);
        query.order("-id");
        query.include("user,focus_user");
       query.findObjects(context, new FindListener<Focus>() {
           @Override
           public void onSuccess(List<Focus> list) {
               loadFocusListener.onSuccess(list);
           }

           @Override
           public void onError(int i, String s) {
              loadFocusListener.onError(i,s);
           }

           @Override
           public void onFinish() {
             loadFocusListener.onFinish();
           }
       });
    }

    @Override
    public void sendFocus(Context context, Focus focus, SendFocusListener sendFocusListener) {

    }

    public interface LoadFocusListener {
        void onSuccess(List<Focus> list);
        void onError(int i, String s);
        void onFinish();
    }
    public interface SendFocusListener {
        void onSuccess(Focus focus);
        void onFailure(int i, String s);
        void onFinish();
    }
}
