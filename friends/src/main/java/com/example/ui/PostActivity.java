package com.example.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Emoji;
import com.example.adapter.EmojiAdapter;
import com.example.administrator.myapplication.R;
import com.example.bean.Post;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2015/11/12.
 */
public class PostActivity extends BasicActivity {
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

    @Override
    public void start() {
        pd=ProgressDialog.show(PostActivity.this,null,dialog_content);
    }

    @Override
    public void succeed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("发表作品");

    }

    @OnClick(R.id.select_image)
    public void select_image() {
        Intent intent = new Intent(PostActivity.this, SelectPicPopupWindow.class);
        intent.putExtra("isCrop", false);
        startActivityForResult(intent, 0);

    }

    @OnClick(R.id.delete_image)
    public void delete_image() {
        path=null;
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
        startActivityForResult(intent,0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.post:
                if (content.getText().toString().trim().equals("")&&path==null) {
                    Toast.makeText(this, "内容或图片不能为空!", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (path != null)
                    uploadImage(new File(path));
                else
                    post();
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
        setDialogContent("正在提交");
        handler.sendEmptyMessage(START);
        Post post = new Post();
        if(!content.getText().toString().trim().equals(""))
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

        obj.save(getApplicationContext(), new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                showToast("-->创建数据成功：" + obj.getObjectId());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                setResult(SUBMIT_OK, intent);
                handler.sendEmptyMessage(SUCCEED);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                showToast("-->创建数据失败：" + arg0 + ",msg = " + arg1);
                handler.sendEmptyMessage(FAIL);
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
