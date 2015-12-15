package com.example.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2015/11/5.
 */
public class Post extends BmobObject{
    private User author;
    private Integer praise_count,comment_count;
    private String user_coordinate;
    private String user_location;
    private Integer id;
    private BmobRelation praises;
    private Boolean is_praised=false;
    private Boolean is_collected=false;
    private List<String> praise_user_id;

    public List<String> getPraise_user_id() {
        return praise_user_id;
    }

    public void setPraise_user_id(List<String> praise_user_id) {
        this.praise_user_id = praise_user_id;
    }

    public Boolean getIs_praised() {
        return is_praised;
    }

    public void setIs_praised(Boolean is_praised) {
        this.is_praised = is_praised;
    }

    public Boolean getIs_collected() {
        return is_collected;
    }

    public void setIs_collected(Boolean is_collected) {
        this.is_collected = is_collected;
    }

    public BmobRelation getPraises() {
        return praises;
    }

    public void setPraises(BmobRelation praises) {
        this.praises = praises;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    private String content;

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    private BmobFile image;


    public void setComment_count(Integer comment_count) {
        this.comment_count = comment_count;
    }



    public Integer getPraise_count() {
        return praise_count;
    }

    public void setPraise_count(Integer praise_count) {
        this.praise_count = praise_count;
    }





    public String getUser_coordinate() {
        return user_coordinate;
    }
    public void setUser_coordinate(String user_coordinate) {
        this.user_coordinate = user_coordinate;
    }
    public String getUser_location() {
        return user_location;
    }
    public void setUser_location(String user_location) {
        this.user_location = user_location;
    }



    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Integer getComment_count() {
        return comment_count;
    }

}
