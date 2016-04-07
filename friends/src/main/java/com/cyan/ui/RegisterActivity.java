package com.cyan.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.bean.User;
import com.cyan.common.Constants;
import com.cyan.community.R;
import com.cyan.module.file.presenter.FilePresenter;
import com.cyan.module.file.presenter.FilePresenterImpl;
import com.cyan.module.file.view.SendFileView;
import com.cyan.module.user.presenter.UserPresenter;
import com.cyan.module.user.presenter.UserPresenterImpl;
import com.cyan.module.user.view.SendUserView;
import com.cyan.util.BitmapUtil;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2015/10/16.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_register,
        toolbarTitle = R.string.register,
        menuId = R.menu.menu_register
)
public class RegisterActivity extends BaseActivity implements SendFileView,SendUserView {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.et_psd)
    EditText etPsd;
    @InjectView(R.id.et_dpsd)
    EditText etDpsd;
    @InjectView(R.id.hide)
    CheckBox hide;
    @InjectView(R.id.head)
    CircleImageView head;
    @InjectView(R.id.user_name)
    EditText userName;
    public static String TAG = "bmob";
    public String sex = "男";
    @InjectView(R.id.sex)
    RadioGroup group;
    private static String url = "";
    @InjectView(R.id.male)
    RadioButton male;
    @InjectView(R.id.female)
    RadioButton female;
    private Bitmap photo;
    private UserPresenter userPresenter;
    private FilePresenter filePresenter;
   private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        filePresenter=new FilePresenterImpl(this,this);
        userPresenter=new UserPresenterImpl(this,this);
        user = new User();
        initView();
    }

    @Override
    public void toastSendSuccess() {
        toast("注册成功");
        setResult(Constants.SAVE_OK);
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("name", userName.getText().toString());
        intent.putExtra("password", etPsd.getText().toString());
        setResult(RESULT_OK, intent);
        pd.dismiss();
        finish();
    }

    @Override
    public void toastSendFailure(int code, String msg) {
        toast("注册失败" + msg);
    }

    @Override
    public void showCircleDialog() {
        pd = ProgressDialog.show(RegisterActivity.this, null, "注册提交");
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
        toast("上传失败" + s);
    }

    @Override
    public void showHorizonalDialog() {
        dialog = new ProgressDialog(RegisterActivity.this);
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
        signUp();
    }

    @Override
    public void dismissHorizonalDialog() {
        dialog.dismiss();
    }

    public void initView() {
        //自动弹出软键盘
        Timer timer = new Timer();

        timer.schedule(new TimerTask()

                       {

                           public void run()

                           {

                               InputMethodManager inputManager =

                                       (InputMethodManager) userName.getContext().getSystemService(
                                               Context.INPUT_METHOD_SERVICE);

                               inputManager.showSoftInput(userName, 0);

                           }

                       },

                500);
        //是否显示密码
        hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                if (arg1) {
                    etPsd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etDpsd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etPsd.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etDpsd.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        //选择性别
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonId = group.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) RegisterActivity.this.findViewById(radioButtonId);
                sex = rb.getText().toString();
            }
        });
    }

    @OnClick(R.id.head)
    public void head() {
        Intent intent = new Intent(RegisterActivity.this, SelectPicPopupWindow.class);
        intent.putExtra("isCrop", true);
        startActivityForResult(intent, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.register:
                if (userName.getText().toString().equals("")) {
                    Toast.makeText(this, "用户名不能为空!", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (etPsd.getText().toString().equals("")) {
                    Toast.makeText(this, "密码不能为空!", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (etDpsd.getText().toString().equals("")) {
                    Toast.makeText(this, "请再输入一遍密码!", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!etPsd.getText().toString().equals(etDpsd.getText().toString())) {
                    Toast.makeText(this, "密码不匹配!", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (f != null) {
                   filePresenter.sendFile(f);
                } else {
                   signUp();
                }

                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

    /**
     * 注册
     */
    private void signUp() {
        pd=ProgressDialog.show(RegisterActivity.this,null,"正在注册");
        user.setUsername(userName.getText().toString());
        user.setPassword(etPsd.getText().toString());
        user.setSex((sex));
        userPresenter.signUp(user);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                Bundle b = data.getBundleExtra("photo");
                photo = b.getParcelable("data");
                head.setImageBitmap(photo);
                f= BitmapUtil.saveBitmap(RegisterActivity.this,photo, Constants.PHOTO_PATH,"head.jpg");
        }
    }

}
