package net.wezu.jxg.ui.user.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.model.UserAddress;
import net.wezu.jxg.model.UserModel;
import net.wezu.jxg.service.UserService;
import net.wezu.jxg.ui.SelectPictureDialog;
import net.wezu.jxg.ui.user.ListCategoriesActivity;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.LabeledTextItem;
import net.wezu.widget.MaterialDialog;
import net.wezu.widget.RoundImageview.RoundedNetImageView;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.ui.base.BaseActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author snox@live.com
 * @date 2015/10/24.
 */
public class PersonalProfileActivity extends BaseActivity {

    public static final String IMAGEFILEPATH = "ImageFilePath";

    @Bind(R.id.edit_nickname) LabeledTextItem editNickname;
    @Bind(R.id.img_user_avatar) RoundedNetImageView image;
    @Bind(R.id.edit_gender)  LabeledTextItem editGender;
    @Bind(R.id.edit_vat)  LabeledTextItem editVat;
    @Bind(R.id.edit_mobile)  LabeledTextItem editPhoneNumber;
    @Bind(R.id.edit_payment_account)  LabeledTextItem editPaymentAccount;
    @Bind(R.id.edit_company_name)  LabeledTextItem editCompanyName;
    @Bind(R.id.btn_address_list)  LabeledTextItem editAddressList;
    @Bind(R.id.et_company_address)  LabeledTextItem editCompanyAddress;
    @Bind(R.id.view_for_worker) LinearLayout viewForWorker;

    private SelectPictureDialog selectPictureDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_profile);

        setTitle("个人资料");

        setDefaultBackButton();

        selectPictureDialog = new SelectPictureDialog(this, new SelectPictureDialog.OnGetImageFile() {
            @Override
            public void onGet(String imageFilePath) {
                uploadAvatar(imageFilePath);
            }
        });

        image.setDefaultImageResId(Application.getInstance().isWorker() ? R.mipmap.bg_default_avata : R.mipmap.user);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateDataToView();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(IMAGEFILEPATH)) {
            if (selectPictureDialog != null) {
                selectPictureDialog.setCameraFile(new File(savedInstanceState.getString(IMAGEFILEPATH)));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (selectPictureDialog != null && selectPictureDialog.getCameraFile() != null) {
            outState.putString(IMAGEFILEPATH, selectPictureDialog.getCameraFile().getPath());
        }
    }

    private void updateDataToView() {
        UserModel result = Application.getInstance().getUserModel();
        UserAddress address = Application.getInstance().getUserAddress();

        boolean isWorker = Application.getInstance().isWorker();

        editNickname.setValue(result.DisplayName);
        editPhoneNumber.setValue(result.Username);

        viewForWorker.setVisibility(isWorker ? View.VISIBLE : View.GONE);

        String avator = Application.getInstance().getUserAvatar();
        if (!TextUtils.isEmpty(avator)) {
            try {
                image.setImageUrl(avator, Application.getInstance().getImageLoader());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String value = result.Profile.getProperty("Gender");
        switch (value) {
            case "": editGender.setValue("保密"); break;
            case "F": editGender.setValue("女"); break;
            case "M": editGender.setValue("男"); break;
        }


        if (isWorker) {
            editNickname.showHideArrow(false);
            editGender.showHideArrow(false);
        }

        editPhoneNumber.showHideArrow(false);

        String vat = result.Profile.getProperty("VAT");
        editVat.setValue(vat != null && vat.equals("true") ? "开通" : "未开通");
        editVat.setVisibility(View.GONE);
        editPaymentAccount.setValue(result.Profile.getProperty("PaymentAccount"));
        editCompanyName.setValue(result.Profile.getProperty("CompanyName"));

        if (!TextUtils.isEmpty(address.Address)) {
            editCompanyAddress.setValue(address.Address);
        } else  {
            editCompanyAddress.setValue("请编辑您的公司地址");
        }

        UserService.getAddresses(requestTag, "billing", new RequestManager.ResponseListener<List<UserAddress>>() {
            @Override
            public void success(List<UserAddress> result, String msg) {

                for (UserAddress address : result) {
                    if (address.IsDefault == 1) {
                        editAddressList.setValue(address.Address);
                        return;
                    }
                }
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });
    }

    @OnClick(R.id.btn_address_list) void showAddressList() {
        startActivity(new Intent(PersonalProfileActivity.this, UserAddressesListActivity.class));
    }

    @OnClick(R.id.btn_categories_list) void showCategoriesList() {
        startActivity(new Intent(this, ListCategoriesActivity.class));
    }

    private static final int REQUEST_CODE_CHANGE_NICKNAME = 1;
    private static final int REQUEST_CODE_CHANGE_GENDER = 2;
    private static final int REQUEST_CODE_EDIT_COMPANY_ADDRESS = 3;

    @OnClick(R.id.edit_nickname) void modifyNickname() {
        if (!Application.getInstance().isWorkerPackage()) {
            showForm("修改昵称", "昵称", "请输入您的昵称", "displayname", Application.getInstance().getUserModel().DisplayName, 6);
        }
    }

    @OnClick(R.id.edit_payment_account) void modifyPaymentAccount() {
        //showForm("修改支付账号", "支付账号", "请输入您的支付账号", "PaymentAccount", Application.getInstance().getUserModel().Profile.getProperty("PaymentAccount"), 50);

        Intent intent = new Intent(this, ChangePaymentAccountActivity.class);
        intent.putExtra("PaymentAccount", Application.getInstance().getUserModel().Profile.getProperty("PaymentAccount"));

        startActivityForResult(intent, REQUEST_CODE_CHANGE_NICKNAME);
    }

    @OnClick(R.id.edit_company_name)
    void modifyCompanyName() {
        showForm("修改公司名称", "公司名称", "请输入您的公司名称", "CompanyName", Application.getInstance().getUserModel().Profile.getProperty("CompanyName"), 30);
    }

    @OnClick(R.id.et_company_address) void editCompanyAddress() {
        Intent intent = new Intent(this, UserAddressEditActivity.class);
        intent.putExtra("companyAddress", Application.getInstance().getUserAddress());

        startActivityForResult(intent, REQUEST_CODE_EDIT_COMPANY_ADDRESS);
    }

    private void showForm(String title, String label, String hint, String field, String defaultValue, int max) {
        if (FastClickUtil.isFastClick()) return;

        Intent intent = new Intent(this, EditTextActivity.class);
        intent.putExtra("value", defaultValue);
        intent.putExtra("field", field);
        intent.putExtra("label", label);
        intent.putExtra("hint", hint);
        intent.putExtra("title", title);
        intent.putExtra("max", max);

        startActivityForResult(intent, REQUEST_CODE_CHANGE_NICKNAME);
    }

    @OnClick(R.id.edit_gender) void modifyGender() {
        if (FastClickUtil.isFastClick()) return;

        if (!Application.getInstance().isWorkerPackage()) {
            startActivityForResult(new Intent(this, ChangeGenderActivity.class), REQUEST_CODE_CHANGE_GENDER);
        }
    }

    @OnClick(R.id.edit_vat) void modifyVat() {
        if (FastClickUtil.isFastClick()) return;

        startActivityForResult(new Intent(this, ChangeVatActivity.class), REQUEST_CODE_CHANGE_GENDER);
    }

    /**
     * 更换头像
     * @param v
     */
    @OnClick({R.id.img_user_avatar, R.id.txt_change_avatar}) void changeAvatar(View v) {
        if (FastClickUtil.isFastClick()) return;

        // 机修工用户不允许修改头像
        if (!Application.getInstance().isWorkerPackage()) {
            selectPictureDialog.show(v);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        if (selectPictureDialog != null) {
            selectPictureDialog.onActivityResult(requestCode, data);
        }

        switch (requestCode) {
            case REQUEST_CODE_CHANGE_NICKNAME:
            case REQUEST_CODE_CHANGE_GENDER:
                updateDataToView();
                break;

            case REQUEST_CODE_EDIT_COMPANY_ADDRESS:
                Application.getInstance().setUserAddress((UserAddress)data.getParcelableExtra("address"));
                updateDataToView();
                break;

//            case REQUEST_CODE_CAMERA:
//                if (cameraFile != null && cameraFile.exists()) {
//                    uploadAvatar(cameraFile.getAbsolutePath());
//                }
//                dismissPopupWindow();
//                break;
//
//            case REQUEST_CODE_LOCAL:
//                uploadAvatar(FileHelper.getPath(this, data.getData()));
//                dismissPopupWindow();
//                break;
        }
    }

    private void uploadAvatar(String filePath) {

        getLoadingDialog("正在上传头像").show();

        RequestManager.getInstance().uploadAvatar(filePath, new RequestManager.ResponseListener<String>() {
            @Override
            public void success(String result, String msg) {
                ToastUtils.show(PersonalProfileActivity.this, "更新成功");

                Application.getInstance().setUserAvatarUpdated();

                try {
                    image.setImageUrl(Application.getInstance().getUserAvatar(), Application.getInstance().getImageLoader(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dismissLoadingDialog();
            }

            @Override
            public void error(String msg) {
                ToastUtils.show(PersonalProfileActivity.this, msg);

                dismissLoadingDialog();
            }
        });

    }

    private void showInputDialog(String title, String defaultValue, final OnInputResultListener listener) {
        final EditText input = new EditText(this);
        input.setText(defaultValue);
        input.setFocusable(true);

        new MaterialDialog(this).setTitle(title)
                .setContentView(input)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.Result(input.getText().toString());
                        }
                    }
                })
                .setNegativeButton("取消")
                .show();
    }

    private void updateProfile(String field, final String value, final OnUpdatedListener listener) {

        Map<String, String> parameter = new HashMap<>();
        parameter.put("fields", field);
        parameter.put(field, value);

        RequestManager.getInstance().post("updateprofile", requestTag, parameter, UserModel.class, new RequestManager.ResponseListener<UserModel>() {

            @Override
            public void success(UserModel result, String msg) {
                Application.getInstance().setUserModel(result);

                listener.success(value);
            }

            @Override
            public void error(String msg) {
                ToastUtils.show(PersonalProfileActivity.this, "更新失败");
            }
        });
    }

    public interface OnInputResultListener {
        void Result(String result);
    }

    private interface OnUpdatedListener {
        void success(String value);
    }
}