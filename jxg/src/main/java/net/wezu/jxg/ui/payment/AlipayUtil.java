package net.wezu.jxg.ui.payment;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import net.wezu.framework.thread.ThreadPool;
import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.util.NumicUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 支付宝
 * Created by snox on 2015/12/2.
 */
public class AlipayUtil {

    private static final String TAG = "AlipayUtil";

    private static final int SDK_PAY_FLAG = 1;

    private static AlipayUtil instance;
    private static Object lock = new Object();
    private Activity mActivity;

    private IPayCallBack callBack;

    private AlipayUtil(Activity activity) {
        this.mActivity = activity;
    }

    public static AlipayUtil getInstance(Activity activity) {
        synchronized (lock) {
            if (instance == null) {
                instance = new AlipayUtil(activity);
            }
            return instance;
        }
    }

    public void setCallBack(IPayCallBack callBack) {
        this.callBack = callBack;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SDK_PAY_FLAG:
                    AlipayResult resultObj = new AlipayResult((String) msg.obj);
                    String resultStatus = resultObj.resultStatus;

                    // 判断resultStatus 为"9000"则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        ToastUtils.show(mActivity, "支付成功", Toast.LENGTH_SHORT);
                        if (callBack != null) {
                            callBack.onPaySuccess();
                        }
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000" 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastUtils.show(mActivity, "支付结果确认中", Toast.LENGTH_SHORT);
                        } else {
                            ToastUtils.show(mActivity, "支付失败", Toast.LENGTH_SHORT);
                            if (callBack != null) {
                                callBack.onPayFailed();
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void startPay(PayEntity payEntity) {
        String orderInfo = getOrderInfo(payEntity);
        String sign = AlipaySignUtils.sign(orderInfo, PaymentConstants.ALIPAY_PRIVATE_KEY);
        if (TextUtils.isEmpty(sign)) {
            ToastUtils.show(mActivity, "获取签名串错误", Toast.LENGTH_SHORT);
            return;
        }

        try {
            // 仅需对sign做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }

        String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
        Log.i("AlipayUtil", "支付宝订单info = " + payInfo);

        ThreadPool.newInstance().execute(new PayRunnable(payInfo));
    }

    private String getOrderInfo(PayEntity payEntity) {
        // 合作者身份ID
        String orderInfo = "partner=" + "\"" + PaymentConstants.ALIPAY_DEFAULT_PARTNER + "\"";

        // 卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + PaymentConstants.ALIPAY_DEFAULT_SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + payEntity.outTradeNo + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + payEntity.subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + payEntity.body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + NumicUtil.formatDouble(payEntity.totalFee) + "\"";

        //FIXME 测试金额
//		orderInfo += "&total_fee=" + "\"" + 0.01 + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + PaymentConstants.ALIPAY_CALLBACK_URL + "\"";

        // 接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
//		orderInfo += "&it_b_pay=\"30m\"";
        orderInfo += "&it_b_pay=\"" + payEntity.timeOut + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    class PayRunnable implements Runnable {
        String payInfo;

        public PayRunnable(String payInfo) {
            this.payInfo = payInfo;
        }

        @Override
        public void run() {
            // 构造 PayTask 对象
            PayTask alipay = new PayTask(mActivity);

            // 调用支付接口
            String result = alipay.pay(payInfo);
            Log.i(TAG, "result = " + result);

            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            handler.sendMessage(msg);
        }
    }
}
