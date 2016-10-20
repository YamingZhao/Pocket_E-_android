package net.wezu.jxg.ui.service_order.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import net.wezu.jxg.R;
import net.wezu.jxg.ui.base.BaseFragment;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.MaterialDialog;

import butterknife.OnClick;

/**
 * Created by i310736(Yaming.Zhao@sap.com) on 5/10/2016.
 * 用户功能选择页面,一级功能导航页面
 */

public class ServicesFragment extends BaseFragment {

    public ServicesFragment() {
        super();
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_main_service_02);
    }

    @OnClick(R.id.ad_01) void showHelp() {
        Intent intent = new Intent(getActivity(), HelpActivity.class);

        startActivity(intent);
    }

    @OnClick({
            R.id.btn_so_01, R.id.btn_so_02, R.id.btn_so_03, R.id.btn_so_04,
            R.id.btn_so_05, R.id.btn_so_06, R.id.btn_so_07, R.id.btn_so_08,
            R.id.btn_po_01, R.id.btn_po_02, R.id.btn_po_03, R.id.btn_po_04,
            R.id.btn_po_05, R.id.btn_po_06, R.id.btn_po_07, R.id.btn_po_08,
    }) void createService(View view) {

        if (FastClickUtil.isFastClick()) return;

        int serviceType;
        String cateName;

        switch (view.getId()) {

            case R.id.btn_so_01:
                serviceType = CreateServiceOrderActivity.ST_SERVICE;
                cateName = "换胎";
                break;

            case R.id.btn_so_02:
                serviceType = CreateServiceOrderActivity.ST_SERVICE;
                cateName = "拖车";
                break;
            case R.id.btn_so_03:
                serviceType = CreateServiceOrderActivity.ST_SERVICE;
                cateName = "送油";
                break;
            case R.id.btn_so_04:
                serviceType = CreateServiceOrderActivity.ST_SERVICE;
                cateName = "绑电";
                break;
            case R.id.btn_so_05:
                serviceType = CreateServiceOrderActivity.ST_SERVICE;
                cateName = "事故咨询";
                break;
            case R.id.btn_so_06:
                serviceType = CreateServiceOrderActivity.ST_SERVICE;
                cateName = "开锁";
                break;
            case R.id.btn_so_07:
                serviceType = CreateServiceOrderActivity.ST_SERVICE;
                cateName = "补胎";
                break;
            case R.id.btn_so_08:
                callCustomService();
                return;

//            case R.id.btn_po_01:
//                serviceType = CreateServiceOrderActivity.ST_PREORDER;
//                cateName = "保养";
//                break;
//            case R.id.btn_po_02:
//                serviceType = CreateServiceOrderActivity.ST_PREORDER;
//                cateName = "";
//                break;
//            case R.id.btn_po_03:
//                serviceType = CreateServiceOrderActivity.ST_PREORDER;
//                cateName = "配钥匙";
//                break;
//            case R.id.btn_po_04:
//                serviceType = CreateServiceOrderActivity.ST_PREORDER;
//                cateName = "检查";
//                break;
//            case R.id.btn_po_05:
//                serviceType = CreateServiceOrderActivity.ST_PREORDER;
//                cateName = "";
//                break;
//            case R.id.btn_po_06:
//                serviceType = CreateServiceOrderActivity.ST_PREORDER;
//                cateName = "";
//                break;
//            case R.id.btn_po_07:
//                serviceType = CreateServiceOrderActivity.ST_PREORDER;
//                cateName = "";
//                break;
//            case R.id.btn_po_08:
//                serviceType = CreateServiceOrderActivity.ST_PREORDER;
//                cateName = "";
//                break;

            default:

                showAlarm();

                return;
        }


        CreateServiceOrderActivity.createService(getActivity(), serviceType, cateName);
    }

    private void showAlarm() {
//        MaterialDialog dialog = new MaterialDialog(getActivity())
//                .setTitle("提示")
//                .setMessage("本服务没有开通，请耐心等待。");
//
//        dialog.show();

        toast("本服务没有开通，请耐心等待。");
    }

    private void callCustomService() {
        final MaterialDialog dialog = new MaterialDialog(getActivity());

        dialog.setTitle("联系客服")
                .setMessage("确认需要拨打电话 400 921 6869 吗？")
                .setPositiveButton("拨打电话", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        call("4009216869");
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).show();
    }

    protected void call(String phoneNumber) {
        Intent intent = new Intent("android.intent.action.CALL", Uri
                .parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}
