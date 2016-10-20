package net.wezu.jxg.ui.service_order;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import net.wezu.widget.RoundImageview.RoundedNetImageView;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.ui.base.BaseViewHolder;
import net.wezu.jxg.util.NumicUtil;

import java.math.BigDecimal;

import butterknife.Bind;

/**
 * 订单列表项视图保持
 *
 * Created by snox on 2015/11/17.
 */
public class OrderListItemViewHolder extends BaseViewHolder<OrderListItemModel> {

    @Bind(R.id.txt_order_no) TextView txtOrderNo;

    @Bind(R.id.txt_order_payment_status) TextView txtPaymentStatus;

    @Bind(R.id.txt_worker_name) TextView txtWorkerName;

    @Bind(R.id.img_user_avatar) RoundedNetImageView avater;

    @Bind(R.id.txt_service_name) TextView txtServiceName;
    //@Bind(R.id.txt_worker_ranker) TextView txtWorkerRanker;
    //@Bind(R.id.txt_distance) TextView txtDistance;

    @Bind(R.id.txt_total_amount) TextView txtTotalAmount;
    @Bind(R.id.txt_tip_fee) TextView tvTipFee;
    @Bind(R.id.txt_total_accept) TextView txtTotalAccept;

    @Bind(R.id.txt_order_type) TextView txtOrderType;
    @Bind(R.id.txt_order_datetime) TextView txtOrderDateTime;

    private boolean isWorker;


    public OrderListItemViewHolder(Context context, View view) {
        super(context, view);

        isWorker = Application.getInstance().isWorker();
    }

    /**
     * 设置数据
     * @param data
     */
    @Override
    public void setData(OrderListItemModel data) {
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("(yyyy-MM-dd HH:mm)");

        txtOrderNo.setText(data.OrderNo);

        String payStatus = "";

        switch (data.PaymentStatus) {
            case "NOT_PAID":
                payStatus = " / 未付款";
                break;
        }

        txtPaymentStatus.setText(data.getOrderStatus() + payStatus);

        if (isWorker) {
            avater.setImageUrl(data.Avatar, Application.getInstance().getImageLoader());
            txtWorkerName.setText(data.Firstname);
        } else {
            avater.setImageUrl(data.WorkerAvatar, Application.getInstance().getImageLoader());
            txtWorkerName.setText(data.WorkerFirstname);
        }

        txtServiceName.setText(data.ServiceType);

        if (data.ServiceTime.getYear() == 0) {
            txtOrderType.setText("紧急");
            txtOrderDateTime.setText("");
        } else {
            txtOrderType.setText("预约");
            txtOrderDateTime.setText(format1.format(data.ServiceTime));
        }

        if (data.WorkerId > 0) {
            if (!isWorker)
                txtWorkerName.setText(data.WorkerFirstname);
            //txtWorkerRanker.setText("");
            //txtDistance.setText("距离");
            txtTotalAccept.setText("已接单 " + data.TotalOrders);

        } else {
            if (!isWorker)
                txtWorkerName.setText("未抢单");
            //txtWorkerRanker.setText("-");
            //txtDistance.setText("-");
            txtTotalAccept.setText("-");
        }

        String amount = "￥ " + NumicUtil.formatDouble(data.OrderTotal);
        if (BigDecimal.ZERO.compareTo(data.TipFee) == -1) {
            tvTipFee.setText("追单 " + NumicUtil.formatDouble(data.TipFee));
            //amount = amount + "(追单 " + NumicUtil.formatDouble(data.OrderTotal) + ")";
        } else {
            tvTipFee.setText("");
        }

        txtTotalAmount.setText(amount);
    }
}
