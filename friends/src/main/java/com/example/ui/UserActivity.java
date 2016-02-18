package com.example.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.administrator.myapplication.R;
import com.example.bean.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2015/9/28.
 */
public class UserActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;


    User user, myUser;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Bitmap photo;
    DisplayImageOptions options;
    ProgressDialog dialog;
    @InjectView(R.id.user_icon_tips)
    TextView userIconTips;
    @InjectView(R.id.user_icon_image)
    ImageView userIconImage;
    @InjectView(R.id.sex_choice_switch)
    ToggleButton sexChoiceSwitch;
    @InjectView(R.id.user_icon)
    RelativeLayout userIcon;
    @InjectView(R.id.user_nick_text)
    TextView userNickText;
    @InjectView(R.id.sex_choice_tips)
    TextView sexChoiceTips;
    @InjectView(R.id.sex_choice)
    RelativeLayout sexChoice;
    @InjectView(R.id.user_sign_text)
    EditText userSignText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("修改资料");
        myUser = BmobUser.getCurrentUser(this, User.class);
        initView();
        user = new User();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save:
                if (!userSignText.getText().toString().equals(myUser.getIntro()))
                    user.setIntro(userSignText.getText().toString());
                if (f!=null)
                    uploadHead(f);
                else
                    updateUser();
                break;
            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initView() {
        options = new DisplayImageOptions.Builder()

                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                        // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
        userNickText.setText(myUser.getUsername());
        if (myUser.getHead() != null) {
            imageLoader.displayImage(myUser.getHead().getFileUrl(getApplicationContext()), userIconImage,options);
        }else{
            userIconImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher,null));
        }
        if (myUser.getSex().equals("男"))
            sexChoiceSwitch.setChecked(true);
        else sexChoiceSwitch.setChecked(false);
        if (myUser.getIntro() != null)
            userSignText.setText(myUser.getIntro());
        else userSignText.setHint("说点什么吧");
        userIconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, SelectPicPopupWindow.class);
                intent.putExtra("isCrop",true);
                startActivityForResult(intent, 0);
            }
        });
        sexChoiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    user.setSex("男");
                else
                    user.setSex("女");
            }
        });

    }

    /**
     * 上传指定路径下的头像
     *
     * @param @param type
     * @param @param i
     * @param @param file
     * @return void
     * @throws
     * @Description: TODO
     */
    private void uploadHead(File file) {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("上传中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.upload(this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                dialog.dismiss();
                url = bmobFile.getUrl();
                showToast("文件上传成功");
                Log.i("life", "t头像上传成功，返回的名称--" + bmobFile.getFileUrl(UserActivity.this) + "，文件名=" + bmobFile.getFilename());


                user.setHead(bmobFile);
                updateUser();
            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub
                Log.i("life", "uploadMovoieFile-->onProgress:" + arg0);
                dialog.setProgress(arg0);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                showToast("-->uploadMovoieFile-->onFailure:" + arg0 + ",msg = " + arg1);
            }

        });
        f=null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                Bundle b = data.getBundleExtra("photo");
                photo = b.getParcelable("data");
                userIconImage.setImageBitmap(photo);

                saveBitmap(photo);
               break;
            default:
                break;

        }
    }

    /**
     * 更新用户
     */
    private void updateUser() {


        user.update(this, myUser.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                toast("更新成功");
                MyApplication.getInstance().setCurrentUser();
                setResult(MainActivity.SAVE_OK);
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                toast("更新用户信息失败:" + msg);
            }
        });

    }

}
