package com.cyan.ui;

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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyan.annotation.ActivityFragmentInject;
import com.cyan.common.Constants;
import com.cyan.community.R;
import com.cyan.util.LayoutUtils;
import com.cyan.util.SimpleHandler;
import com.cyan.widget.Slider;
import com.cyan.widget.ThicknessPreviewView;
import com.cyan.widget.TuyaView;
import com.rarepebble.colorpicker.ColorPickerView;

import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

@ActivityFragmentInject(
        contentViewId = R.layout.activity_doodle,
        toolbarTitle = R.string.doodle,
        menuId = R.menu.menu_doodle
)
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
        ButterKnife.inject(this);
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
       startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
   }
public void saveBitmap(){
    dialog = new ProgressDialog(this);
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.setIndeterminate(false);
    dialog.setCancelable(true);
    dialog.setCanceledOnTouchOutside(false);
    dialog.show(DoodleActivity.this, null, "正在保存");
    filePath=Constants.PIC_STORE_PATH+System.currentTimeMillis()+".jpg";
    tuyaView.saveToSDCard(filePath);
}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(tuyaView.savePath.size()>0)
                showSaveDialog();
            else finish();
            return true;
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(tuyaView.savePath.size()>0)
                    showSaveDialog();
                else finish();
                break;
            case R.id.finish:
              saveBitmap();
                break;
            case R.id.delete:
                tuyaView.clear();
                break;
        }
        return false;
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
            mView = DoodleActivity.this.getLayoutInflater().inflate(R.layout.dialog_save, null);
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
                    saveBitmap();
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
                Dialog dialog = new AlertDialog.Builder(DoodleActivity.this)
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
