package com.cyan.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "REPLY_TO_ME".
 */
public class ReplyToMe {

    private Long id;
    private String yourid;
    private String postid;
    private String userid;
    private String user_name;
    private String head;
    private String post_content;
    private String comment_content;
    private String comment_id;
    private String reply_content;
    private String post_author_name;
    private String post_author_id;
    private java.util.Date create_time;

    public ReplyToMe() {
    }

    public ReplyToMe(Long id) {
        this.id = id;
    }

    public ReplyToMe(Long id, String yourid, String postid, String userid, String user_name, String head, String post_content, String comment_content, String comment_id, String reply_content, String post_author_name, String post_author_id, java.util.Date create_time) {
        this.id = id;
        this.yourid = yourid;
        this.postid = postid;
        this.userid = userid;
        this.user_name = user_name;
        this.head = head;
        this.post_content = post_content;
        this.comment_content = comment_content;
        this.comment_id = comment_id;
        this.reply_content = reply_content;
        this.post_author_name = post_author_name;
        this.post_author_id = post_author_id;
        this.create_time = create_time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getYourid() {
        return yourid;
    }

    public void setYourid(String yourid) {
        this.yourid = yourid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }

    public String getPost_author_name() {
        return post_author_name;
    }

    public void setPost_author_name(String post_author_name) {
        this.post_author_name = post_author_name;
    }

    public String getPost_author_id() {
        return post_author_id;
    }

    public void setPost_author_id(String post_author_id) {
        this.post_author_id = post_author_id;
    }

    public java.util.Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(java.util.Date create_time) {
        this.create_time = create_time;
    }

}
