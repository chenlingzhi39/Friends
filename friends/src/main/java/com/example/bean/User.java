package com.example.bean;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2015/9/25.
 */

public class User extends BmobUser  {
private String gender;
private String intro;
private List<String> collect_post_id;
private Integer focus_num,fans_num,post_num;
private BmobFile background;

    public BmobFile getBackground() {
        return background;
    }

    public void setBackground(BmobFile background) {
        this.background = background;
    }

    public Integer getPost_num() {
        return post_num;
    }

    public void setPost_num(Integer post_num) {
        this.post_num = post_num;
    }

    public Integer getFans_num() {
        return fans_num;
    }

    public void setFans_num(Integer fans_num) {
        this.fans_num = fans_num;
    }

    public Integer getFocus_num() {
        return focus_num;
    }

    public void setFocus_num(Integer focus_num) {
        this.focus_num = focus_num;
    }

    public List<String> getCollect_post_id() {
        return collect_post_id;
    }

    public void setCollect_post_id(List<String> collect_post_id) {
        this.collect_post_id = collect_post_id;
    }

    public BmobFile getHead() {
        return head;
    }

    public void setHead(BmobFile head) {
        this.head = head;
    }

    private BmobFile head;
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    private String sex;

    private Integer age;
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
