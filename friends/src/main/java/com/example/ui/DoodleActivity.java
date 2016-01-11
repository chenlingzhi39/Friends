package com.example.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.administrator.myapplication.R;
import com.example.common.Constants;
import com.example.util.LayoutUtils;
import com.example.util.SimpleHandler;
import com.example.widget.Slider;
import com.example.widget.ThicknessPreviewView;
import com.example.widget.TuyaView;
import com.rarepebble.colorpicker.ColorPickerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DoodleActivity extends BaseActivity implements TuyaView.Helper{


    Bitmap background = null;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.palette)
    ImageButton palette;
    @InjectView(R.id.broom)
    ImageButton broom;
    @InjectView(R.id.undo)
    ImageButton undo;
    @InjectView(R.id.image)
    ImageButton image;
    @InjectView(R.id.brush)
    ImageButton brush;

    @InjectView(R.id.settings)
    LinearLayout settings;
    @InjectView(R.id.tuyaView)
    TuyaView tuyaView;
    Handler handler = new Handler();
    private int curMode = 1;
    private int color = Color.BLACK;
    private ProgressDialog dialog;
    private String filePath;
    public static final int REQUEST_CODE_SELECT_IMAGE = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_doodle);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("涂鸦板");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setContentView(R.layout.activity_doodle);
        int width = this.getWindowManager().getDefaultDisplay().getWidth();
        int height = this.getWindowManager().getDefaultDisplay().getHeight();
        //background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //Canvas canvas = new Canvas(background);
        //Paint paint = new Paint();
        //paint.setColor(Color.WHITE);
        //canvas.drawColor(Color.WHITE);
        //canvas.drawBitmap(background, 0, 0, paint);

        //  background = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
      tuyaView.setHelper(this);

    }

    @Override
    public void onSavingFinished() {
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                if(dialog!=null){
                    dialog.dismiss();
                }
                Intent intent = new Intent();
                intent.putExtra("path",filePath);
                setResult(RESULT_OK, intent);
                finish();
            }
        };
       handler.post(runnable);
    }

    @OnClick(R.id.brush)
    public void brush() {
        switch (curMode) {
            case 1:
                brush.setImageDrawable(getResources().getDrawable(R.drawable.ic_eraser_dark_x24));
                curMode = 4;
                tuyaView.setMode(curMode);
                break;
            case 4:
                brush.setImageDrawable(getResources().getDrawable(R.drawable.ic_brush_dark_x24));
                curMode = 1;
                tuyaView.setMode(curMode);
                break;
        }

    }

    @OnClick(R.id.palette)
    public void palette() {
        SimpleHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {
                showColorPickerDialog();
            }
        });

    }

    @OnClick(R.id.broom)
    public void broom() {
        showThicknessDialog();
    }

    @OnClick(R.id.undo)
    public void undo() {
        tuyaView.undo();

    }
   @OnClick(R.id.image)
   public void image(){
       Intent intent = new Intent();
       intent.setType("image/*");
       intent.setAction(Intent.ACTION_GET_CONTENT);
       startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doodle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.finish:
                dialog = new ProgressDialog(this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(false);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show(DoodleActivity.this, null, "正在登录");
                filePath=Constants.PIC_STORE_PATH+System.currentTimeMillis()+".jpg";
                tuyaView.saveToSDCard(filePath);

                break;
            case R.id.delete:
                tuyaView.clear();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                if (bitmap != null) {
                   tuyaView.setBackground(bitmap);
                }
            } catch (OutOfMemoryError e) {
                // Ignore
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    class ColorPickerDialogHelper implements
            DialogInterface.OnDismissListener {
        private Dialog mDialog;
        private View mView;
        private ColorPickerView colorPickerView;

        private ColorPickerDialogHelper() {
            mView = DoodleActivity.this.getLayoutInflater().inflate(R.layout.dialog_colorpicker, null);
            colorPickerView = (ColorPickerView) mView.findViewById(R.id.colorPicker);
            colorPickerView.setColor(color);
        }

        public View getView() {
            return mView;
        }

        public ColorPickerView getColorPickerView() {
            return colorPickerView;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }


    }

    public void showColorPickerDialog() {
        final ColorPickerDialogHelper helper = new ColorPickerDialogHelper();
                Dialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(DoodleActivity.this, R.style.myDialog))
                        .setView(helper.getView())
                        .setOnDismissListener(helper)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                color = helper.getColorPickerView().getColor();
                                tuyaView.setColor(color);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                helper.setDialog(dialog);
                dialog.show();
    }

    private class ThicknessDialogHelper implements DialogInterface.OnClickListener, Slider.OnSetProgressListener {

        private View mView;
        private ThicknessPreviewView mTpv;
        private Slider mSlider;

        @SuppressLint("InflateParams")
        public ThicknessDialogHelper() {
            mView = getLayoutInflater().inflate(R.layout.dialog_thickness, null);
            mTpv = (ThicknessPreviewView) mView.findViewById(R.id.thickness_preview_view);
            mSlider = (Slider) mView.findViewById(R.id.slider);

            mTpv.setThickness(tuyaView.getSize());
            mTpv.setColor(tuyaView.getColor());
            mSlider.setProgress((int) LayoutUtils.pix2dp(DoodleActivity.this, tuyaView.getSize()));
            mSlider.setOnSetProgressListener(this);
        }

        public View getView() {
            return mView;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (DialogInterface.BUTTON_POSITIVE == which) {
                tuyaView.setSize(LayoutUtils.dp2pix(DoodleActivity.this, mSlider.getProgress()));
            }
        }

        @Override
        public void onSetProgress(Slider slider, int newProgress, int oldProgress, boolean byUser, boolean confirm) {
            mTpv.setThickness(LayoutUtils.dp2pix(DoodleActivity.this, newProgress));
        }
    }

    private void showThicknessDialog() {
        ThicknessDialogHelper helper = new ThicknessDialogHelper();
        new AlertDialog.Builder(DoodleActivity.this)
                .setView(helper.getView())
                .setPositiveButton(android.R.string.ok, helper)
                .show();
    }
}
