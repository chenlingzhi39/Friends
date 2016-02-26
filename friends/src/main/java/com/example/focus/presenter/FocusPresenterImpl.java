package com.example.focus.presenter;

import android.content.Context;

import com.example.bean.Focus;
import com.example.focus.model.FocusModel;
import com.example.focus.model.FocusModelImpl;
import com.example.focus.view.FocusView;

import java.util.List;

import cn.bmob.v3.BmobQuery;

/**
 * Created by Administrator on 2016/2/26.
 */
public class FocusPresenterImpl implements FocusPresenter,FocusModelImpl.LoadFocusListener{
private Context context;
    private FocusView focusView;
    private boolean first=true;
private FocusModel focusModel;
    public FocusPresenterImpl(Context context, FocusView focusView) {
        this.context = context;
        this.focusView = focusView;
        focusModel=new FocusModelImpl();
    }

    @Override
    public void loadFocus(BmobQuery<Focus> query) {
        focusView.showProgress();
        focusModel.loadFocus(context,query,this);
    }

    @Override
    public void sendFocus(Focus focus) {

    }


    @Override
    public void onSuccess(List<Focus> list) {
        focusView.addFocus(list);
        if (first && list.size() == 0)
            focusView.showEmpty();
        if (first)
        {focusView.showRecycler();
            first=false;}
    }

    @Override
    public void onError(int i, String s) {
       focusView.showError();
    }

    @Override
    public void onFinish() {
       focusView.stopLoadmore();
    }
}
