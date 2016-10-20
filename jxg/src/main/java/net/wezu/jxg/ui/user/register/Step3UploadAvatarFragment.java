package net.wezu.jxg.ui.user.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.ui.base.BaseFragment;
import net.wezu.jxg.ui.SelectPictureDialog;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.RoundImageview.RoundedNetImageView;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author snox@live.com
 * @date 2015/10/27.
 */
public class Step3UploadAvatarFragment extends BaseFragment {

    public static final String IMAGE_FILE_PATH = "ImageFilePath";

    @Bind(R.id.img_user_avatar) RoundedNetImageView  imgAvatar;

    private String token;
    private String username;
    private String password;

    private SelectPictureDialog changeAvatarDialog;


    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        setContentView(R.layout.fragment_register_avarta);

        this.username = getArguments().getString("username");
        this.password = getArguments().getString("password");

        this.token = username + "," + password;

        changeAvatarDialog = new SelectPictureDialog(this, new SelectPictureDialog.OnGetImageFile() {
            @Override
            public void onGet(String imageFilePath) {
                uploadAvatar(imageFilePath);
            }
        });

        imgAvatar.setDefaultImageResId(R.mipmap.upload_avatar_logo);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(IMAGE_FILE_PATH)) {
            if (changeAvatarDialog != null) {
                changeAvatarDialog.setCameraFile(new File(savedInstanceState.getString(IMAGE_FILE_PATH)));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (changeAvatarDialog != null && changeAvatarDialog.getCameraFile() != null) {
            outState.putString(IMAGE_FILE_PATH, changeAvatarDialog.getCameraFile().getPath());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        if (changeAvatarDialog != null) {
            changeAvatarDialog.onActivityResult(requestCode, data);
        }
    }

    @OnClick(R.id.button_upload) void showUpdateMenu(View v) {
        if (FastClickUtil.isFastClick()) return;
        changeAvatarDialog.show(v);
    }


    @OnClick(R.id.tv_skip) void skip() {
        if (FastClickUtil.isFastClick()) return;
        finish();
    }

    private void finish() {

//        if (Application.getInstance().isWorkerPackage()) {
//            Bundle bundle = new Bundle();
//            bundle.putString("token", token);
//            bundle.putString("username", username);
//            bundle.putString("password", password);
//
//            Step4RegisterServiceFragment fragment = new Step4RegisterServiceFragment();
//            fragment.setArguments(bundle);
//
//            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content,
//                    fragment).commitAllowingStateLoss();
//        } else {
            getActivity().setResult(Activity.RESULT_OK, new Intent().putExtra("username", username).putExtra("password", password));
            getActivity().finish();
//        }
    }

    private void uploadAvatar(String filePath) {

        getLoadingDialog("正在上传头像").show();

        RequestManager.getInstance().uploadAvatar(filePath, token, new RequestManager.ResponseListener<String>() {
            @Override
            public void success(String result, String msg) {
                toast("更新成功");

                try {
                    imgAvatar.setImageUrl(result, Application.getInstance().getImageLoader(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dismissLoadingDialog();

                finish();
            }

            @Override
            public void error(String msg) {
                toast(msg);

                dismissLoadingDialog();
            }
        });

    }
}
