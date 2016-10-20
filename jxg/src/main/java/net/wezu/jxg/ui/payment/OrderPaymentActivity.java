package net.wezu.jxg.ui.payment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.model.ProductOrderListItem;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.OrderEntity;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.payment.AlipayUtil;
import net.wezu.jxg.ui.payment.IPayCallBack;
import net.wezu.jxg.ui.payment.PayEntity;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.jxg.util.NumicUtil;
import net.wezu.widget.MaterialDialog;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 订单支付
 *
 * Created by snox on 2015/12/2.
 */
public class OrderPaymentActivity extends BaseActivity {

    public static final String SERVICE_ORDER = "SERVICE_ORDER";
    public static final String PRODUCT_ORDER = "PRODUCT_ORDER";

    @Bind(R.id.txt_service_name) TextView txtServiceName;
    @Bind(R.id.txt_worker_name) TextView txtWorkerName;
    @Bind(R.id.txt_total_amount) TextView txtTotalAmount;
    @Bind(R.id.tv_receiver) TextView tvReceiver;
    @Bind(R.id.tv_shipping_address) TextView tvShippingAddress;
    @Bind(R.id.tv_invoice_type) TextView tvInvoiceType;

    @Bind(R.id.radio_offline)
    ImageView radioOffline;

    @Bind(R.id.radio_alipay) ImageView radioAlipay;
    @Bind(R.id.radio_weixin) ImageView radioWeixin;

    @Bind(R.id.payment_offline)
    LinearLayout offlinePayment;

    private BigDecimal payAmount;
    private PayEntity payEntity;

    enum PaymentMethod {
        Unknown,
        Alipay,
        WeixinPay,
        UnionPay,
        Offline,
    }

    private PaymentMethod paymentMethod = PaymentMethod.Unknown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_order_payment);

        payEntity = new PayEntity();

        OrderEntity orderEntity = getIntent().getParcelableExtra(SERVICE_ORDER);
        if (orderEntity != null) {
            setContentView(R.layout.activity_service_order_payment);
            setServiceOrder(orderEntity);
        } else {
            setContentView(R.layout.activity_payment_mall_order);
            ProductOrderListItem productOrder = getIntent().getParcelableExtra(PRODUCT_ORDER);
            setProductOrder(productOrder);
        };
        setTitle("支付");
        setDefaultBackButton();
    }

    private int orderId;

    private void setServiceOrder(OrderEntity orderEntity) {

        orderId = orderEntity.order.OrderId;

        payAmount = orderEntity.order.OrderTotal;

        txtServiceName.setText(orderEntity.order.ServiceType);
        txtWorkerName.setText(orderEntity.order.WorkerFirstname);
        txtTotalAmount.setText(NumicUtil.formatDouble(orderEntity.order.OrderTotal));// new DecimalFormat("####.00").format(orderEntity.order.OrderTotal));

        payEntity.productName = "服务 - " + orderEntity.order.ProductName;

        payEntity.outTradeNo = orderEntity.order.OrderNo;
        payEntity.subject = orderEntity.order.ServiceType;
        payEntity.timeOut = "1c";
        payEntity.totalFee = payAmount;// NumicUtil.formatDouble(payAmount);
    }

    private void setProductOrder(ProductOrderListItem order) {
        orderId = order.OrderId;

        payAmount = order.OrderTotal;

        offlinePayment.setVisibility(View.GONE);

        txtServiceName.setText(order.OrderNo);
        txtWorkerName.setText(String.valueOf(order.details.size()));
        txtTotalAmount.setText(NumicUtil.formatDouble(order.OrderTotal));// new DecimalFormat("####.00").format(orderEntity.order.OrderTotal));

        tvReceiver.setText(order.ReceiverName);
        tvShippingAddress.setText(order.Address);
        tvInvoiceType.setText(order.InvoiceTitle);

        payEntity.productName = "商城订单";
        payEntity.outTradeNo = order.OrderNo;
        payEntity.subject = order.OrderNo;
        payEntity.timeOut = "1c";
        payEntity.totalFee = payAmount; // NumicUtil.formatDouble(payAmount);
    }

    @OnClick({R.id.payment_alipay, R.id.payment_weixin, R.id.payment_offline}) void selectPaymethod(View view) {
        radioOffline.setImageResource(R.mipmap.ic_pay_normal);
        radioWeixin.setImageResource(R.mipmap.ic_pay_normal);
        radioAlipay.setImageResource(R.mipmap.ic_pay_normal);

        switch (view.getId()) {
            case R.id.payment_alipay:
                radioAlipay.setImageResource(R.mipmap.ic_pay_choice);
                paymentMethod = PaymentMethod.Alipay;
                break;
            case R.id.payment_weixin:
                radioWeixin.setImageResource(R.mipmap.ic_pay_choice);
                paymentMethod = PaymentMethod.WeixinPay;
                break;
            case R.id.payment_offline:
                radioOffline.setImageResource(R.mipmap.ic_pay_choice);
                paymentMethod = PaymentMethod.Offline;
                break;
        }
    }

    @OnClick(R.id.btn_confirm) void submit() {
        if (FastClickUtil.isFastClick()) return;

        switch (paymentMethod) {
            case Unknown:
                ToastUtils.show(this, "请选择支付方式");
                break;

            case Alipay: doAlipay(); break;

            case WeixinPay: doWxpay(); break;

            case Offline:
                final MaterialDialog dialog = new MaterialDialog(this);
                dialog.setTitle("线下付款")
                        .setMessage("线下付款将不受平台的保护，点击确认继续")
                        .setPositiveButton("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                offlinePayment();
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
        }
    }

    private void offlinePayment() {
        ServiceOrderService.requestOfflinePayment(requestTag, orderId, new RequestManager.ResponseListener<OrderListItemModel>() {
            @Override
            public void success(OrderListItemModel result, String msg) {
                toast("请求线下付款成功，请机修工确认");
                finish();
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });
    }

    private void doAlipay() {
        AlipayUtil alipayUtil = AlipayUtil.getInstance(this);
        alipayUtil.setCallBack(alipayListener);
        alipayUtil.startPay(payEntity);
    }

    IPayCallBack alipayListener = new IPayCallBack() {

        @Override
        public void onPaySuccess() {
            toast("支付成功");

            setResult(RESULT_OK);

//            Intent intent = new Intent(mContext, RebatePayCommentActivity.class);
//            intent.putExtra(RebateKey.KEY_INTENT_ORDER_ID, mEntity.data.orderId);
//            intent.putExtra(RebateKey.KEY_INTENT_REBATE_ITEM_ID, detailEntity.data.itemId);
//            Log.i("--传给RebatePayCommentActivity的itemId:" + detailEntity.data.itemId);
//            intent.putExtra(RebateKey.KEY_INTENT_IS_NEW_ORDER, true);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//			if (mActivity instanceof RebatePayActivity) {
//				((RebatePayActivity) mActivity).finish();
//			}
        }

        @Override
        public void onPayFailed() {
            toast("支付失败");
        }
    };

    private void doWxpay() {
        WXPayUtil wxPay = new WXPayUtil(this);
        wxPay.startPay(payEntity);
    }
}
