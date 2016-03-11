package com.cyan.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/2/1.
 */
public class Focus extends BmobObject{
    User user;
    User focus_user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer id;
    public User getFocusUser() {
        return focus_user;
    }

    public void setFocusUser(User focus_user) {
        this.focus_user = focus_user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
