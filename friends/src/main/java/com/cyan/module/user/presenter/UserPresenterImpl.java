package com.cyan.module.user.presenter;

import android.content.Context;

import com.cyan.bean.User;
import com.cyan.module.user.model.UserModel;
import com.cyan.module.user.model.UserModelImpl;
import com.cyan.module.user.view.UserView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/3/4.
 */
public class UserPresenterImpl implements UserPresenter<User>{
private UserView userView;
private Context context;
private UserModel<User> userModel;
private SaveListener saveListener=new SaveListener() {
    @Override
    public void onStart() {
        userView.showCircleDialog();
    }

    @Override
    public void onSuccess() {
        userView.toastSendSuccess();
    }

    @Override
    public void onFailure(int code, String msg) {
        userView.toastSendFailure(code,msg);
    }

    @Override
    public void onFinish() {
        userView.dismissCircleDialog();
    }
};


    public UserPresenterImpl(UserView userView, Context context) {
        userModel=new UserModelImpl();
        this.userView = userView;
        this.context = context;
    }

    @Override
    public void login(User user) {
   userModel.login(context, user, saveListener);
    }

    @Override
    public void signUp(User user) {
   userModel.signUp(context,user,saveListener);
    }

    @Override
    public void update(User user) {
        userModel.update(context, user, new UpdateListener() {
            @Override
            public void onStart() {
                userView.showCircleDialog();
            }

            @Override
            public void onSuccess() {
                userView.toastSendSuccess();
            }

            @Override
            public void onFailure(int code, String msg) {
                userView.toastSendFailure(code,msg);
            }

            @Override
            public void onFinish() {
                userView.dismissCircleDialog();
            }
        });
    }

    @Override
    public void getUser(BmobQuery query) {
        userModel.getUser(context, query, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {

            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onFinish() {

            }
        });
    }
}
