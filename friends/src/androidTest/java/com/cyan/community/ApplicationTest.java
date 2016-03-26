package com.cyan.community;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.cyan.bean.Post;
import com.cyan.module.post.model.PostModel;
import com.cyan.module.post.model.PostModelImpl;
import com.cyan.module.post.presenter.PostPresenter;
import com.cyan.module.post.presenter.PostPresenterImpl;

import cn.bmob.v3.BmobQuery;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
    public void testGetPost(){
        PostModel postModel=new PostModelImpl();
        BmobQuery<Post> query=new BmobQuery<>();
        postModel.loadPost(getApplication(),query,null);

    }
}