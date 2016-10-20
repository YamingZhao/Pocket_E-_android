package net.wezu.jxg.ui.service_order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import net.wezu.framework.eventbus.EventBus;
import net.wezu.framework.eventbus.Subscribe;
import net.wezu.framework.eventbus.ThreadMode;
import net.wezu.jxg.BuildConfig;
import net.wezu.jxg.data.ServiceOrderStatus;
import net.wezu.jxg.model.WorkerEntity;
import net.wezu.jxg.receiver.PushMessageCommand;
import net.wezu.jxg.ui.map.AddressInformation;
import net.wezu.jxg.ui.payment.OrderPaymentActivity;
import net.wezu.jxg.ui.service_order.user.OrderDisputeActivity;
import net.wezu.jxg.ui.service_order.user.ServiceOrderAddCommentActivity;
import net.wezu.jxg.ui.service_order.worker.WorkerActivity;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.jxg.util.FormatUtil;
import net.wezu.widget.MaterialDialog;
import net.wezu.widget.RoundImageview.RoundedNetImageView;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.OrderEntity;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.model.OrderLog;
import net.wezu.jxg.receiver.PushMessage;
import net.wezu.jxg.util.NumicUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 订单详情,三级页面，提交订单后查看订单详情
 *
 * Created by snox on 2015/12/8.
 */
public class ServiceOrderDetailActivity extends ServiceOrderDetailBaseActivity {


    // region  控件定义

    @Bind(R.id.fragment_content) View content;
    @Bind(R.id.layout_loading) LinearLayout loading;
    @Bind(R.id.layout_error) LinearLayout error;
    @Bind(R.id.txt_error_message) TextView errorMessage;

    // 地址部分
    @Bind(R.id.tv_prov)     TextView txtAddressProvice;
    @Bind(R.id.tv_city)     TextView txtAddressCity;
    @Bind(R.id.tv_dis)      TextView txtAddressDistrict;
    @Bind(R.id.tv_address)  TextView txtAddressStreet;
    @Bind(R.id.tv_name)     TextView txtAddressName;

    @Bind(R.id.txt_service_type) TextView txtServiceType;
    @Bind(R.id.txt_service_date) TextView txtServiceDate;
    @Bind(R.id.txt_order_no) TextView txtOrderNo;
    @Bind(R.id.txt_car_type) TextView txtModelName;
    @Bind(R.id.txt_plate_no) TextView txtPlateNo;
    @Bind(R.id.txt_amount) TextView txtAmount;
    @Bind(R.id.tv_tip_fee) TextView tvTipFee;
    @Bind(R.id.layout_logs_container) LinearLayout listViewOrderLogs;
    @Bind(R.id.btn_submit) Button btnSubmit;

    @Bind(R.id.view_panel) View firstPanel;
    @Bind(R.id.view_push_info) View pushInfoView;
    @Bind(R.id.txt_push_result) TextView txtPushResult;

    @Bind(R.id.view_user) View userView;
    @Bind(R.id.tv_user_turename) TextView tvUserTrueName;
    @Bind(R.id.tv_distance_for_worker) TextView tvDistanceForWorker;
    @Bind(R.id.iv_call_user) ImageView ivCallUser;
    @Bind(R.id.img_user_avata1) RoundedNetImageView userAvata;

    @Bind(R.id.view_worker) View workerView;
    @Bind(R.id.txt_worker_name) TextView txtWorkerName;
    @Bind(R.id.rating_score) RatingBar ratingBar;
    @Bind(R.id.tv_distance_for_user) TextView tvDistanceForUser;
    @Bind(R.id.iv_call_worker) ImageView ivCallWorker;
    @Bind(R.id.img_user_avatar) RoundedNetImageView workerAvata;

    @Bind(R.id.btn_add_tipfee_button) Button btnAddTipfee;
    @Bind(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;

    @Bind(R.id.image_upload_1) RoundedNetImageView imageUpload1;
    @Bind(R.id.image_upload_2) RoundedNetImageView imageUpload2;
    @Bind(R.id.image_upload_3) RoundedNetImageView imageUpload3;

    @Bind(R.id.view_message) View panelMessage;
    @Bind(R.id.tv_leave_message) TextView txtLeaveMessage;

    @Bind(R.id.images_container) LinearLayout imageContainer;
    // endregion

    private final ArrayList<String> mImages;

    private int orderId;

    private LayoutInflater mInflater;

    private boolean isWorker;

    private LocationClient locationClient;
    private BDLocation location;

    public ServiceOrderDetailActivity() {
        mImages = new ArrayList<>();
    }

    public static void showOrder(Activity activity, int orderId) {
        Intent intent = new Intent(activity, ServiceOrderDetailActivity.class)
                .putExtra(ORDER_ID, orderId);

        activity.startActivity(intent);
    }

//    public static void showOrder(Activity activity, OrderEntity orderEntity) {
//        Intent intent = new Intent(activity, ServiceOrderDetailActivity.class)
//                .putExtra(ORDER_ENTITY, orderEntity);
//
//        activity.startActivity(intent);
//    }

    public static Intent getServiceStartIntent(Context context, int orderId) {
        Intent intent = new Intent(context, ServiceOrderDetailActivity.class);
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

        setContentView(R.layout.activity_service_order_detail);

        isWorker = Application.getInstance().isWorker();

        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        showLoading();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        Application.getInstance().setCurrentViewOrderId(-1);
        super.onDestroy();
    }

    private void updateLocation(BDLocation location) {
        this.location = location;

        double distance = -1;

        if (isWorker) {
            distance = DistanceUtil.getDistance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(orderEntity.order.lat, orderEntity.order.lng));
        } else if (orderEntity.order.wlat > 0) {
            distance = DistanceUtil.getDistance(new LatLng(orderEntity.order.wlat, orderEntity.order.wlng), new LatLng(orderEntity.order.lat, orderEntity.order.lng));
        }

        if (distance > 0) {
            tvDistanceForUser.setText(formatDistance(distance));
            tvDistanceForWorker.setText(formatDistance(distance));
        }
    }

    public void showLoading() {
        content.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    public void showOrderDetail() {
        content.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
    }

    public void showError(String message) {
        content.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);

        errorMessage.setText(message);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushMessage msg) {
        try {
            int dd = Integer.parseInt(msg.oid);
            if (dd != orderId) return;

            if (orderEntity != null && orderEntity.order.OrderId == dd) {

                loadOrder(dd);

                if (PushMessageCommand.ORDER_WORKER_ADD_TIPFEE.equals(msg.cmd)) {
                    final MaterialDialog dialog = new MaterialDialog(this);

                    dialog.setTitle("追单提示")
                            .setMessage(msg.msg)
                            .setPositiveButton("知道了", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            }).show();

                }
                //ToastUtils.show(getActivity(), "重新加载订单");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private View.OnClickListener mCancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            new MaterialDialog(ServiceOrderDetailActivity.this)
                    .setTitle("确认")
                    .setMessage("您是否需要取消本服务订单？")
                    .setPositiveButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ServiceOrderService.cancel(requestTag, orderId, new RequestManager.ResponseListener<OrderListItemModel>() {
                                @Override
                                public void success(OrderListItemModel result, String msg) {
                                    toast("取消成功");

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
    };

    public void setData(OrderEntity orderEntity) {

        showOrderDetail();

        hideRightButton();

        orderId = orderEntity.order.OrderId;

        Application.getInstance().setCurrentViewOrderId(orderEntity.order.OrderId);

        txtOrderNo.setText(orderEntity.order.OrderNo);
//        txtServiceType.setText(orderEntity.order.ServiceType + " - " + orderEntity.order.ProductName);
        txtServiceType.setText(orderEntity.order.ProductName);
        txtAmount.setText(NumicUtil.formatDouble(orderEntity.order.OrderTotal));
        tvTipFee.setVisibility(View.GONE);
        if (BigDecimal.ZERO.compareTo(orderEntity.order.TipFee) == -1) {
            tvTipFee.setText("追单 " + NumicUtil.formatDouble(orderEntity.order.TipFee));
            tvTipFee.setVisibility(View.VISIBLE);
        }
        String orderStatus = orderEntity.order.OrderStatus;

        if (orderEntity.order.isUrgent()) {
            txtServiceDate.setText("紧急");
        } else {
            txtServiceDate.setText(FormatUtil.formatDate(orderEntity.order.ServiceTime));
        }

        if (ServiceOrderStatus.CANCELLED.equals(orderEntity.order.OrderStatus)) {
            firstPanel.setVisibility(View.GONE);
        } else {
            firstPanel.setVisibility(View.VISIBLE);
        }

        try {
            AddressInformation addressInformation = AddressInformation.fromLocation(orderEntity.order.ServiceLocation);
            txtAddressProvice.setText(addressInformation.province);
            txtAddressCity.setText(addressInformation.city);
            txtAddressDistrict.setText(addressInformation.district);
            txtAddressStreet.setText(addressInformation.street);
            txtAddressName.setText(addressInformation.name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        txtModelName.setText(orderEntity.order.BrandName + " " + orderEntity.order.ModalName);
        txtPlateNo.setText(orderEntity.order.Remark);

        // 用户信息展示部分

        pushInfoView.setVisibility(View.GONE);
        workerView.setVisibility(View.GONE);
        userView.setVisibility(View.GONE);

        // 设置用户头像
        // TODO 这里的用户头像数据暂时没有
        if (!TextUtils.isEmpty(orderEntity.order.Avatar))
            userAvata.setImageUrl(orderEntity.order.Avatar, Application.getInstance().getImageLoader());
        if (!TextUtils.isEmpty(orderEntity.order.WorkerAvatar))
            workerAvata.setImageUrl(orderEntity.order.WorkerAvatar, Application.getInstance().getImageLoader());

        btnAddTipfee.setEnabled(false);
        btnAddTipfee.setVisibility(View.GONE);

        if (isWorker) {
            userView.setVisibility(View.VISIBLE);
        } else {
            if (orderStatus.equals(ServiceOrderStatus.CREATED)) {
                pushInfoView.setVisibility(View.VISIBLE);

                setResendView();
            } else if (orderStatus.equals(ServiceOrderStatus.CANCELLED)) {
                //workerView.setVisibility(View.VISIBLE);
            } else {
                workerView.setVisibility(View.VISIBLE);
            }
        }

        tvUserTrueName.setText(orderEntity.client.DisplayName);

        txtWorkerName.setText(orderEntity.order.WorkerFirstname);
        //tvWorkerMobile.setText(orderEntity.order.WorkerUsername);

        ratingBar.setIsIndicator(true);
        ratingBar.setRating(orderEntity.order.AvgRating);

        if (!TextUtils.isEmpty(orderEntity.order.Pic1)) {
            imageUpload1.setImageUrl(orderEntity.order.Pic1, Application.getInstance().getImageLoader());
            mImages.add(orderEntity.order.Pic1);
        }
        if (!TextUtils.isEmpty(orderEntity.order.Pic2)) {
            imageUpload2.setImageUrl(orderEntity.order.Pic2, Application.getInstance().getImageLoader());
            mImages.add(orderEntity.order.Pic2);
        }
        if (!TextUtils.isEmpty(orderEntity.order.Pic3)) {
            imageUpload3.setImageUrl(orderEntity.order.Pic3, Application.getInstance().getImageLoader());
            mImages.add(orderEntity.order.Pic3);
        }

        if (mImages.size() == 0) {
            imageContainer.setVisibility(View.GONE);
        }

        panelMessage.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(orderEntity.order.RemarkDetail)) {
            txtLeaveMessage.setText(orderEntity.order.RemarkDetail);
            panelMessage.setVisibility(View.VISIBLE);
        }

        if (isWorker) {
            switch (orderStatus) {
                case ServiceOrderStatus.CREATED:
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("抢单");

                    if (orderEntity.orderinfo != null) {
                        if (orderEntity.orderinfo.PushCount > 0) {
                            txtPushResult.setText(String.format("已推送给 %d 名机修工", orderEntity.orderinfo.PushCount));
                        } else {
                            txtPushResult.setText("当前没有空闲的机修工，请拨打客服热线4009216869");
                        }
                    } else {
                        txtPushResult.setText("当前没有空闲的机修工，请拨打客服热线4009216869");
                    }

                    locationClient = new LocationClient(getApplicationContext());
                    LocationClientOption option = new LocationClientOption();

                    option.setOpenGps(true);
                    option.setCoorType("bd09ll");
                    option.setScanSpan(5000);

                    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                    option.setIsNeedAddress(true);

                    locationClient.setLocOption(option);

                    locationClient.registerLocationListener(new BDLocationListener() {
                        @Override
                        public void onReceiveLocation(BDLocation bdLocation) {

                            updateLocation(bdLocation);
                        }
                    });

                    locationClient.start();

                    break;
                case ServiceOrderStatus.CATCHED:
                    btnSubmit.setEnabled(false);
                    btnSubmit.setText("等待确认");
                    break;
                case ServiceOrderStatus.CONFIRMED:
//                    btnAddTipfee.setEnabled(true);
//                    btnAddTipfee.setVisibility(View.VISIBLE);


//                    setRightButton("客服", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //showAppealDialog();
//                            callCustomerService();
//                        }
//                    });
                    btnAddTipfee.setEnabled(true);
                    btnAddTipfee.setVisibility(View.VISIBLE);

                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("开始服务");
                    break;
                case ServiceOrderStatus.SERVICING:
                    btnAddTipfee.setEnabled(true);
                    btnAddTipfee.setVisibility(View.VISIBLE);

                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("确认收款");

                    setRightButton("客服", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callCustomerService();
                            //showAppealDialog();
                        }
                    });
                    break;
//                case ServiceOrderStatus.WORKDONE:
//                    btnAddTipfee.setEnabled(true);
//                    btnAddTipfee.setVisibility(View.VISIBLE);
//                    btnSubmit.setEnabled(true);
//                    btnSubmit.setText("线下付款");
//                    setRightButton("客服", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //showAppealDialog();
//                        }
//                    });
//                    break;

                case ServiceOrderStatus.CLOSED:
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("完成");
                    break;

                case ServiceOrderStatus.CANCELLED:
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("完成");
                    break;
            }
        } else {
            switch (orderStatus) {
                case ServiceOrderStatus.CREATED:
                    btnSubmit.setEnabled(false);
                    btnSubmit.setText("等待抢单");

                    if (orderEntity.orderinfo != null) {
                        if (orderEntity.orderinfo.PushCount > 0) {
                            txtPushResult.setText(String.format("已推送给 %d 名机修工", orderEntity.orderinfo.PushCount));
                        } else {
                            txtPushResult.setText("当前没有空闲的机修工，请拨打客服热线4009216869");
                        }
                    } else {
                        txtPushResult.setText("当前没有空闲的机修工，请拨打客服热线4009216869");
                    }

                    setRightButton("取消", mCancelClick);

                    btnAddTipfee.setText("请耐心等待");
                    btnAddTipfee.setVisibility(View.GONE);
                    break;
                case ServiceOrderStatus.CATCHED:
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("抢单确认");

                    setRightButton("取消", mCancelClick);
                    break;
                case ServiceOrderStatus.CONFIRMED:
                    btnSubmit.setEnabled(false);
                    btnSubmit.setText("等待服务");

                    setRightButton("取消", mCancelClick);
                    break;

                case ServiceOrderStatus.SERVICING:
//                    btnSubmit.setEnabled(true);
//
//                    btnSubmit.setText("确认完工");
//                    break;
//                case ServiceOrderStatus.WORKDONE:
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("付款");
                    break;

                case ServiceOrderStatus.CLOSED:
                    if (orderEntity.comment != null) {
                        btnSubmit.setEnabled(true);

                        btnSubmit.setText("完成");
                    } else {
                        btnSubmit.setEnabled(true);

                        btnSubmit.setText("评价");
                    }
                    //if (!"OFFLINE".equals(orderEntity.order.PayTypeId))
                        setRightButton("申诉", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showAppealDialog();
                            }
                        });
                    break;

                case "CANCELLED":
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("完成");
                    break;
            }
        }

        tvDistanceForUser.setText("");
        tvDistanceForWorker.setText("");

        if (orderEntity.order.lat > 0 && orderEntity.order.wlat > 0) {
            double distance = DistanceUtil.getDistance(new LatLng(orderEntity.order.wlat, orderEntity.order.wlng), new LatLng(orderEntity.order.lat, orderEntity.order.lng));

            tvDistanceForUser.setText(formatDistance(distance));//String.format("%.1f 千米", distance / 1000));
            tvDistanceForWorker.setText(formatDistance(distance));//String.format("%.1f 千米", distance / 1000));
        }
        //userAvata.setImageUrl(orderEntity.orderinfo.);

        listViewOrderLogs.removeAllViews();
        ServiceOrderService.getLogs(requestTag, orderEntity.order.OrderId, new RequestManager.ResponseListener<List<OrderLog>>() {
            @Override
            public void success(List<OrderLog> result, String msg) {
                if (result != null) {
                    for (OrderLog log : result) {
                        addLog(log);
                    }
                }
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });
    }

    private void callCustomerService() {
        call("13916921601");
    }

    // 打电话
    @OnClick({R.id.iv_call_user, R.id.iv_call_worker}) void call() {
        call(isWorker ? orderEntity.order.Username : orderEntity.order.WorkerUsername);
    }



    @OnClick(R.id.btn_submit) void submit() {
        switch (orderEntity.order.OrderStatus) {
            case ServiceOrderStatus.CREATED:
                if (BuildConfig.DEBUG) {
                    if (location == null) {
                        location = new BDLocation();
                    }
                } else {
                    if (location == null) {
                        toast("没有定位数据");
                        return;
                    }
                }

                ServiceOrderService.catchOrder(requestTag, orderEntity.order.OrderId, new LatLng(location.getLatitude(), location.getLongitude()),
                        location.getAddrStr(), new RequestManager.ResponseListener<OrderListItemModel>() {

                    @Override
                    public void success(OrderListItemModel result, String msg) {
                        toast("抢单成功");
                        reloadOrder();
                    }

                    @Override
                    public void error(String msg) {
                        toast(msg);
                    }
                });
                break;
            case ServiceOrderStatus.CATCHED:
                ServiceOrderService.confirm(requestTag, orderEntity.order.OrderId, new RequestManager.ResponseListener<OrderListItemModel>() {
                    @Override
                    public void success(OrderListItemModel result, String msg) {
                        toast("确认成功");
                        reloadOrder();
                    }

                    @Override
                    public void error(String msg) {
                        toast("确认失败\n" + msg);
                    }
                });
                break;
            case ServiceOrderStatus.CONFIRMED:
                ServiceOrderService.startService(requestTag, orderEntity.order.OrderId, new RequestManager.ResponseListener<OrderListItemModel>() {
                    @Override
                    public void success(OrderListItemModel result, String msg) {
                        toast("开始服务成功");
                        reloadOrder();
                    }

                    @Override
                    public void error(String msg) {
                        toast("开始服务失败\n" + msg);
                    }
                });
                break;
            case ServiceOrderStatus.SERVICING:
//                ServiceOrderService.done(requestTag, orderEntity.order.OrderId, new RequestManager.ResponseListener<OrderListItemModel>() {
//
//                    @Override
//                    public void success(OrderListItemModel result, String msg) {
//                        toast("确认完工成功");
//                        loadOrder();
//                    }
//
//                    @Override
//                    public void error(String msg) {
//                        toast(msg);
//                    }
//                });
//                break;
//            case ServiceOrderStatus.WORKDONE:

                if (isWorker) {
                    // 机修工确认线下支付
                    confirmOfflinePayment();
                } else {
                    // 用户线上支付
                    startActivityForResult(new Intent(ServiceOrderDetailActivity.this, OrderPaymentActivity.class)
                            .putExtra(OrderPaymentActivity.SERVICE_ORDER, orderEntity), 1);
                }

                break;

            case ServiceOrderStatus.CLOSED:
                if (!isWorker && orderEntity.comment == null) {
                    showAddCommentDialog();
                } else {
                    finish();
                }
                break;


            case ServiceOrderStatus.CANCELLED:
                finish();
                break;
        }
    }

    @OnClick(R.id.btn_add_tipfee_button) void btnUserClicked() {
//        if (!isWorker && orderEntity.order.OrderStatus.equals(ServiceOrderStatus.CATCHED)) {
//            // 更换机修工
//            ServiceOrderService.changeWorker(requestTag, orderEntity.order.OrderId, new RequestManager.ResponseListener<OrderListItemModel>() {
//
//                @Override
//                public void success(OrderListItemModel result, String msg) {
//                    toast("更换机修工成功，重新抢单");
//                    finish();
//                }
//
//                @Override
//                public void error(String msg) {
//                    toast(msg);
//                }
//            });
//        } else if (!isWorker && orderEntity.order.OrderStatus.equalsIgnoreCase(ServiceOrderStatus.CONFIRMED)) {
//            // 用户追单
//            showAddFeeDialog();
//        }

        if (isWorker)
        {
            switch (orderEntity.order.OrderStatus)
            {
                case ServiceOrderStatus.SERVICING:
                case ServiceOrderStatus.WORKDONE:
                    showAddFeeDialog();
                    break;

            }
        }
    }

    @OnClick(R.id.view_rating) void showWorkerComments() {
        if (FastClickUtil.isFastClick()) return;

        WorkerEntity entity = null;
        if (orderEntity!=null && orderEntity.order != null) {
            entity = orderEntity.order.getWorkerEntity();

            if (entity != null)
                WorkerActivity.startActivity(this, entity);
        }
    }

    @OnClick(R.id.images_container) void showOrderImages() {
        if (FastClickUtil.isFastClick()) return;

        if (mImages.size() > 0) {
            Intent intent = new Intent(this, ServiceOrderImageGalleryActivity.class);
            intent.putStringArrayListExtra("images", mImages);
            startActivity(intent);
        }
    }

    private void showAddFeeDialog() {
        ServiceOrderAddFeeDialog dialog = new ServiceOrderAddFeeDialog(this, orderEntity.order.TipFee);
        dialog.setOnClickListener(new ServiceOrderAddFeeDialog.OnClickListener() {
            @Override
            public void onClick(BigDecimal fee, String description) {
                ServiceOrderService.addFee(requestTag, orderId, fee, description, new RequestManager.ResponseListener<OrderListItemModel>() {
                    @Override
                    public void success(OrderListItemModel result, String msg) {
                        reloadOrder();
                        toast("追单成功");
                    }

                    @Override
                    public void error(String msg) {
                        toast(msg);
                    }
                });
            }
        });
        dialog.show();
    }

    private void showAppealDialog() {

        Intent intent = new Intent(this, OrderDisputeActivity.class);
        intent.putExtra("order", orderEntity);

        startActivity(intent);

//        ServiceOrderAppealDialog dialog = new ServiceOrderAppealDialog(this);
//        dialog.setOnClickListener(new ServiceOrderAppealDialog.OnClickListener() {
//            @Override
//            public void onClick(String description) {
//                ServiceOrderService.dispute(requestTag, orderId, description, new RequestManager.ResponseListener<OrderDispute>() {
//                    @Override
//                    public void success(OrderDispute OrderDispute, String msg) {
//                        toast("申诉发布成功，请等待处理");
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

    private void confirmOfflinePayment() {
        new MaterialDialog(ServiceOrderDetailActivity.this)
                .setTitle("线下付款确认")
                .setMessage("当前订单确认使用线下支付？")
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ServiceOrderService.confirmOfflinePayment(requestTag, orderEntity.order.OrderId, new RequestManager.ResponseListener<OrderListItemModel>() {
                            @Override
                            public void success(OrderListItemModel result, String msg) {
                                toast("线下付款成功");
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

    private void showAddCommentDialog() {
        ServiceOrderAddCommentActivity.show(this, orderEntity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ServiceOrderAddCommentActivity.RC_ADD_COMMENT:
                    btnSubmit.setEnabled(false);
                    reloadOrder();
                    break;
            }
        }
    }

    private void addLog(OrderLog log) {
        View v = mInflater.inflate(R.layout.listitem_order_log, null);

        ((TextView)v.findViewById(R.id.txt_log_name)).setText(log.Message);
        ((TextView)v.findViewById(R.id.txt_log_date)).setText(log.CreatedOn);

        listViewOrderLogs.addView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 重发请求页面
     */
    private void setResendView() {

        long span = System.currentTimeMillis() - orderEntity.order.CreatedTime.getTime();

        if (span > 0) {

        }
    }

    class Xss extends Timer {

    }

    class TimeCount extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            setSendEnabled(false);
            btnAddTipfee.setText(millisUntilFinished / 1000 + " 秒后可重新推送");
        }

        @Override
        public void onFinish() {
            setSendEnabled(true);
            btnAddTipfee.setText("重新推送");
        }
    }

    private void setSendEnabled(boolean enabled) {
        btnAddTipfee.setEnabled(enabled);
    }
}
