package com.example.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.administrator.myapplication.R;
import com.example.bean.User;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2015/10/16.
 */
public class RegisterActivity extends BaseActivity {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("注册");
        initView();
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
        intent.putExtra("isCrop",true);
        startActivityForResult(intent, 0);
    }

    ;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
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
                    uploadHead(f);
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
        final User myUser = new User();
        myUser.setUsername(userName.getText().toString());
        myUser.setPassword(etPsd.getText().toString());
        myUser.setSex((sex));
        myUser.setHead(imageFile);
        myUser.signUp(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                toast("注册成功:" + myUser.getUsername() + "-"
                        + myUser.getObjectId() + "-" + myUser.getCreatedAt()
                        + "-" + myUser.getSessionToken() + ",是否验证：" + myUser.getEmailVerified());
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("name", userName.getText().toString());
                intent.putExtra("password", etPsd.getText().toString());
                setResult(RESULT_OK, intent);
                pd.dismiss();
                finish();
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                toast("注册失败:" + msg);
                pd.dismiss();
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
                head.setImageBitmap(photo);
              saveBitmap(photo,Environment.getExternalStorageDirectory()+"/friends/", "head.jpg");
        }
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
                Log.i("life", "t头像上传成功，返回的名称--" + bmobFile.getFileUrl(RegisterActivity.this) + "，文件名=" + bmobFile.getFilename());



                signUp();
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



    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

}
