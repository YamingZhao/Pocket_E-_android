package net.wezu.jxg.ui.user.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.payment.AlipayUtil;
import net.wezu.jxg.ui.payment.IPayCallBack;
import net.wezu.jxg.ui.payment.PayEntity;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.jxg.util.NumicUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by snox on 2016/4/6.
 */
public class ChangePaymentAccountActivity extends ChangProfileBaseActivity {

    @Bind(R.id.edit_label) TextView edtLabel;
    @Bind(R.id.edit_nickname) EditText edtNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_pay_account);

        nickName = Application.getInstance().getUserModel().Profile.getProperty("PaymentAccount");

        edtLabel.setText("支付宝账号");
        setDefaultBackButton();
        setTitle("修改支付账号");

        edtNickname.setText(nickName);
    }

    private String nickName;

    @OnClick(R.id.btn_confirm) void onSave() {
        if (FastClickUtil.isFastClick()) return;

        nickName = edtNickname.getText().toString();
        if (TextUtils.isEmpty(nickName)) {
            ToastUtils.show(this, "不能为空");
            return;
        }

        PayEntity payEntity = new PayEntity();

        String username = Application.getInstance().getUserModel().Username;

        payEntity.outTradeNo = username + (new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date()));
        payEntity.subject = "用户 " + username + " 付款账号验证";
        payEntity.timeOut = "1c";
        payEntity.totalFee = new BigDecimal(0.01);// NumicUtil.formatDouble(0.01);

        doAlipay(payEntity);

        // 这里需要进行一笔支付，才能回调写入

        //updateProfile(field, nickName);
    }

    private void doAlipay(PayEntity payEntity) {
        AlipayUtil alipayUtil = AlipayUtil.getInstance(this);
        alipayUtil.setCallBack(alipayListener);
        alipayUtil.startPay(payEntity);
    }

    IPayCallBack alipayListener = new IPayCallBack() {

        @Override
        public void onPaySuccess() {
            toast("支付成功");

            updateProfile("PaymentAccount", nickName);

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
}
