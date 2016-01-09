package com.example.dao;

/**
 * Created by Administrator on 2016/1/9.
 */
public interface Communicate {
    void start();
    void succeed();
    void fail();
    void refreshQuery();
    void loadMoreQuery();

}
