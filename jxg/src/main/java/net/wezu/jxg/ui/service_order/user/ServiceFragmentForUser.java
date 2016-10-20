package net.wezu.jxg.ui.service_order.user;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;

import net.wezu.jxg.ui.service_order.user.CreateServiceOrderActivity;
import net.wezu.widget.CircleButton;
import net.wezu.jxg.R;
import net.wezu.jxg.ui.base.BaseFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 紧急服务
 *
 * @author snox@live.com
 * @date 2015/10/21.
 */
public class ServiceFragmentForUser extends BaseFragment {

    private int mServiceType;

    @Bind(R.id.btn_switch)
    CircleButton switchButton;

    @Bind(R.id.layout_root)
    RelativeLayout layout;

    @Bind(R.id.service_01)
    CircleButton btnService01;
    @Bind(R.id.service_02)
    CircleButton btnService02;
    @Bind(R.id.service_03)
    CircleButton btnService03;
    @Bind(R.id.service_04)
    CircleButton btnService04;
    @Bind(R.id.service_05)
    CircleButton btnService05;
    @Bind(R.id.service_06)
    CircleButton btnService06;
    @Bind(R.id.service_07)
    CircleButton btnService07;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_main_service);

        setServiceType(CreateServiceOrderActivity.ST_SERVICE);
    }

    @OnClick(R.id.btn_switch) void switchMode() {
        setServiceType(isService() ? CreateServiceOrderActivity.ST_PREORDER : CreateServiceOrderActivity.ST_SERVICE);
    }

    private boolean isService() {
        return mServiceType == CreateServiceOrderActivity.ST_SERVICE;
    }

    private void setServiceType(int serviceType) {
        mServiceType = serviceType;

        switch (mServiceType) {
            case CreateServiceOrderActivity.ST_PREORDER:
                layout.setBackgroundColor(Color.parseColor("#97c8ff"));
                switchButton.setColor(Color.parseColor("#9945be17"));
                switchButton.setImageResource(R.mipmap.btn_2);
                btnService01.setImageResource(R.mipmap.ds_01);
                btnService02.setImageResource(R.mipmap.ds_02);
                btnService03.setImageResource(R.mipmap.ds_03);
                btnService04.setImageResource(R.mipmap.ds_04);
                btnService05.setImageResource(R.mipmap.ds_05);
                btnService06.setImageResource(R.mipmap.ds_06);
                btnService07.setImageResource(R.mipmap.ds_07);
                break;

            case CreateServiceOrderActivity.ST_SERVICE:
                layout.setBackgroundColor(Color.parseColor("#ffabab"));
                switchButton.setColor(Color.parseColor("#99ff4948"));
                switchButton.setImageResource(R.mipmap.btn_1);
                switchButton.setImageResource(R.mipmap.btn_2);
                btnService01.setImageResource(R.mipmap.es_01);
                btnService02.setImageResource(R.mipmap.es_02);
                btnService03.setImageResource(R.mipmap.es_03);
                btnService04.setImageResource(R.mipmap.es_04);
                btnService05.setImageResource(R.mipmap.es_05);
                btnService06.setImageResource(R.mipmap.es_06);
                btnService07.setImageResource(R.mipmap.es_07);
                break;
        }
    }

    // 送油/
    @OnClick(R.id.service_01) void on01() {
        CreateServiceOrderActivity.createService(getActivity(), mServiceType, isService() ? "送油" : "代办");
    }

    // 事故咨询/
    @OnClick(R.id.service_02) void on02() {
        CreateServiceOrderActivity.createService(getActivity(), mServiceType, isService() ? "事故咨询" : "改装");
    }

    // 其它/
    @OnClick(R.id.service_03) void on03() {
        CreateServiceOrderActivity.createService(getActivity(), mServiceType, isService() ? "其它" : "其他");
    }

    // 开锁/配钥匙
    @OnClick(R.id.service_04) void on04() {
        CreateServiceOrderActivity.createService(getActivity(), mServiceType, isService() ? "开锁" : "配钥匙");
    }

    // 帮电/
    @OnClick(R.id.service_05) void on05() {
        CreateServiceOrderActivity.createService(getActivity(), mServiceType, isService() ? "帮电" : "检查");
    }

    // 拖车/
    @OnClick(R.id.service_06) void on06() {
        CreateServiceOrderActivity.createService(getActivity(), mServiceType, isService() ? "拖车" : "喷漆");
    }

    // 换胎/保养
    @OnClick(R.id.service_07) void on07() {
        CreateServiceOrderActivity.createService(getActivity(), mServiceType, isService() ? "换胎" : "保养");
    }


}
