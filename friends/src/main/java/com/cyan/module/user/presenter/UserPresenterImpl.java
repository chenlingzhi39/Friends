package com.cyan.module.user.presenter;

import android.content.Context;

import com.cyan.bean.User;
import com.cyan.module.user.model.UserModel;
import com.cyan.module.user.model.UserModelImpl;
import com.cyan.module.user.view.GetUserView;
import com.cyan.module.user.view.SendUserView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/3/4.
 */
public class UserPresenterImpl implements UserPresenter<User>{
private SendUserView sendUserView;
    private GetUserView getUserView;
private Context context;
private UserModel<User> userModel;
    private boolean first=true;
private SaveListener saveListener=new SaveListener() {
    @Override
    public void onStart() {
        sendUserView.showCircleDialog();
    }

    @Override
    public void onSuccess() {
        sendUserView.toastSendSuccess();
    }

    @Override
    public void onFailure(int code, String msg) {
        sendUserView.toastSendFailure(code,msg);
    }

    @Override
    public void onFinish() {
        sendUserView.dismissCircleDialog();
    }
};


    public UserPresenterImpl(SendUserView sendUserView, Context context) {
        userModel=new UserModelImpl();
        this.sendUserView = sendUserView;
        this.context = context;
    }

    public UserPresenterImpl(Context context, GetUserView getUserView) {
        userModel=new UserModelImpl();
        this.context = context;
        this.getUserView = getUserView;
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
                sendUserView.showCircleDialog();
            }

            @Override
            public void onSuccess() {
                sendUserView.toastSendSuccess();
            }

            @Override
            public void onFailure(int code, String msg) {
                sendUserView.toastSendFailure(code,msg);
            }

            @Override
            public void onFinish() {
                sendUserView.dismissCircleDialog();
            }
        });
    }

    @Override
    public void getUser(BmobQuery query) {
        userModel.getUser(context, query, new FindListener<User>() {
            @Override
            public void onStart() {
                getUserView.showProgress(first);
            }

            @Override
            public void onSuccess(List<User> list) {
                if (first && list.size() == 0)
                {getUserView.showEmpty();
                    return;
                }
                getUserView.addUsers(list);
                getUserView.showRecycler();
            }


            @Override
            public void onError(int i, String s) {
               getUserView.showError();
            }

            @Override
            public void onFinish() {
              getUserView.stopLoadmore();
                getUserView.stopRefresh();
            }
        });
    }
}
