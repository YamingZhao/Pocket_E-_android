package net.wezu.jxg.ui.service_order.worker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.ServiceOrderStatus;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.ui.base.BaseViewHolder;
import net.wezu.jxg.util.NumicUtil;
import net.wezu.widget.RoundImageview.RoundedNetImageView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by snox on 2015/11/29.
 */
public class WorkerOrderListItemViewHolder extends BaseViewHolder<OrderListItemModel> {

    @Bind(R.id.txt_car_type) TextView txtCarType;
    @Bind(R.id.txt_service_date) TextView txtServiceDate;
    @Bind(R.id.txt_service_type) TextView txtServiceType;
    @Bind(R.id.txt_order_status) TextView txtOrderStatus;
    @Bind(R.id.txt_total_amount) TextView txtTotalAmount;
    @Bind(R.id.txt_plate_no) TextView txtPlateNo;
    @Bind(R.id.txt_tip_fee) TextView txtTipFee;
    @Bind(R.id.tv_order_id) TextView tvOrderId;
    @Bind(R.id.tv_user_turename) @Nullable TextView userName;
    @Bind(R.id.img_user_avatar) @Nullable RoundedNetImageView avatar;

    private String mobile;
    private OrderListItemModel order;

    public WorkerOrderListItemViewHolder(Context context, View view) {
        super(context, view);
    }

    @Override
    public void setData(OrderListItemModel data) {

        order = data;

        txtCarType.setText(data.BrandName + " " + data.ModalName);
        txtServiceType.setText(data.ServiceType);

        if (data.ServiceTime.getYear() == 0) {
            txtServiceDate.setText("紧急");
            txtServiceDate.setTextColor(0xFFFF0000);
        } else {
            txtServiceDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(data.ServiceTime));
            txtServiceDate.setTextColor(0xFF0000FF);
        }

        switch (data.OrderStatus) {
            case ServiceOrderStatus.CREATED:    txtOrderStatus.setText(Application.getInstance().isWorker() ? "忽略" : "待抢");   break;
            case ServiceOrderStatus.CATCHED:    txtOrderStatus.setText("待确认"); break;
            case ServiceOrderStatus.CONFIRMED:  txtOrderStatus.setText("已确认"); break;
            case ServiceOrderStatus.SERVICING:  txtOrderStatus.setText("服务中"); break;
            case ServiceOrderStatus.WORKDONE:   txtOrderStatus.setText("完工");   break;
            case ServiceOrderStatus.CLOSED:     txtOrderStatus.setText("完成");   break;
            case ServiceOrderStatus.CANCELLED:  txtOrderStatus.setText("已取消"); break;
        }

        if (avatar!=null){
            avatar.setImageUrl(data.Avatar, Application.getInstance().getImageLoader());
        }
        if (userName!=null){
            userName.setText(data.Firstname);
        }

        txtTotalAmount.setText(String.format("%s 元", NumicUtil.formatDouble(data.OrderTotal)));

        //txtPlateNo.setText(String.format("车牌号：%s", data.Remark));
        txtPlateNo.setText(data.Remark);

        if (data.TipFee.compareTo(BigDecimal.ZERO) == 1) {
            txtTipFee.setText(String.format("追单 %s 元", NumicUtil.formatDouble(data.TipFee)));
            txtTipFee.setVisibility(View.VISIBLE);
        } else {
            txtTipFee.setVisibility(View.GONE);
        }

        tvOrderId.setText(data.OrderNo);

        mobile = data.Username;
    }

    @OnClick(R.id.btn_tel) void tel() {
        Intent intent = new Intent("android.intent.action.CALL", Uri
                .parse("tel:" + mobile));
        mContext.startActivity(intent);
    }

    @OnClick(R.id.txt_order_status) void ignore() {
        if (Application.getInstance().isWorker() && ServiceOrderStatus.CREATED.equals(order.OrderStatus)) {
            ignore(order);
        }
    }

    protected void ignore(OrderListItemModel order) { }
}
