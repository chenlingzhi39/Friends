package com.example.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;


import com.example.administrator.myapplication.R;
import com.example.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/10/23.
 */
public class SelectPicPopupWindow extends Activity {
    @InjectView(R.id.photo)
    Button photo;
    @InjectView(R.id.album)
    Button album;
    @InjectView(R.id.cancel)
    Button cancel;
    String file_path;
boolean isCrop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);
        ButterKnife.inject(this);
        isCrop=getIntent().getBooleanExtra("isCrop",true);
    }

    @OnClick(R.id.photo)
    public void photo() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {


                Intent intent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, 2);

        } else {
            Toast.makeText(SelectPicPopupWindow.this, "没有储存卡", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @OnClick(R.id.album)
    public void album() {
        Intent i = new Intent(
                Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(i, 1);

    }

    @OnClick(R.id.cancel)
    public void cancel() {
        finish();
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪

        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高

        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 如果是直接从相册获取

            case 1:
                if (data != null) {
                    if(isCrop)
                    startPhotoZoom(data.getData());
                    else {
                        Intent intent = new Intent();
                     /*   Uri originalUri=data.getData();
                        String[] proj =  { MediaStore.Images.Media.DATA };
                        //好像是android多媒体数据库的封装接口，具体的看Android文档
                        Cursor cursor = getContentResolver().query(originalUri, proj, null, null, null);
                        //按我个人理解 这个是获得用户选择的图片的索引值
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        //将光标移至开头 ，这个很重要，不小心很容易引起越界
                        cursor.moveToFirst();
                        //最后根据索引值获取图片路径
                        String path = cursor.getString(column_index);*/
                        String path= Utils.getPath(this,data);
                        intent.putExtra("path",path);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;
            // 如果是调用相机拍照时

            case 2:
                if(data!=null) {
                    if (isCrop)
                        startPhotoZoom(data.getData());
                    else
                    {
                        Intent intent = new Intent();
                        Uri originalUri=data.getData();
                        String[] proj =  { MediaStore.Images.Media.DATA };
                        //好像是android多媒体数据库的封装接口，具体的看Android文档
                        Cursor cursor = getContentResolver().query(originalUri, proj, null, null, null);
                        //按我个人理解 这个是获得用户选择的图片的索引值
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        //将光标移至开头 ，这个很重要，不小心很容易引起越界
                        cursor.moveToFirst();
                        //最后根据索引值获取图片路径
                        String path = cursor.getString(column_index);

                        intent.putExtra("path",path);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;
            // 取得裁剪后的图片

            case 3:

                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        // photo = extras.getParcelable("data");
//                 Drawable drawable = new BitmapDrawable(photo);

                        /**

                         * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上

                         * 传到服务器，QQ头像上传采用的方法跟这个类似

                         */

                 /*ByteArrayOutputStream stream = new ByteArrayOutputStream();

                 photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);

                 byte[] b = stream.toByteArray();

                 // 将图片流以字符串形式存储下来



                 tp = new String(Base64Coder.encodeLines(b));

                 这个地方大家可以写下给服务器上传图片的实现，直接把tp直接上传就可以了，

                 服务器处理的方法是服务器那边的事了，吼吼



                 如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换

                 为我们可以用的图片类型就OK啦...吼吼

                 Bitmap dBitmap = BitmapFactory.decodeFile(tp);

                 Drawable drawable = new BitmapDrawable(dBitmap);

                 */
                        //  head.setImageBitmap(photo);

                        Intent intent = new Intent();
                        intent.putExtra("photo", extras);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
