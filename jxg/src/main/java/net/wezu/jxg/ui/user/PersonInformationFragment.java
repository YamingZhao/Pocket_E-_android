package net.wezu.jxg.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.wezu.jxg.model.UserModel;
import net.wezu.jxg.ui.user.profile.PersonalProfileActivity;
import net.wezu.jxg.ui.user.settings.PersonalSettingActivity;
import net.wezu.jxg.ui.user.wallet.WorkerWalletActivity;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.LabeledTextItem;
import net.wezu.widget.RoundImageview.RoundedNetImageView;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.ui.LoginActivity;
import net.wezu.jxg.ui.service_order.ServiceOrderActivity;
import net.wezu.jxg.ui.user.cars.UserCarListActivity;
import net.wezu.jxg.ui.base.BaseFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 个人信息
 *
 * @author snox@live.com
 * @date 2015/10/21.
 */
public class PersonInformationFragment extends BaseFragment {

    @Bind(R.id.txt_display_name) TextView tvDisplayName;
    @Bind(R.id.img_user_avatar) RoundedNetImageView imgAvatar;

    @Bind(R.id.btn_user_car_list) LabeledTextItem cars;
    @Bind(R.id.view_withdraw) LabeledTextItem withItem;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_personal_information);

        imgAvatar.setDefaultImageResId(Application.getInstance().isWorker() ? R.mipmap.bg_default_avata : R.mipmap.user);

        if (!Application.getInstance().isWorker()) {
            withItem.setVisibility(View.GONE);
        } else {
            cars.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateDataToView();
    }

    private void updateDataToView() {
        UserModel result = Application.getInstance().getUserModel();
        if (result == null) return;

        if (tvDisplayName != null && !TextUtils.isEmpty(result.DisplayName)) {
            tvDisplayName.setText(result.DisplayName);
        }
        if (!TextUtils.isEmpty(result.Profile.Website)) {
            try {
                imgAvatar.setImageUrl(Application.getInstance().getUserAvatar(), Application.getInstance().getImageLoader());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 个人信息
     */
    @OnClick({R.id.txt_display_name, R.id.img_user_avatar}) void showUserProfile() {
        if (FastClickUtil.isFastClick()) return;

        startActivity(new Intent(getActivity(), PersonalProfileActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case 100:
                startActivity(new Intent(getActivity(), LoginActivity.class).putExtra("logout", true));
                getActivity().finish();
                break;
        }
    }

    /**
     * 用户订单列表
     */
    @OnClick(R.id.txt_service_order) void showServiceOrders() {
        if (FastClickUtil.isFastClick()) return;

        startActivity(new Intent(getActivity(), ServiceOrderActivity.class));
    }

    /**
     * 车辆列表
     */
    @OnClick(R.id.btn_user_car_list) void showCarList() {
        if (FastClickUtil.isFastClick()) return;

        startActivity(new Intent(getActivity(), UserCarListActivity.class));
    }

    /**
     * 设置
     */
    @OnClick(R.id.view_settings) void showSettings() {
        if (FastClickUtil.isFastClick()) return;

        startActivityForResult(new Intent(getActivity(), PersonalSettingActivity.class), 100);
    }

    @OnClick(R.id.view_coupon) void showCoupon() {
        if (FastClickUtil.isFastClick()) return;

        MyCouponListActivity.startActivity(getActivity());
    }

    @OnClick(R.id.view_withdraw) void showWithdraw() {
        if (FastClickUtil.isFastClick()) return;

        startActivity(new Intent(getActivity(), WorkerWalletActivity.class));
    }

    /**
     * 消息中心
     */
//    @OnClick(R.id.btn_message) void showMessages() {
//        MessagesListActivity.startActivity(getActivity());
//    }
}
