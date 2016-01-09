package com.example.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2015/11/5.
 */
public class Post extends BmobObject{
    private User author;
    private Integer praise_count,comment_count;
    private String user_coordinate;
    private String user_location;
    private Integer id;
    private List<String> praise_user_id;


    public void setPraise_user_id(List<String> praise_user_id) {
        this.praise_user_id = praise_user_id;
    }

    public List<String> getPraise_user_id() {
        return praise_user_id;
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
