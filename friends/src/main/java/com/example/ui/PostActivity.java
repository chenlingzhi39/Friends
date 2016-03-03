package com.example.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.Emoji;
import com.example.adapter.EmojiAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Post;
import com.example.module.post.presenter.PostPresenter;
import com.example.module.post.presenter.PostPresenterImpl;
import com.example.module.post.view.SendPostView;
import com.example.util.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Record;
import de.greenrobot.daoexample.RecordDao;

/**
 * Created by Administrator on 2015/11/12.
 */
public class PostActivity extends BaseActivity implements SendPostView, AMapLocationListener {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.content)
    EditText content;
    @InjectView(R.id.location)
    TextView location;
    @InjectView(R.id.delete_image)
    ImageView deleteImage;
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.select_image)
    ImageButton selectImage;
    @InjectView(R.id.select_emoji)
    ImageButton selectEmoji;
    ImageLoader imageLoader = ImageLoader.getInstance();

    Bitmap photo;
    String path;
    public final static int SUBMIT_OK = 3;
    @InjectView(R.id.is_located)
    CheckBox isLocated;
    @InjectView(R.id.doodle)
    ImageButton doodle;
    private SQLiteDatabase db;
    private DaoSession daoSession;
    private RecordDao recordDao;
    private DaoMaster daoMaster;
    private PostPresenter postPresenter;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("发表作品");
        postPresenter = new PostPresenterImpl(this, this);
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption.setGpsFirst(false);
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        locationOption.setOnceLocation(true);
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stopLocation();
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        StringBuilder sb=new StringBuilder();
        if(aMapLocation.getErrorCode() == 0) {
            location.setText(aMapLocation.getAddress());
           Log.i("address",aMapLocation.getAddress());}
            else
         {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + aMapLocation.getErrorCode() + "\n");
            sb.append("错误信息:" +aMapLocation.getErrorInfo() + "\n");
            sb.append("错误描述:" +aMapLocation.getLocationDetail() + "\n");
             Log.i("error",sb.toString());
        }
    }

    @Override
    public void refresh(Post post) {
        setResult(SUBMIT_OK);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "records-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        recordDao = daoSession.getRecordDao();
        Record record = new Record();
        record.setType("post");
        record.setContent(post.getContent());
        record.setUser_id(MyApplication.getInstance().getCurrentUser().getObjectId());
        record.setAdd_time(StringUtils.toDate(post.getCreatedAt()));
        if (post.getImage() != null)
            record.setImage(post.getImage().getFileUrl(getApplicationContext()));
        record.setObject_id(post.getObjectId());
        recordDao.insert(record);
        pd.dismiss();
        finish();
    }

    @Override
    public void showCircleDialog() {
        pd = ProgressDialog.show(PostActivity.this, null, "正在提交");
    }

    @Override
    public void showHorizonalDialog() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("上传中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void updateHorizonalDialog(Integer i) {
        dialog.setProgress(i);
    }

    @Override
    public void dismissHorizonalDialog() {
        dialog.dismiss();
    }

    @Override
    public void dismissCircleDialog() {
        pd.dismiss();
    }

    @Override
    public void toastUploadFailure() {
        toast("上传失败");
    }

    @Override
    public void toastSendFailure() {
        toast("提交失败");
    }

    @Override
    public void toastUploadSuccess() {
        toast("上传成功");
    }

    @Override
    public void toastSendSuccess() {
        toast("提交成功");
    }

    @OnClick(R.id.select_image)
    public void select_image() {
        Intent intent = new Intent(PostActivity.this, SelectPicPopupWindow.class);
        intent.putExtra("isCrop", false);
        startActivityForResult(intent, 0);

    }

    @OnClick(R.id.delete_image)
    public void delete_image() {
        path = null;
        image.setVisibility(View.GONE);
        deleteImage.setVisibility(View.GONE);
    }

    @OnClick(R.id.select_emoji)
    public void select_emoji() {
        showEmojiDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @OnClick(R.id.doodle)
    public void doodle() {
        Intent intent = new Intent(PostActivity.this, DoodleActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.post:
                if (content.getText().toString().trim().equals("") && path == null) {
                    Toast.makeText(this, "内容或图片不能为空!", Toast.LENGTH_SHORT).show();
                    break;
                }
                Post post = new Post();
                if (!content.getText().toString().trim().equals(""))
                    post.setContent(content.getText().toString());
                else post.setContent("分享图片");
                Log.i("isLocated",isLocated.isChecked()+"");
                if (isLocated.isChecked())
                    post.setUser_location(location.getText()+"");
                    post.setComment_count(0);
                post.setPraise_count(0);
                post.setAuthor(MyApplication.getInstance().getCurrentUser());
                postPresenter.sendPost(path == null ? null : new File(path), post);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:


                path = data.getStringExtra("path");

                imageLoader.displayImage("file://" + path, image);
                Log.i("path", data.getStringExtra("path"));
                image.setVisibility(View.VISIBLE);
                deleteImage.setVisibility(View.VISIBLE);


                break;
        }

    }

    /**
     * 上传指定路径下的图片
     *
     * @param @param type
     * @param @param i
     * @param @param file
     * @return void
     * @throws
     * @Description: TODO
     */
    private void uploadImage(File file) {
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
                Log.i("life", "图片上传成功，返回的名称--" + bmobFile.getFileUrl(getApplicationContext()) + "，文件名=" + bmobFile.getFilename());
                imageFile = bmobFile;
                post();
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

    }

    private void post() {

        Post post = new Post();
        if (!content.getText().toString().trim().equals(""))
            post.setContent(content.getText().toString());
        else post.setContent("分享图片");
        if (imageFile != null)
            post.setImage(imageFile);
        post.setComment_count(0);
        post.setPraise_count(0);
        post.setAuthor(MyApplication.getInstance().getCurrentUser());
        insertObject(post);
    }


    public void insertObject(final BmobObject obj) {
        pd = ProgressDialog.show(PostActivity.this, null, "正在提交");
        obj.save(getApplicationContext(), new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                showToast("-->创建数据成功：" + obj.getObjectId());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                setResult(SUBMIT_OK, intent);
                DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "records-db", null);
                db = helper.getWritableDatabase();
                daoMaster = new DaoMaster(db);
                daoSession = daoMaster.newSession();
                recordDao = daoSession.getRecordDao();
                Record record = new Record();
                record.setType("post");
                record.setContent(((Post) obj).getContent());
                record.setUser_id(MyApplication.getInstance().getCurrentUser().getObjectId());
                record.setAdd_time(StringUtils.toDate(obj.getCreatedAt()));
                if (((Post) obj).getImage() != null)
                    record.setImage(((Post) obj).getImage().getFileUrl(getApplicationContext()));
                record.setObject_id(obj.getObjectId());
                recordDao.insert(record);
                pd.dismiss();
                finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                showToast("-->创建数据失败：" + arg0 + ",msg = " + arg1);
                pd.dismiss();
            }
        });
    }

    private class EmojiDialogHelper implements
            DialogInterface.OnDismissListener {

        private Dialog mDialog;
        private View mView;

        private EmojiDialogHelper() {
            @SuppressLint("InflateParams")
            RecyclerView recyclerView = (RecyclerView) PostActivity.this
                    .getLayoutInflater().inflate(R.layout.dialog_emoji, null);
            EmojiAdapter emojiAdapter = new EmojiAdapter();
            emojiAdapter.setOnItemClickLitener(new EmojiAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (mDialog != null) {
                        EditText editText = content;
                        String emoji = Emoji.EMOJI_VALUE[position];
                        int start = Math.max(editText.getSelectionStart(), 0);
                        int end = Math.max(editText.getSelectionEnd(), 0);
                        editText.getText().replace(Math.min(start, end), Math.max(start, end),
                                emoji, 0, emoji.length());
                        mDialog.dismiss();
                        mDialog = null;
                        Log.i("select", position + "");
                    }

                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
            recyclerView.setAdapter(emojiAdapter);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                    3, StaggeredGridLayoutManager.VERTICAL));// TODO adjust by view width


            mView = recyclerView;
        }

        public View getView() {
            return mView;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }


        @Override
        public void onDismiss(DialogInterface dialog) {
            mDialog = null;
        }


    }

    private void showEmojiDialog() {
        EmojiDialogHelper helper = new EmojiDialogHelper();
        Dialog dialog = new AlertDialog.Builder(this)
                .setView(helper.getView())
                .setOnDismissListener(helper)
                .create();
        helper.setDialog(dialog);
        dialog.show();
    }

}
