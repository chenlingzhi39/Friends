package com.example.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/2/1.
 */
public class Focus extends BmobObject{
    User user;
    User focusUser;

    public User getFocusUser() {
        return focusUser;
    }

    public void setFocusUser(User focusUser) {
        this.focusUser = focusUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
