package com.example.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2015/9/23.
 */
public class LoginActivity extends BasicActivity {
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
    private final int LOGIN_START = 0;
    private final int LOGIN_SUCCESS = 1;
    private final int LOGIN_FAIL = 2;

    ProgressDialog pd;
    Message message ;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case LOGIN_SUCCESS:
                    pd.dismiss();
                    finish();
                    break;
                case LOGIN_START:
                    pd=ProgressDialog.show(LoginActivity.this,null,"正在登录");
                    break;
                case LOGIN_FAIL:
                    pd.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("登录");
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
        message=new Message();
        message.arg1 = LOGIN_START;
        handler.sendMessage(message);
        handler.sendEmptyMessage(LOGIN_START);
        final User bu2 = new User();
        bu2.setUsername(userName.getText().toString());
        bu2.setPassword(userPwd.getText().toString());
        bu2.login(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                toast(bu2.getUsername() + "登陆成功");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user", testGetCurrentUser());
                setResult(RESULT_OK, intent);
                message = new Message();
                message.arg1 = LOGIN_SUCCESS;
                handler.sendMessage(message);

            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                message = new Message();
                message.arg1 = LOGIN_FAIL;
                handler.sendMessage(message);
                toast("登陆失败:" + msg);
            }
        });

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

    /**
     * 获取本地用户
     */
    private User testGetCurrentUser() {
        User myUser = BmobUser.getCurrentUser(this, User.class);
        if (myUser != null) {
            Log.i("life", "本地用户信息:objectId = " + myUser.getObjectId() + ",name = " + myUser.getUsername()
            );
        } else {
            toast("本地用户为null,请登录。");
        }
        return myUser;
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
