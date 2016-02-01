package com.example.bean;

import cn.bmob.v3.datatype.BmobFile;

public class Focus {
private int id,userId;
private Boolean special,isFocus;
private BmobFile userImg,img;
private String name,userName,userIntro,intro;



public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getUserId() {
	return userId;
}
public void setUserId(int userId) {
	this.userId = userId;
}
public Boolean getSpecial() {
	return special;
}
public void setSpecial(Boolean special) {
	this.special = special;
}
public BmobFile getUserImg() {
	return userImg;
}
public void setUserImg(BmobFile userImg) {
	this.userImg = userImg;
}
public BmobFile getImg() {
	return img;
}
public void setImg(BmobFile img) {
	this.img = img;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public Boolean getIsFocus() {
	return isFocus;
}
public void setIsFocus(Boolean isFocus) {
	this.isFocus = isFocus;
}
public String getUserIntro() {
	return userIntro;
}
public void setUserIntro(String userIntro) {
	this.userIntro = userIntro;
}
public String getIntro() {
	return intro;
}
public void setIntro(String intro) {
	this.intro = intro;
}

}
