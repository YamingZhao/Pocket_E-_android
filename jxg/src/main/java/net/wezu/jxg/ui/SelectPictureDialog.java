package net.wezu.jxg.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import net.wezu.framework.util.BitmapUtil;
import net.wezu.framework.util.FileHelper;
import net.wezu.jxg.R;
import net.wezu.jxg.util.BitmapUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by snox on 2016/1/17.
 */
public class SelectPictureDialog {

    private final Activity activity;
    private final Fragment fragment;
    private OnGetImageFile onGetImageFile;

    private PopupWindow popupWindow;

    public File getCameraFile() {
        return cameraFile;
    }

    public void setCameraFile(File cameraFile) {
        this.cameraFile = cameraFile;
    }

    private File cameraFile;

    private int id;

    private static final int REQUEST_CODE_CAMERA = 18;
    private static final int REQUEST_CODE_LOCAL = 19;

    public SelectPictureDialog(Activity activity, OnGetImageFile onGetImageFile) {
        this.activity = activity;
        this.onGetImageFile = onGetImageFile;
        this.fragment = null;
    }

    public SelectPictureDialog(Fragment fragment, OnGetImageFile onGetImageFile) {
        this.activity = fragment.getActivity();
        this.onGetImageFile = onGetImageFile;
        this.fragment = fragment;
    }

    public void show(View v, OnGetImageFile onGetImageFile) {
        this.onGetImageFile = onGetImageFile;

        show(v);
    }

    public void show(View v) {
        View menuView = LayoutInflater.from(activity)
                .inflate(R.layout.menu_upload_pic, null);

        popupWindow = new PopupWindow(menuView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        menuView.findViewById(R.id.btn_from_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow();

                cameraFile = new File(Environment.getExternalStorageDirectory(), "jxg/images/" + System.currentTimeMillis() + ".jpg");
                cameraFile.getParentFile().mkdirs();

                Uri uri = Uri.fromFile(cameraFile);

                if (fragment != null) {
                    fragment.startActivityForResult(
                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)), REQUEST_CODE_CAMERA);
                } else {
                    activity.startActivityForResult(
                            new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)), REQUEST_CODE_CAMERA);
                }
            }
        });

        menuView.findViewById(R.id.btn_from_local).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow();

                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");

                } else {
                    intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                if (fragment != null) {
                    fragment.startActivityForResult(intent, REQUEST_CODE_LOCAL);
                } else {
                    activity.startActivityForResult(intent, REQUEST_CODE_LOCAL);
                }
            }
        });

        menuView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow();
            }
        });

        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void onActivityResult(int requestCode, Intent data) {
        if (onGetImageFile == null) return;

        String imagePath = null;

        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (cameraFile != null && cameraFile.exists()) {
                    imagePath = cameraFile.getAbsolutePath();
                }
                break;

            case REQUEST_CODE_LOCAL:
                imagePath = FileHelper.getPath(activity, data.getData());
                break;
        }

        if (!TextUtils.isEmpty(imagePath)) {

            File file = new File(Environment.getExternalStorageDirectory(), "jxg/images/thumb/" + System.currentTimeMillis() + ".jpg");
            file.getParentFile().mkdirs();

            try {
                BitmapUtils.saveBitmap(imagePath, 800, 800, 50, true, file.getAbsolutePath());

                onGetImageFile.onGet(file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public interface OnGetImageFile {
        void onGet(String imageFilePath);
    }
}
