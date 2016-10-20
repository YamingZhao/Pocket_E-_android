package net.wezu.jxg.ui.service_order.worker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import net.wezu.framework.eventbus.EventBus;
import net.wezu.framework.eventbus.Subscribe;
import net.wezu.framework.eventbus.ThreadMode;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.data.ServiceOrderStatus;
import net.wezu.jxg.model.OrderEntity;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.receiver.PushMessage;
import net.wezu.jxg.receiver.PushMessageCommand;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.ui.map.AddressInformation;
import net.wezu.jxg.ui.map.RoutePlanActivity;
import net.wezu.jxg.ui.service_order.ServiceOrderAddFeeDialog;
import net.wezu.jxg.ui.service_order.ServiceOrderDetailActivity;
import net.wezu.jxg.ui.service_order.ServiceOrderDetailBaseActivity;
import net.wezu.jxg.ui.service_order.ServiceOrderImageGalleryActivity;
import net.wezu.jxg.ui.service_order.ServiceOrderStatusChangedEvent;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.jxg.util.NumicUtil;
import net.wezu.widget.MaterialDialog;
import net.wezu.widget.RoundImageview.RoundedNetImageView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 机修工服务订单页面
 *
 * Created by snox on 2016/3/27.
 */
public class WorkerServiceOrderDetailActivity extends ServiceOrderDetailBaseActivity {

    // region controls
    @Bind(R.id.view_root) View viewRoot;

    @Bind(R.id.img_user_avatar) RoundedNetImageView imgAvatar;
    @Bind(R.id.tv_user_turename) TextView tvUserTrueName;
    @Bind(R.id.txt_service_type) TextView tvServiceType;
    @Bind(R.id.txt_car_type) TextView tvModalName;
    @Bind(R.id.txt_plate_no) TextView tvPlateNo;
    @Bind(R.id.txt_service_date) TextView tvServiceDate;

    @Bind(R.id.tv_area_1) TextView tvArea1;
    @Bind(R.id.tv_area_2) TextView tvArea2;
    @Bind(R.id.tv_area_3) TextView tvArea3;
    @Bind(R.id.tv_area_4) TextView tvArea4;
    @Bind(R.id.tv_distance_for_worker) TextView tvDistanceWorker;

    @Bind(R.id.panel_message) View panelMessage;
    @Bind(R.id.tv_message) TextView tvMessage;

    @Bind(R.id.panel_images) View panelImages;
    @Bind(R.id.img_service_1) RoundedNetImageView imageService1;
    @Bind(R.id.img_service_2) RoundedNetImageView imageService2;
    @Bind(R.id.img_service_3) RoundedNetImageView imageService3;

    @Bind(R.id.txt_order_no) TextView tvOrderNo;
    @Bind(R.id.layout_logs_container) LinearLayout llLogsContainer;

    @Bind(R.id.txt_amount) TextView tvAmount;
    @Bind(R.id.tv_tip_fee) TextView tvTipFee;
    @Bind(R.id.btn_submit) TextView btnSubmit;

    @Bind(R.id.map_view) MapView mapView;
    BaiduMap baiduMap;

    // endregion

    private final ArrayList<String> mImages;

    public WorkerServiceOrderDetailActivity() {
        mImages = new ArrayList<>();
    }

    public static void showOrder(Activity activity, OrderListItemModel order) {
        Intent intent = new Intent(activity, WorkerServiceOrderDetailActivity.class)
                .putExtra(ORDER_ENTITY, order);

        activity.startActivity(intent);
    }

    public static Intent getServiceStartIntent(Context context, int orderId) {
        Intent intent = new Intent(context, WorkerServiceOrderDetailActivity.class);
        intent.putExtra(ServiceOrderDetailActivity.ORDER_ID, orderId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_order_detail_worker);
        viewRoot.setVisibility(View.GONE);

        EventBus.getDefault().register(this);

        mapView.setVisibility(View.GONE);

        mapView.showZoomControls(false);
        mapView.showScaleControl(false);
        mapView.setLogoPosition(LogoPosition.logoPostionRightBottom);

        baiduMap = mapView.getMap();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        Application.getInstance().setCurrentViewOrderId(-1);
        super.onDestroy();
    }

    @OnClick(R.id.nav) void Nav() {
        if (FastClickUtil.isFastClick()) return;

        if (orderEntity !=null && orderEntity.order != null) { // && ServiceOrderStatus.CATCHED.equals(orderEntity.order.OrderStatus)) {
            LatLng from = Application.getInstance().getLatLng();
            LatLng to = new LatLng(orderEntity.order.lat, orderEntity.order.lng);

            if (from != null) {
                Intent intent = new Intent(this, RoutePlanActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("from", from);
                intent.putExtra("to", to);

                startActivity(intent);
            }
        }
    }

    //region setData function
    @Override
    protected void setData(OrderEntity order) {
        hideRightButton();
        btnSubmit.setEnabled(false);

        Application.getInstance().setCurrentViewOrderId(order.order.OrderId);

        tvOrderNo.setText(order.order.OrderNo);
        tvServiceType.setText(order.order.ServiceType + " - " + order.order.ProductName);
        tvModalName.setText(order.order.BrandName + " " + order.order.ModalName);
        tvPlateNo.setText(order.order.Remark);

        if (order.order.isUrgent()) {
            tvServiceDate.setText("紧急");
            tvServiceDate.setTextColor(Color.RED);
        } else {
            tvServiceDate.setText((new SimpleDateFormat("yyyy-MM-dd")).format(order.order.ServiceTime));
            tvServiceDate.setTextColor(Color.BLUE);
        }

        try {
            AddressInformation address = AddressInformation.fromLocation(order.order.ServiceLocation);
            tvArea1.setText(address.province);
            tvArea2.setText(address.city);
            tvArea3.setText(address.district);
            tvArea4.setText(address.street);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // avatar
        if (!TextUtils.isEmpty(order.order.Avatar)) {
            imgAvatar.setImageUrl(order.order.Avatar, Application.getInstance().getImageLoader());
        }
        tvUserTrueName.setText(order.client.DisplayName);

        if (ServiceOrderStatus.CREATED.equals(order.order.OrderStatus)) {
            if (orderListItemModel != null){
                orderEntity.order.wlat = orderListItemModel.wlat;
                orderEntity.order.wlng = orderListItemModel.wlng;
                orderEntity.order.WorkerLocation = orderListItemModel.WorkerLocation;
            } else if (WorkerServiceOrderToCatchListFragment.getInstance() != null) {

                BDLocation location = WorkerServiceOrderToCatchListFragment.getInstance().getLocation();

                if (location != null) {
                    orderEntity.order.wlat = location.getLatitude();
                    orderEntity.order.wlng = location.getLongitude();
                    orderEntity.order.WorkerLocation = location.getAddrStr();
                }
            }
        }

        LatLng ll = new LatLng(orderEntity.order.lat, orderEntity.order.lng);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(16.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        panelMessage.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(orderEntity.order.RemarkDetail)) {
            panelMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(orderEntity.order.RemarkDetail);
        }

        if (order.order.lat > 0 && order.order.wlat > 0) {
            double distance = DistanceUtil.getDistance(new LatLng(orderEntity.order.wlat, orderEntity.order.wlng), new LatLng(orderEntity.order.lat, orderEntity.order.lng));

            tvDistanceWorker.setText("距离: " + formatDistance(distance));
        }

        if (!TextUtils.isEmpty(orderEntity.order.Pic1)) {
            imageService1.setImageUrl(orderEntity.order.Pic1, Application.getInstance().getImageLoader());
            mImages.add(orderEntity.order.Pic1);
        }
        if (!TextUtils.isEmpty(orderEntity.order.Pic2)) {
            imageService2.setImageUrl(orderEntity.order.Pic2, Application.getInstance().getImageLoader());
            mImages.add(orderEntity.order.Pic2);
        }
        if (!TextUtils.isEmpty(orderEntity.order.Pic3)) {
            imageService3.setImageUrl(orderEntity.order.Pic3, Application.getInstance().getImageLoader());
            mImages.add(orderEntity.order.Pic3);
        }

        if (mImages.size() == 0) {
            panelImages.setVisibility(View.GONE);
        } else {
            panelImages.setVisibility(View.VISIBLE);
        }

        switch (order.order.OrderStatus) {
            case ServiceOrderStatus.CREATED:
                btnSubmit.setEnabled(true);
                btnSubmit.setText("抢单");
                break;

            case ServiceOrderStatus.CATCHED:
                break;

            case ServiceOrderStatus.CONFIRMED:
                btnSubmit.setEnabled(true);
                btnSubmit.setText("开始服务");
                enableAddTipfee();
                break;

            case ServiceOrderStatus.SERVICING:
                enableAddTipfee();
                btnSubmit.setEnabled(true);
                btnSubmit.setText("确认收款");
                break;

            case ServiceOrderStatus.CLOSED:
                btnSubmit.setEnabled(true);
                btnSubmit.setText("关闭");
                break;

            case ServiceOrderStatus.CANCELLED:
                btnSubmit.setEnabled(true);
                btnSubmit.setText("关闭");
                break;
        }

        tvAmount.setText(NumicUtil.formatDouble(order.order.OrderTotal));
        tvTipFee.setVisibility(View.GONE);
        if (BigDecimal.ZERO.compareTo(order.order.TipFee) == -1) {
            tvTipFee.setText("追单 " + NumicUtil.formatDouble(order.order.TipFee));
            tvTipFee.setVisibility(View.VISIBLE);
        }

        viewRoot.setVisibility(View.VISIBLE);
        loadOrderLogs(llLogsContainer, orderId);
    }

    //endregion

    @OnClick(R.id.img_call) void call() {
        call(orderEntity.order.Username);
    }

    @OnClick(R.id.panel_images) void showOrderImages() {
        if (mImages.size() > 0) {
            Intent intent = new Intent(this, ServiceOrderImageGalleryActivity.class);
            intent.putStringArrayListExtra("images", mImages);
            startActivity(intent);
        }
    }

    //region submit button action

    @OnClick(R.id.btn_submit) void submit() {
        if (FastClickUtil.isFastClick()) return;

        switch (orderEntity.order.OrderStatus) {

            //region 抢单
            case ServiceOrderStatus.CREATED:

                getLoadingDialog("正在抢单").show();

                ServiceOrderService.catchOrder(requestTag, orderId, new LatLng(orderEntity.order.lat, orderEntity.order.lng),
                        orderEntity.order.WorkerLocation, new RequestManager.ResponseListener<OrderListItemModel>() {
                            @Override
                            public void success(OrderListItemModel result, String msg) {
                                toast("抢单成功");
                                dismissLoadingDialog();
                                EventBus.getDefault().post(new ServiceOrderStatusChangedEvent());
                                reloadOrder();
                            }

                            @Override
                            public void error(String msg) {
                                toast(msg);
                                dismissLoadingDialog();
                            }
                        });

                break;
            //endregion

            //region 开始服务
            case ServiceOrderStatus.CONFIRMED:
                confirmStartService();
                break;
            //endregion

            //region 确认线下支付
            case ServiceOrderStatus.SERVICING:
                confirmOfflinePayment();
                break;
            //endregion

            default:
                finish();
                break;
        }
    }

    // endregion

    private void enableAddTipfee() {
        int result = new BigDecimal(1000).compareTo(orderEntity.order.TipFee);
        if (result == 1) {
            setRightButton("追单", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddFeeDialog();
                }
            });
        }
    }

    private void showAddFeeDialog() {
        final MaterialDialog dialog = new MaterialDialog(this);

//        dialog.setTitle("追单");
//        EditText contentView = new EditText(this);
//        contentView.setHint("请输入追单金额");
//        contentView.setTextSize(12);
//        contentView.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);//R.layout.dialog_service_order_add_fee
//        dialog.setView(contentView);

        View dialogAddfree = LayoutInflater.from(this).inflate(R.layout.dialog_service_order_add_fee, null);

        final TextView tvAlarm = (TextView) dialogAddfree.findViewById(R.id.tv_alarm);
        tvAlarm.setVisibility(View.GONE);

        final EditText edtDescription = (EditText) dialogAddfree.findViewById(R.id.edt_description);
        final EditText edtAmount = (EditText) dialogAddfree.findViewById(R.id.edt_amount);
        edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                //btnSubmit.setEnabled(false);
//                if (count == 0) return;
//
//                BigDecimal value = new BigDecimal(s.toString());
//
//                tvAlarm.setVisibility(View.GONE);
//
//                //btnSubmit.setEnabled(true);
//
//                if (value.compareTo(new BigDecimal(1000).subtract(orderEntity.order.TipFee)) == 1) {
//                    tvAlarm.setError("您输入的追单金额已经超过1000块");
//                    tvAlarm.setVisibility(View.VISIBLE);
//                }
//                dialog.getPositiveButton().setEnabled(tvAlarm.getVisibility() == View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    s.append('0');
                }
                BigDecimal value = new BigDecimal(s.toString());

                tvAlarm.setVisibility(View.GONE);

                //btnSubmit.setEnabled(true);

                if (value.compareTo(new BigDecimal(1000).subtract(orderEntity.order.TipFee)) == 1) {
                    tvAlarm.setError("您输入的追单金额已经超过1000块");
                    tvAlarm.setVisibility(View.VISIBLE);
                }
                dialog.getPositiveButton().setEnabled(tvAlarm.getVisibility() == View.GONE);
            }
        });

        dialog.setContentView(dialogAddfree);
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvAlarm.getVisibility() == View.VISIBLE) {
                    return;
                }

                BigDecimal amount = new BigDecimal(edtAmount.getText().toString());
                if (BigDecimal.ZERO.compareTo(amount) != -1) {
                    toast("追单金额要大于0");
                    return;
                }

                ServiceOrderService.addFee(requestTag, orderId,
                        new BigDecimal(edtAmount.getText().toString()),
                        edtDescription.getText().toString(),
                        new RequestManager.ResponseListener<OrderListItemModel>() {
                            @Override
                            public void success(OrderListItemModel result, String msg) {
                                reloadOrder();
                                dialog.dismiss();
                                toast("追单成功");
                            }

                            @Override
                            public void error(String msg) {
                                toast(msg);
                            }
                        });
            }
        });

        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

        dialog.getPositiveButton().setEnabled(false);
//        ServiceOrderAddFeeDialog dialog = new ServiceOrderAddFeeDialog(this, orderEntity.order.TipFee);
//        dialog.setOnClickListener(new ServiceOrderAddFeeDialog.OnClickListener() {
//            @Override
//            public void onClick(BigDecimal fee, String description) {
//                ServiceOrderService.addFee(requestTag, orderId, fee, description, new RequestManager.ResponseListener<OrderListItemModel>() {
//                    @Override
//                    public void success(OrderListItemModel result, String msg) {
//                        reloadOrder();
//                        toast("追单成功");
//                    }
//
//                    @Override
//                    public void error(String msg) {
//                        toast(msg);
//                    }
//                });
//            }
//        });
//        dialog.show();
    }

    private void confirmStartService() {
        new MaterialDialog(this)
                .setTitle("开始服务确认")
                .setMessage("当前订单确认开始服务？")
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getLoadingDialog("请稍等").show();
                        ServiceOrderService.startService(requestTag, orderId, new RequestManager.ResponseListener<OrderListItemModel>() {
                            @Override
                            public void success(OrderListItemModel result, String msg) {
                                dismissLoadingDialog();
                                EventBus.getDefault().post(new ServiceOrderStatusChangedEvent());
                                reloadOrder();
                            }

                            @Override
                            public void error(String msg) {
                                dismissLoadingDialog();
                                toast(msg);
                            }
                        });
                    }
                })
                .setNegativeButton("取消")
                .show();
    }

    private void confirmOfflinePayment() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle("线下付款确认")
                .setMessage("当前订单确认使用线下支付？")
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        ServiceOrderService.confirmOfflinePayment(requestTag, orderId, new RequestManager.ResponseListener<OrderListItemModel>() {
                            @Override
                            public void success(OrderListItemModel result, String msg) {
                                toast("线下付款成功");
                                EventBus.getDefault().post(new ServiceOrderStatusChangedEvent());
                                reloadOrder();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushMessage msg) {
        try {
            int dd = Integer.parseInt(msg.oid);
            if (dd != orderId) return;

            if (orderEntity != null && orderEntity.order.OrderId == dd) {

                if (PushMessageCommand.ORDER_CANCELED_TO_WORKER.equals(msg.cmd)) {
                    showCancelDialog();
                    return;
                } else if (PushMessageCommand.ORDER_OFFLINEPAY_TO_WORKER.equals(msg.cmd)) {
                    confirmOfflinePayment();
                    return;
                }

                loadOrder(dd);
                //ToastUtils.show(getActivity(), "重新加载订单");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCancelDialog() {
        final MaterialDialog dialog = new MaterialDialog(this);

        dialog.setTitle("订单取消")
                .setMessage("用户已经取消订单，您不能继续本单了。")
                .setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }
}
