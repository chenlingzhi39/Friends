package com.example.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.administrator.myapplication.R;
import com.example.util.FileUtil;

import java.io.File;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2016/3/3.
 */
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
    private Bitmap photo;
    private SaveImageTask saveImageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("图片");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save:
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        try {
                            Glide.with(PhotoActivity.this)
                                    .load(url)
                                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                    .get() // needs to be called on background thread
                            ;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }).subscribeOn(Schedulers.newThread()).doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        FileUtil.copyFile(s, Environment.getExternalStorageDirectory() + "/friends/" + url.substring(url.lastIndexOf("/") + 1));
                    }
                }).observeOn(Schedulers.io()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        toast("已保存至" + Environment.getExternalStorageDirectory() + "/friends/" + url.substring(url.lastIndexOf("/") + 1));
                    }
                });
             /*   saveImageTask = new SaveImageTask(this);
                saveImageTask.execute(url);*/
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SaveImageTask extends AsyncTask<String, Void, File> {
        private Context context;

        public SaveImageTask(Context context) {
            this.context = context;
        }

        @Override
        protected File doInBackground(String... params) {
            String url = params[0]; // should be easy to extend to share multiple images at once
            try {
                return Glide
                        .with(context)
                        .load(url)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get() // needs to be called on background thread
                        ;
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) {
                return;
            }
            String path = result.getPath();
            FileUtil.copyFile(path, Environment.getExternalStorageDirectory() + "/friends/" + url.substring(url.lastIndexOf("/") + 1));
            toast("已保存至" + Environment.getExternalStorageDirectory() + "/friends/" + url.substring(url.lastIndexOf("/") + 1));
        }
    }
}
