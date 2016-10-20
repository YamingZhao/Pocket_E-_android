package net.wezu.jxg.ui.user.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.wezu.framework.util.StringUtil;
import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.ui.user.AreaSelectorActivity;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.LabeledTextItem;
import net.wezu.widget.MaterialDialog;
import net.wezu.widget.ToggleButton;
import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.UserAddress;
import net.wezu.jxg.ui.base.BaseActivity;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author snox@live.com
 * @date 2015/10/24.
 */
public class UserAddressEditActivity extends BaseActivity {

    private static final int REQUEST_CODE_SELECT_AREA = 1;

    @Bind(R.id.txt_name) EditText editName;
    @Bind(R.id.txt_mobile) EditText editMobile;
    @Bind(R.id.btn_select_area) LabeledTextItem btnSelectArea;
    @Bind(R.id.txt_address) EditText editAddress;
    @Bind(R.id.tb_isdefault) ToggleButton btnIsDefault;
    @Bind(R.id.ll_isdefault) LinearLayout viewIsDefault;

    private UserAddress address;

    private boolean isCompanyAddress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_address_edit);

        setDefaultBackButton();

        Intent intent = getIntent();
        if (intent.hasExtra("userAddress")) {
            address = intent.getParcelableExtra("userAddress");
        } else if (intent.hasExtra("companyAddress")) {
            address = intent.getParcelableExtra("companyAddress");
            isCompanyAddress = true;
        } else {
            address = new UserAddress();
        }

        btnIsDefault.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                address.IsDefault = on ? 1 : 0;
            }
        });

        updateDataToView();
    }

    private void updateDataToView() { //(UserAddress name) {

        if (!isCompanyAddress) {
            if (address.UserAddressId > 0) {
                setTitle("编辑地址");

                setRightButton("删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (FastClickUtil.isFastClick()) return;

                        delete();
                    }
                });
            } else {
                setTitle("新建地址");
            }
        } else {
            setTitle("修改公司地址");
            viewIsDefault.setVisibility(View.GONE);
        }

        editName.setText(address.ReceiverName);
        editMobile.setText(address.ReceiverPhone);
        if (!TextUtils.isEmpty(address.District) && !TextUtils.isEmpty(address.Region)) {
            btnSelectArea.setValue(address.Region + " - " + address.District);
        }
        editAddress.setText(address.Address);
        if (address.IsDefault == 1)
            btnIsDefault.setToggleOn();
        else
            btnIsDefault.setToggleOff();
    }

    private void delete() {


        new MaterialDialog(this)
                .setTitle("删除确认")
                .setMessage("您确认需要删除地址吗？")
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> parameters = new HashMap<>();
                        parameters.put("id", String.valueOf(address.UserAddressId));

                        RequestManager.getInstance().post("deleteuseraddress", requestTag, parameters, UserAddress.class, new RequestManager.ResponseListener<UserAddress>() {

                            @Override
                            public void success(UserAddress result, String msg) {
                                ToastUtils.show(UserAddressEditActivity.this, "删除地址成功");
                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void error(String msg) {
                                toast(msg);
                            }
                        });
                    }
                })
                .setNegativeButton("取消")
                .show();
    }

    @OnClick(R.id.btn_select_area)
    public void selectArea() {
        updateEditData();
        startActivityForResult(new Intent(UserAddressEditActivity.this, AreaSelectorActivity.class), REQUEST_CODE_SELECT_AREA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_CODE_SELECT_AREA:
                address.District = data.getStringExtra("city");
                address.Region = data.getStringExtra("region");
                updateDataToView();
                break;
        }
    }

    private void updateEditData() {
        address.ReceiverName = editName.getText().toString();
        address.ReceiverPhone = editMobile.getText().toString();
        address.Address = editAddress.getText().toString();
    }


    @OnClick(R.id.btn_new) void save() {
        if (FastClickUtil.isFastClick()) return;

        updateEditData();

        if (TextUtils.isEmpty(address.ReceiverName)) {
            ToastUtils.show(this, "请填写联系人");
            editName.requestFocus();
            return;
        }

        if (!StringUtil.isMobileNO(address.ReceiverPhone)) {
            ToastUtils.show(this, "请填写联系人手机号");
            editMobile.setError("请填写联系人手机号");
            editMobile.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address.Address)) {
            ToastUtils.show(this, "请填写详细地址");
            editAddress.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address.Region) || TextUtils.isEmpty(address.District)) {
            ToastUtils.show(this, "请选择所在地区");
            return;
        }

        String method;
        HashMap<String, String> parameters = new HashMap<>();

        if (address.UserAddressId > 0) {
            parameters.put("id", String.valueOf(address.UserAddressId));
            method = "updateuseraddress";
        } else {
            method = "adduseraddress";
        }
        parameters.put("receivername", address.ReceiverName);
        parameters.put("receiverphone", address.ReceiverPhone);
        parameters.put("type", TextUtils.isEmpty(address.Type) ? "billing" : address.Type);
        parameters.put("country", "China");
        parameters.put("region", address.Region);
        parameters.put("district", address.District);
        parameters.put("address", address.Address);
        parameters.put("default", address.IsDefault == 1 ? "true" : "false");


        RequestManager.getInstance().post(method, requestTag, parameters, UserAddress.class, new RequestManager.ResponseListener<UserAddress>(){

            @Override
            public void success(UserAddress result, String msg) {
                toast("保存地址成功");
                setResult(RESULT_OK, new Intent().putExtra("address", result));
                finish();
            }

            @Override
            public void error(String msg) {
                ToastUtils.show(UserAddressEditActivity.this, msg);
            }
        });
    }
}
