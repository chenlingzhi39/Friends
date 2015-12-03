package com.example.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2015/11/24.
 */
public class Reply extends BmobObject {
    private String content;
    private int reply_id;
    private User user;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReply_id() {
        return reply_id;
    }

    public void setReply_id(int reply_id) {
        this.reply_id = reply_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
