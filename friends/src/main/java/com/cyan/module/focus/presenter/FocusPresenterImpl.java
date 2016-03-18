package com.cyan.module.focus.presenter;

import android.content.Context;

import com.cyan.bean.Focus;
import com.cyan.module.focus.model.FocusModel;
import com.cyan.module.focus.model.FocusModelImpl;
import com.cyan.module.focus.view.FocusView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/2/26.
 */
public class FocusPresenterImpl implements FocusPresenter{
private Context context;
    private FocusView focusView;
    private boolean first=true;
private FocusModel<Focus> focusModel;
    public FocusPresenterImpl(Context context, FocusView focusView) {
        this.context = context;
        this.focusView = focusView;
        focusModel=new FocusModelImpl();
    }

    @Override
    public void loadFocus(BmobQuery<Focus> query) {
        focusModel.loadFocus(context, query, new FindListener<Focus>() {
            @Override
            public void onStart() {
                focusView.showProgress();
            }

            @Override
            public void onSuccess(List<Focus> list) {
                focusView.addFocus(list);
                if (first && list.size() == 0)
                    focusView.showEmpty();
                if (first) {
                    focusView.showRecycler();
                    first = false;
                }
            }

            @Override
            public void onError(int i, String s) {
                focusView.showError();
            }

            @Override
            public void onFinish() {
                focusView.stopLoadmore();
            }
        });
    }

}
