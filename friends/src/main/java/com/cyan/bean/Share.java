package com.cyan.bean;

/**
 * Created by Administrator on 2015/11/2.
 */
public class Share {
    private int id;
    private String img;
    private int userId;
    private String addTime;
    private int click;
    private String userImg;
    private String userName;
 

    public Share() {
        super();
        // TODO 自动生成的构造函数存根
    }
    public Share(int id, String img, int userId, String addTime, int click) {
        super();
        this.id = id;
        this.img = img;
        this.userId = userId;
        addTime = addTime;
        this.click = click;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getAddTime() {
        return addTime;
    }
    public void setAddTime(String addTime) {
        addTime = addTime;
    }
    public int getClick() {
        return click;
    }
    public void setClick(int click) {
        this.click = click;
    }
    public String getUserImg() {
        return userImg;
    }
    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }


}
