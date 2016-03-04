package com.example.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.myapplication.R;
import com.example.bean.User;
import com.example.module.user.presenter.UserPresenter;
import com.example.module.user.presenter.UserPresenterImpl;
import com.example.module.user.view.UserView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2015/9/23.
 */
public class LoginActivity extends BaseActivity implements UserView {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.btn_regist)
    Button btnRegist;
    @InjectView(R.id.btn_login)
    Button btnLogin;
    @InjectView(R.id.user_name)
    EditText userName;
    @InjectView(R.id.user_pwd)
    EditText userPwd;
   private UserPresenter userPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("登录");
        userPresenter=new UserPresenterImpl(this,this);
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
    }



    @Override
    public void dismissCircleDialog() {
        pd.dismiss();
    }

    @Override
    public void showCircleDialog() {
        pd=ProgressDialog.show(LoginActivity.this,null,"正在登陆");
    }

    @Override
    public void toastSendFailure(int code, String msg) {
        toast("登陆失败");
    }

    @Override
    public void toastSendSuccess() {
        toast("登陆成功");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user", MyApplication.getInstance().getCurrentUser());
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.btn_regist)
    public void regist() {

        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
          startActivityForResult(intent, 0);
    }

    @OnClick(R.id.btn_login)
    public void login() {
        /**
         * 登陆用户
         */
        User user = new User();
        user.setUsername(userName.getText().toString());
        user.setPassword(userPwd.getText().toString());
      userPresenter.login(user);

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

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                //注册成功自动添加用户名密码
                Bundle b = data.getExtras();
                userName.setText(b.getString("name", ""));
                userPwd.setText(b.getString("password", ""));
                break;
        }
    }
}
