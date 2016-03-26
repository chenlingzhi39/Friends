package com.cyan.ui;

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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.bumptech.glide.Glide;
import com.cyan.app.MyApplication;
import com.cyan.Emoji;
import com.cyan.adapter.EmojiAdapter;
import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.bean.Post;
import com.cyan.community.R;
import com.cyan.module.file.presenter.FilePresenter;
import com.cyan.module.file.presenter.FilePresenterImpl;
import com.cyan.module.file.view.SendFileView;
import com.cyan.module.post.presenter.PostPresenter;
import com.cyan.module.post.presenter.PostPresenterImpl;
import com.cyan.module.post.view.SendPostView;
import com.cyan.util.StringUtils;

import java.io.File;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Draft;
import de.greenrobot.daoexample.DraftDao;
import de.greenrobot.daoexample.Record;
import de.greenrobot.daoexample.RecordDao;

/**
 * Created by Administrator on 2015/11/12.
 */
@ActivityFragmentInject(
        contentViewId = R.layout.activity_post,
        toolbarTitle = R.string.post,
        menuId = R.menu.menu_post
)
public class PostActivity extends BaseActivity implements SendPostView, SendFileView, AMapLocationListener {
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
    @InjectView(R.id.draft)
    ImageButton draft;

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
    private DraftDao draftDao;
    private PostPresenter postPresenter;
    private FilePresenter filePresenter;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        postPresenter = new PostPresenterImpl(this, this);
        filePresenter = new FilePresenterImpl(this, this);
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
        StringBuilder sb = new StringBuilder();
        if (aMapLocation.getErrorCode() == 0) {
            location.setText(aMapLocation.getAddress());
            Log.i("address", aMapLocation.getAddress());
        } else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + aMapLocation.getErrorCode() + "\n");
            sb.append("错误信息:" + aMapLocation.getErrorInfo() + "\n");
            sb.append("错误描述:" + aMapLocation.getLocationDetail() + "\n");
            Log.i("error", sb.toString());
        }
    }

    @Override
    public void getFile(BmobFile bmobFile) {
        post.setImage(bmobFile);
        postPresenter.sendPost(post);
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
    public void toastUploadFailure(int i, String s) {
        toast("上传失败" + s);
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


    @OnClick(R.id.doodle)
    public void doodle() {
        Intent intent = new Intent(PostActivity.this, DoodleActivity.class);
        startActivityForResult(intent, 0);
    }
    @OnClick(R.id.draft)
    public void draft(){
        Intent intent=new Intent(PostActivity.this,DraftActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.post:
                if (content.getText().toString().trim().equals("") && path == null) {
                    Toast.makeText(this, "内容或图片不能为空!", Toast.LENGTH_SHORT).show();
                    break;
                }
                post = new Post();
                if (!content.getText().toString().trim().equals(""))
                    post.setContent(content.getText().toString());
                else post.setContent("分享图片");
                Log.i("isLocated", isLocated.isChecked() + "");
                if (isLocated.isChecked())
                    post.setUser_location(location.getText() + "");
                post.setComment_count(0);
                post.setPraise_count(0);
                post.setAuthor(MyApplication.getInstance().getCurrentUser());
                if (path == null)
                    postPresenter.sendPost(post);
                else
                    filePresenter.sendFile(new File(path));
                break;
            case android.R.id.home:
                if(!content.getText().toString().equals(""))
                showSaveDialog();
                else finish();
                break;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(!content.getText().toString().equals(""))
                showSaveDialog();
            else finish();
            return true;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                path = data.getStringExtra("path");
                Glide.with(this).load("file://" + path).into(image);
                Log.i("path", data.getStringExtra("path"));
                image.setVisibility(View.VISIBLE);
                deleteImage.setVisibility(View.VISIBLE);
                break;
        }

    }

    public class SaveDialogHelper implements
            DialogInterface.OnDismissListener, View.OnClickListener {
        @InjectView(R.id.not_save)
        Button notSave;
        @InjectView(R.id.cancel)
        Button cancel;
        @InjectView(R.id.ok)
        Button ok;
        @InjectView(R.id.title)
        TextView title;
        private Dialog mDialog;
        private View mView;

        public SaveDialogHelper() {
            mView = PostActivity.this.getLayoutInflater().inflate(R.layout.dialog_save, null);
            ButterKnife.inject(this,mView);
            notSave.setOnClickListener(this);
            cancel.setOnClickListener(this);
            ok.setOnClickListener(this);
            title.setText("把你输入的文字保存为草稿?");
        }

        public void setDialog(Dialog mDialog) {
            this.mDialog = mDialog;
        }

        public View getView() {
            return mView;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.not_save:
                    finish();
                    break;
                case R.id.cancel:
                    mDialog.dismiss();
                    break;
                case R.id.ok:
                    Draft draft = new Draft();
                    DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "drafts-db", null);
                    db = helper.getWritableDatabase();
                    daoMaster = new DaoMaster(db);
                    daoSession = daoMaster.newSession();
                    draftDao = daoSession.getDraftDao();
                    draft.setContent(content.getText().toString());
                    draft.setCreate_time(new Date(System.currentTimeMillis()));
                    draftDao.insert(draft);
                    mDialog.dismiss();
                    finish();
                    break;
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            mDialog = null;
        }
    }

    public void showSaveDialog() {
        SaveDialogHelper helper = new SaveDialogHelper();
        Dialog dialog = new AlertDialog.Builder(this)
                .setView(helper.getView())
                .setOnDismissListener(helper)
                .create();
        helper.setDialog(dialog);
        dialog.show();
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
