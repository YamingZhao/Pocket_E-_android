package net.wezu.jxg.ui.user.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.UserAddress;
import net.wezu.jxg.service.UserService;
import net.wezu.jxg.ui.base.BaseListActivity;
import net.wezu.jxg.ui.base.BaseViewHolder;
import net.wezu.jxg.util.FastClickUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemLongClick;

/**
 * @author snox@live.com
 * @date 2015/10/24.
 */
public class UserAddressesListActivity extends BaseListActivity<UserAddress, UserAddressesListActivity.UserAddressViewHolder> {

    private static String TAG_FOR_RESULT = "for_result";
    public static String RESULT_TAG = "result";

    private boolean isNeedReturn;

    public static void startActivity(Activity activity) {
        activity.startActivity(new Intent(activity, UserAddressesListActivity.class));
    }

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, UserAddressesListActivity.class);
        intent.putExtra(TAG_FOR_RESULT, true);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("收货地址");
        setDefaultBackButton();

        isNeedReturn = getIntent().getBooleanExtra(TAG_FOR_RESULT, false);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_user_address_list);
    }

    @Override
    protected void refreshData() {
        setRefreshing(true);

        UserService.getAddresses(requestTag, "billing", new RequestManager.ResponseListener<List<UserAddress>>() {
            @Override
            public void success(List<UserAddress> result, String msg) {
                clear();
                addDataItems(result);

                setRefreshing(false);
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });
    }

    @Override
    protected boolean equalItem(UserAddress item1, UserAddress item2) {
        return false;
    }

    @Override
    protected int getListItemLayoutResourceId() {
        return R.layout.listitem_user_address;
    }

    @Override
    protected UserAddressViewHolder createViewHolder(Context context, View convertView) {
        return new UserAddressViewHolder(UserAddressesListActivity.this, convertView);
    }

    @OnItemLongClick(android.R.id.list) boolean deleteAddress(int position) {
        // TODO 用户地址删除功能
        ToastUtils.show(this, getItem(position).ReceiverName);
        return true;
    }

    public class UserAddressViewHolder extends BaseViewHolder<UserAddress> {

        @Bind(R.id.btn_edit) View btn_edit;
        @Bind(R.id.txt_name) TextView txtUsername;
        @Bind(R.id.txt_mobile) TextView txtMobile;
        @Bind(R.id.txt_address) TextView txtAddress;
        @Bind(R.id.txt_isdefault) TextView txtIsDefault;


        public UserAddressViewHolder(Context context, View view) {
            super(context, view);
        }

        @Override
        public void setData(final UserAddress data) {
            txtUsername.setText(data.ReceiverName);
            txtMobile.setText(data.ReceiverPhone);
            txtAddress.setText(data.Address);
            txtIsDefault.setVisibility(data.IsDefault == 1 ? View.VISIBLE : View.GONE);

            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FastClickUtil.isFastClick()) return;
                    openEditForm(data);
                }
            });

            if (isNeedReturn) {
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (FastClickUtil.isFastClick()) return;
                        selectAddress(data);
                    }
                });
            }
        }
    }

    @OnClick(R.id.btn_new) void newAddress() {
        if (FastClickUtil.isFastClick()) return;
        openEditForm(null);
    }

    private void openEditForm(UserAddress address) {

        Intent intent = new Intent(UserAddressesListActivity.this, UserAddressEditActivity.class);
        if (address != null) {
            intent.putExtra("userAddress", address);
        }

        startActivityForResult(intent, 1);
    }

    private void selectAddress(UserAddress address) {
        Intent result = new Intent();
        result.putExtra(RESULT_TAG, address);

        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode!= 1 || resultCode != RESULT_OK) return;

        refreshData();
    }
}
