package com.example.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.bean.Focus;
import com.example.bean.Post;
import com.example.bean.User;
import com.example.listener.ScrollViewListener;
import com.example.util.Utils;
import com.example.widget.AlphaView;
import com.example.widget.ObservableScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2015/9/25.
 */
public class UserInfoActivity extends AppCompatActivity implements ScrollViewListener {


    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.content)
    RelativeLayout content;
    @InjectView(R.id.scrollView)
    ObservableScrollView scrollView;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.toolbar_background)
    AlphaView toolbarBackground;
    @InjectView(R.id.buttons)
    LinearLayout buttons;
    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.post_num)
    TextView postNum;
    @InjectView(R.id.btn_post)
    LinearLayout btnPost;
    @InjectView(R.id.focus_num)
    TextView focusNum;
    @InjectView(R.id.btn_focus)
    LinearLayout btnFocus;
    @InjectView(R.id.fans_num)
    TextView fansNum;
    @InjectView(R.id.btn_fans)
    LinearLayout btnFans;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.edit)
    Button edit;
    private User user;
    private static final int NOT_FOCUS = 0;
    private static final int BE_FOCUSED = 1;
    private static final int FOCUS = 2;
    private static final int FOCUS_EACH = 3;
    private int state = 0;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private int fans_num;
    private int focus_num;
    private int post_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.inject(this);
        user = (User) getIntent().getExtras().get("user");
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("用户");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        toolbar.setPadding(0, Utils.getStatusBarHeight(this), 0, 0);
        scrollView.setScrollViewListener(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.getStatusBarHeight(this) + Utils.getToolbarHeight(this));
        toolbarBackground.setLayoutParams(lp);
        image.post(new Runnable() {
            @Override
            public void run() {
                toolbarBackground.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background), image.getWidth(), image.getHeight());
            }
        });

    }
    @OnClick(R.id.btn_post)
    public void btn_post(){
        Intent intent=new Intent(UserInfoActivity.this,PostListActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("post_num",post_num);
        startActivityForResult(intent,0);

    }
    @OnClick(R.id.btn_focus)
    public void btn_focus(){
        Intent intent=new Intent(UserInfoActivity.this,FocusActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("focus_num",focus_num);
        startActivity(intent);
    }
    @OnClick(R.id.btn_fans)
    public void btn_fans(){
        Intent intent=new Intent(UserInfoActivity.this,FansActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("fans_num",fans_num);
        startActivity(intent);
    }
    @OnClick(R.id.edit)
    public void edit() {
        if (!user.getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId())) {
            Focus focus = new Focus();
            focus.setUser(MyApplication.getInstance().getCurrentUser());
            focus.setFocusUser(user);
            switch (state) {
                case 0:
                case 1:
                    focus.save(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            if (state == 1) state = 3;
                            else state = 2;
                            edit.setText("已关注");
                            fans_num = fans_num - 1;
                            fansNum.setText(fans_num + "");
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                    break;
                case 2:
                case 4:
                    focus.delete(this, new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            if (state == 2) {
                                state = 0;
                                edit.setText("+关注");
                            } else {
                                state = 1;
                                edit.setText("互相关注");
                            }
                            fans_num = fans_num - 1;
                            fansNum.setText(fans_num + "");

                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                    break;
            }
        }
    }

    private void init() {
        if (user.getHead() != null) {
            imageLoader.displayImage(user.getHead().getFileUrl(this), head);
        }
        if (user.getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId())) {
            edit.setText("编辑");
            edit.setClickable(true);
        } else {
            queryFocus();
        }
        userName.setText(user.getUsername());
    }

    @Override
    public void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy) {

        if (y >= 0 && y <= (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - buttons.getHeight())) {
            toolbarBackground.setAlpha(0);
            toolbar.setBackgroundColor(Color.argb(255 * y / (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this)), 0, 0, 0));
        }
        if (y >= (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - 2 * buttons.getHeight()) && y <= (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - buttons.getHeight()))

        {
            Log.i("long", (y - (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - 2 * buttons.getHeight())) + "");
            toolbarBackground.setAlpha(255 * (y - (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - 2 * buttons.getHeight())) / buttons.getHeight());
        }
        if (y > (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - buttons.getHeight())) {
            toolbarBackground.setAlpha(255);
        }
        if (y > (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this))) {
            toolbar.setBackgroundColor(Color.argb(255 * (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this) - buttons.getHeight()) / (image.getHeight() - Utils.getStatusBarHeight(this) - Utils.getToolbarHeight(this)), 0, 0, 0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void queryFocus() {
        BmobQuery<Focus> query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(MyApplication.getInstance().getCurrentUser()));
        query.addWhereEqualTo("focus_user", new BmobPointer(user));
        final User user0=new User();
        user0.setObjectId(user.getObjectId());
        query.findObjects(this, new FindListener<Focus>() {
            @Override
            public void onSuccess(List list) {
                if (list.size() != 0) {
                    edit.setText("已关注");
                    BmobQuery<Focus> query = new BmobQuery<>();
                    query.addWhereEqualTo("focus_user", new BmobPointer(MyApplication.getInstance().getCurrentUser()));
                    query.addWhereEqualTo("user", new BmobPointer(user0));
                    query.findObjects(getApplicationContext(), new FindListener<Focus>() {
                        @Override
                        public void onSuccess(List list) {
                            if (list.size() != 0)
                                state = 3;
                            else state = 2;
                            edit.setClickable(true);
                        }

                        @Override
                        public void onError(int i, String s) {
                        }
                    });
                } else {
                    BmobQuery<Focus> query = new BmobQuery<>();
                    query.addWhereEqualTo("focus_user", new BmobPointer(MyApplication.getInstance().getCurrentUser()));
                    query.addWhereEqualTo("user", new BmobPointer(user0));
                    query.findObjects(getApplicationContext(), new FindListener<Focus>() {
                        @Override
                        public void onSuccess(List list) {
                            if (list.size() != 0) {
                                edit.setText("相互关注");
                                state = 1;
                            } else edit.setText("+关注");
                            edit.setClickable(true);
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        query = new BmobQuery<>();
        query.addWhereEqualTo("focus_user", new BmobPointer(user0));
        query.count(this, Focus.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                fans_num = i;
                fansNum.setText(fans_num + "");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
        query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(user0));
        query.count(this, Focus.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                focus_num = i;
                focusNum.setText(focus_num + "");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
        BmobQuery<Post> query1=new BmobQuery<>();
        query1.addWhereEqualTo("author",new BmobPointer(user0));
        query1.count(this, Post.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                post_num=i;
                postNum.setText(post_num+"");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case MainActivity.REFRESH_PRAISE:
                Intent intent = new Intent();
                intent.putExtra("post_id", data.getStringExtra("post_id"));
                intent.putExtra("is_praised", data.getBooleanExtra("is_praised",false));
                setResult(MainActivity.REFRESH_PRAISE, intent);
                break;
            case MainActivity.REFRESH_COLLECTION:
                intent = new Intent();
                intent.putExtra("post_id", data.getStringExtra("post_id"));
                intent.putExtra("is_collected", data.getBooleanExtra("is_collected",false));
                setResult(MainActivity.REFRESH_COLLECTION, intent);
                break;
            case MainActivity.REFRESH_COMMENT:
                intent = new Intent();
                intent.putExtra("post",(Post)data.getExtras().get("post"));
                setResult(MainActivity.REFRESH_COMMENT, intent);
                break;
            default:
                break;
        }

    }
}
