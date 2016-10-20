package net.wezu.jxg.ui.user.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import net.wezu.framework.util.BitmapUtil;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.Category;
import net.wezu.jxg.model.UserModel;
import net.wezu.jxg.service.UserService;
import net.wezu.jxg.ui.SelectPictureDialog;
import net.wezu.jxg.ui.base.BaseFragment;
import net.wezu.jxg.ui.payment.WXUtil;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.LabeledTextItem;
import net.wezu.widget.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 机修工注册第三步，上传头像/身份证等等，正式注册
 *
 * Created by snox on 2016/3/24.
 */
public class Step3WorkerRegisterFragment extends BaseFragment {

    public static final String IMAGE_FILE_PATH = "ImageFilePath";

    @Bind(R.id.img_select_id1) ImageView imgIdCard1;
    @Bind(R.id.img_select_id2) ImageView imgIdCard2;
    @Bind(R.id.img_select_id3) ImageView imgIdCard3;
    @Bind(R.id.img_select_id4) ImageView imgIdCard4;
    @Bind(R.id.img_select_work_lic1) ImageView imgWorkLic1;
    @Bind(R.id.img_select_work_lic2) ImageView imgWorkLic2;
    @Bind(R.id.img_select_work_lic3) ImageView imgWorkLic3;
    @Bind(R.id.img_select_work_lic4) ImageView imgWorkLic4;
    @Bind(R.id.img_user_avatar) ImageView imgAvatar;
    @Bind(R.id.lti_work_items) LabeledTextItem service_items;

    public Step3WorkerRegisterFragment() {
        files = new HashMap<>();
    }

    private Map<String, String> registerInformation;
    private Map<String, File> files;

    public void setRegisterInformation(Map<String, String> registerInformation) {
        this.registerInformation = registerInformation;
    }

    private SelectPictureDialog selectPictureDialog;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        setContentView(R.layout.fragment_register_worker_step3);

        selectPictureDialog = new SelectPictureDialog(this, null);
    }

    @OnClick({
            R.id.img_select_id1, R.id.img_select_id2, R.id.img_select_id3, R.id.img_select_id4,
            R.id.img_select_work_lic1, R.id.img_select_work_lic2, R.id.img_select_work_lic3, R.id.img_select_work_lic4,
            R.id.img_user_avatar}) void selectIdCard(final View v) {
        if (FastClickUtil.isFastClick()) return;

        selectPictureDialog.show(v, new SelectPictureDialog.OnGetImageFile() {
            @Override
            public void onGet(String path) {
                Bitmap bitmap = WXUtil.extractThumbNail(path, 800, 800, true);
                if (bitmap != null) {

                    switch (v.getId()) {
                        case R.id.img_select_id1:
                            files.put("identity1", new File(path));
                            imgIdCard1.setImageBitmap(bitmap);
                            break;
                        case R.id.img_select_id2:
                            files.put("identity2", new File(path));
                            imgIdCard2.setImageBitmap(bitmap);
                            break;
                        case R.id.img_select_id3:
                            files.put("identity3", new File(path));
                            imgIdCard3.setImageBitmap(bitmap);
                            break;
                        case R.id.img_select_id4:
                            files.put("identity4", new File(path));
                            imgIdCard4.setImageBitmap(bitmap);
                            break;
                        case R.id.img_select_work_lic1:
                            files.put("workcert1", new File(path));
                            imgWorkLic1.setImageBitmap(bitmap);
                            break;
                        case R.id.img_select_work_lic2:
                            files.put("workcert2", new File(path));
                            imgWorkLic2.setImageBitmap(bitmap);
                            break;
                        case R.id.img_select_work_lic3:
                            files.put("workcert3", new File(path));
                            imgWorkLic3.setImageBitmap(bitmap);
                            break;
                        case R.id.img_select_work_lic4:
                            files.put("workcert4", new File(path));
                            imgWorkLic4.setImageBitmap(bitmap);
                            break;
                        case R.id.img_user_avatar:
                            files.put("avatar", new File(path));
                            imgAvatar.setImageBitmap(bitmap);
                            break;
                    }
                }
            }
        });
    }

    // 申请项目
    @OnClick(R.id.lti_work_items) void selectWorkItems() {
        if (FastClickUtil.isFastClick()) return;

        Intent intent = new Intent(getActivity(), ServiceCategoryActivity.class);
        intent.putParcelableArrayListExtra("service_items", selectedCategories);
        startActivityForResult(intent, 100);
    }

    @OnClick(R.id.btn_next_step) void register(View v) {
        if (FastClickUtil.isFastClick()) return;

//        if (selectedCategories == null || selectedCategories.size() == 0) {
//            toast("请选择申请项目");
//            return;
//        }

//        String categories = "";
//        for (Category category : selectedCategories) {
//            if (!TextUtils.isEmpty(categories)) categories += ",";
//            categories += category.CategoryId;
//        }
//
//        registerInformation.put("items", categories);

//        if (!files.containsKey("identity1")) {
//            toast("请上传身份证信息");
//            return;
//        }

//        if (!files.containsKey("workcert1")) {
//            toast("请上传工作证信息");
//            return;
//        }

        if (!files.containsKey("avatar")) {
            toast("请上传头像");
            return;
        }

        getLoadingDialog("正在注册用户，请稍候").show();

        UserService.register(requestTag, registerInformation, files, new RequestManager.ResponseListener<UserModel>() {
            @Override
            public void success(UserModel result, String msg) {

                dismissLoadingDialog();

                showRegisterAduiting();
//                getActivity().setResult(Activity.RESULT_CANCELED);
//                getActivity().finish();
            }

            @Override
            public void error (String msg){
                toast(msg);
                dismissLoadingDialog();
            }
        });
    }

    // region

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(IMAGE_FILE_PATH)) {
            if (selectPictureDialog != null) {
                selectPictureDialog.setCameraFile(new File(savedInstanceState.getString(IMAGE_FILE_PATH)));
            }
        }

        if (savedInstanceState != null && registerInformation != null) {
            registerInformation.clear();
            for (String key : savedInstanceState.keySet()) {
                if (key.equals(IMAGE_FILE_PATH)) continue;
                registerInformation.put(key, savedInstanceState.getString(key));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (selectPictureDialog != null && selectPictureDialog.getCameraFile() != null) {
            outState.putString(IMAGE_FILE_PATH, selectPictureDialog.getCameraFile().getPath());
        }

        if (registerInformation != null) {
            for (String key : registerInformation.keySet()) {
                outState.putString(key, registerInformation.get(key));
            }
        }
    }

    private ArrayList<Category> selectedCategories;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == 100) {
            selectedCategories = data.getParcelableArrayListExtra("data");

            String value = "";
            for (Category category : selectedCategories) {
                if (!TextUtils.isEmpty(value)) value += ",";
                value += category.Name;
            }
            service_items.setValue(value);
        }

        if (selectPictureDialog != null) {
            selectPictureDialog.onActivityResult(requestCode, data);
        }
    }

    // endregion

    private void showRegisterAduiting() {
        final MaterialDialog dialog = new MaterialDialog(getActivity());
        dialog.setMessage("注册成功时能提示注册成功，请等待客服审核")
                .setTitle("审核中")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        dialog.show();
    }
}
