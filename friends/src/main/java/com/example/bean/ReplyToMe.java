package com.example.bean;

/**
 * Created by Administrator on 2016/1/25.
 */
public class ReplyToMe extends CommentToMe{
 private String reply_content;
    private String post_author_name;
    private String post_author_id;

    public String getPost_author_id() {
        return post_author_id;
    }

    public void setPost_author_id(String post_author_id) {
        this.post_author_id = post_author_id;
    }

    public String getPost_author_name() {
        return post_author_name;
    }

    public void setPost_author_name(String post_author_name) {
        this.post_author_name = post_author_name;
    }

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }
}
