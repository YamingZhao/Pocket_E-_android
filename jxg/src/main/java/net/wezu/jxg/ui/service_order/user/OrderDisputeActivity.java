package net.wezu.jxg.ui.service_order.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.data.ServiceOrderStatus;
import net.wezu.jxg.model.OrderDispute;
import net.wezu.jxg.model.OrderEntity;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.widget.MaterialDialog;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 订单申诉
 * Created by snox on 2016/4/27.
 */
public class OrderDisputeActivity extends BaseActivity {

    @Bind(R.id.edt_message) EditText edtMessage;
    @Bind(R.id.btn_submit) Button btnSubmit;

    private OrderEntity orderEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_dispute);

        setTitle("申诉");
        setDefaultBackButton();

        orderEntity = getIntent().getParcelableExtra("order");
        if (orderEntity == null) {
            finish();
            return;
        }

        edtMessage.setClickable(false);
        btnSubmit.setEnabled(false);

        ServiceOrderService.getServiceComplain(requestTag, orderEntity.order.OrderId, new RequestManager.ResponseListener<OrderDispute>() {
            @Override
            public void success(OrderDispute result, String msg) {
                if (result.OrderDisputeId == 0) {
                    btnSubmit.setEnabled(true);
                } else {
                    edtMessage.setText(result.Comment);
                    btnSubmit.setText("进行中");
                }
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });

        if (ServiceOrderStatus.CLOSED.equals(orderEntity.order.OrderStatus)
                && !"OFFLINE".equals(orderEntity.order.PayTypeId)) {
            setRightButton("退款", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refund();
                }
            });
        }
    }

    @OnClick(R.id.btn_submit) void submit() {

        String comment = edtMessage.getText().toString();

        if (TextUtils.isEmpty(comment)) {
            edtMessage.setError("请输入申诉内容");
            toast("请输入申诉内容");
            return;
        }

        ServiceOrderService.postServiceComplain(requestTag, orderEntity.order.OrderId, "FEEDBACK", comment, new RequestManager.ResponseListener<OrderDispute>() {
            @Override
            public void success(OrderDispute result, String msg) {
                btnSubmit.setEnabled(false);
                btnSubmit.setText("进行中");

                final MaterialDialog dialog = new MaterialDialog(OrderDisputeActivity.this);
                dialog
                        .setTitle("退款申请")
                        .setMessage("你的投诉已经收到，我们会尽快与你联系")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });
    }

    private void refund() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog
                .setTitle("退款申请")
                .setMessage("您需要对本订单进行退款处理?")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doRefund();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void doRefund() {
        ServiceOrderService.postServiceComplain(requestTag, orderEntity.order.OrderId, "REFUND", "用户退款申请", new RequestManager.ResponseListener<OrderDispute>() {
            @Override
            public void success(OrderDispute result, String msg) {
                final MaterialDialog dialog = new MaterialDialog(OrderDisputeActivity.this);
                dialog
                        .setTitle("退款申请")
                        .setMessage("你的退款申请已经收到，我们会尽快与你联系")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });
    }
}
