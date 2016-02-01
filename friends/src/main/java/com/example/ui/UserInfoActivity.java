package com.example.ui;

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
    private static final int NOT_FOCUS=0;
    private static final int BE_FOCUSED=1;
    private static final int FOCUS=2;
    private static final int FOCUS_EACH=3;
    private int state=0;
    private ImageLoader imageLoader = ImageLoader.getInstance();

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
@OnClick(R.id.edit)
public void edit(){
   if(!user.getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId())){
    Focus focus=new Focus();
    focus.setUser(MyApplication.getInstance().getCurrentUser());
    focus.setFocusUser(user);
    switch (state){
        case 0:
        case 1:
            focus.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                  if(state==1)state=3;
                    else state=2;
                    edit.setText("已关注");
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
                    if(state==2){state=0;edit.setText("+关注");}
                    else {state=1;
                    edit.setText("互相关注");
                    }
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
            break;
    }}
}

    private void init() {
        if (user.getHead() != null) {
            imageLoader.displayImage(user.getHead().getFileUrl(this), head);
        }
        if(user.getObjectId().equals(MyApplication.getInstance().getCurrentUser().getObjectId()))
        {edit.setText("编辑");
        edit.setClickable(true);}
        else{
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
    public void queryFocus(){
        BmobQuery query=new BmobQuery<Focus>();
        query.addWhereEqualTo("user",MyApplication.getInstance().getCurrentUser());
        query.addWhereEqualTo("focusUser",user);
        query.findObjects(this, new FindListener() {
            @Override
            public void onSuccess(List list) {
                if(list.size()!=0)
                {edit.setText("已关注");
                    BmobQuery query=new BmobQuery<Focus>();
                    query.addWhereEqualTo("focusUser",MyApplication.getInstance().getCurrentUser());
                    query.addWhereEqualTo("user",user);
                    query.findObjects(getApplicationContext(), new FindListener() {
                        @Override
                        public void onSuccess(List list) {
                            if (list.size() != 0)
                             state = 3;
                             else state=2;
                            edit.setClickable(true);
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }else{
                    BmobQuery query=new BmobQuery<Focus>();
                    query.addWhereEqualTo("focusUser",MyApplication.getInstance().getCurrentUser());
                    query.addWhereEqualTo("user",user);
                    query.findObjects(getApplicationContext(), new FindListener() {
                        @Override
                        public void onSuccess(List list) {
                            if(list.size()!=0)
                            {   edit.setText("相互关注");
                            state=1;}
                            else edit.setText("+关注");
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

    }

}
