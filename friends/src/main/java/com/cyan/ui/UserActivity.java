package com.cyan.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.bean.User;
import com.cyan.community.R;
import com.cyan.module.file.presenter.FilePresenter;
import com.cyan.module.file.presenter.FilePresenterImpl;
import com.cyan.module.file.view.SendFileView;
import com.cyan.module.user.presenter.UserPresenter;
import com.cyan.module.user.presenter.UserPresenterImpl;
import com.cyan.module.user.view.UserView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2015/9/28.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_user,
        toolbarTitle = R.string.update,
        menuId =R.menu.menu_user
)
public class UserActivity extends BaseActivity implements UserView,SendFileView{

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    User user, myUser;
    Bitmap photo;
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
    private UserPresenter userPresenter;
   private FilePresenter filePresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        userPresenter=new UserPresenterImpl(this,this);
        filePresenter=new FilePresenterImpl(this,this);
        myUser = MyApplication.getInstance().getCurrentUser();
        initView();
        user = new User();
        user.setObjectId(myUser.getObjectId());
    }

    @Override
    public void toastSendSuccess() {
        toast("更新成功");
        setResult(MainActivity.SAVE_OK);
        MyApplication.getInstance().setCurrentUser();
    }

    @Override
    public void toastSendFailure(int code, String msg) {
       toast("更新失败");
    }

    @Override
    public void showCircleDialog() {
        pd = ProgressDialog.show(UserActivity.this, null, "正在提交");
    }

    @Override
    public void dismissCircleDialog() {
       pd.dismiss();
    }

    @Override
    public void updateHorizonalDialog(Integer i) {
      dialog.setProgress(i);
    }

    @Override
    public void toastUploadSuccess() {
      toast("上传成功");
    }

    @Override
    public void toastUploadFailure(int i,String s) {
        toast("上传失败"+s);
    }

    @Override
    public void showHorizonalDialog() {
            dialog = new ProgressDialog(UserActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setTitle("上传中...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();



    }

    @Override
    public void getFile(BmobFile bmobFile) {
        user.setHead(bmobFile);
        userPresenter.update(user);
    }

    @Override
    public void dismissHorizonalDialog() {
      dialog.dismiss();
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
                   filePresenter.sendFile(f);
                else
                    userPresenter.update(user);
                break;
            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initView() {
        userNickText.setText(myUser.getUsername());
        if (myUser.getHead() != null) {
            Glide.with(this).load(myUser.getHead().getFileUrl(getApplicationContext())).into(userIconImage);
        }else{
            userIconImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        }
        if (myUser.getSex().equals("男"))
            sexChoiceSwitch.setChecked(true);
        else sexChoiceSwitch.setChecked(false);
        if (myUser.getIntro() != null)
        { userSignText.setText(myUser.getIntro());
        userSignText.setSelection(myUser.getIntro().length());}
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                Bundle b = data.getBundleExtra("photo");
                photo = b.getParcelable("data");
                userIconImage.setImageBitmap(photo);
                saveBitmap(photo, Environment.getExternalStorageDirectory()+"/friends/", "head.jpg");
               break;
            default:
                break;

        }
    }



}
