package com.cyan.ui;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.community.R;
import com.cyan.util.FileUtil;

import java.io.File;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2016/3/3.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_photo,
        toolbarTitle = R.string.photo,
        menuId =R.menu.menu_photo
)
public class PhotoActivity extends BaseActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.mToolbarContainer)
    AppBarLayout mToolbarContainer;
    @InjectView(R.id.iv_photo)
    PhotoView ivPhoto;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        url = getIntent().getStringExtra("photo");
        if (url.substring(url.lastIndexOf(".") + 1).equals("gif"))
            Glide.with(this).load(url).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<String, GifDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                   progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivPhoto);
        else Glide.with(this).load(url).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(ivPhoto);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save:
                subscription=Observable.create(new Observable.OnSubscribe<File>() {
                    @Override
                    public void call(Subscriber<? super File> subscriber) {
                        try {
                            File f=Glide.with(PhotoActivity.this)
                                    .load(url)
                                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                    .get() // needs to be called on background thread
                            ;
                         subscriber.onNext(f);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.io()).map(new Func1<File, String>() {
                    @Override
                    public String call(File file) {
                        Log.i("path", file.getPath());
                        FileUtil.copyFile(file.getPath(), Environment.getExternalStorageDirectory() + "/friends/" + url.substring(url.lastIndexOf("/") + 1));
                        return file.getPath();
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        toast("已保存至" + Environment.getExternalStorageDirectory() + "/friends/" + url.substring(url.lastIndexOf("/") + 1));
                    }
                });

                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
