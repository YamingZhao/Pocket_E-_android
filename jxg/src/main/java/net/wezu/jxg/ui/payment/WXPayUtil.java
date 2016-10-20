package net.wezu.jxg.ui.payment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.wezu.framework.util.MD5;
import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.pay.wechat.XmlUtils;
import net.wezu.widget.dialog.CustomProgressDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class WXPayUtil {

//    private static WXPayUtil instance;
//    private static Object lock = new Object();

    private Activity activity;
    private IWXAPI api;

//    public static WXPayUtil getInstance(Activity activity) {
//        synchronized (lock) {
//            if (instance == null) {
//                instance = new WXPayUtil(activity);
//            }
//            return instance;
//        }
//    }

    public WXPayUtil(Activity activity) {
        this.activity = activity;

        api = WXAPIFactory.createWXAPI(activity, null);
        api.registerApp(PaymentConstants.WX_APP_ID);
    }


    public void startPay(PayEntity entity) {
        if (api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
            new GetPrepayIdTask().execute(entity);
        } else {
            ToastUtils.show(activity, "您的手机未安装微信客户端", Toast.LENGTH_SHORT);
        }
    }

    // region GetPrepayIdTask

    private class GetPrepayIdTask extends AsyncTask<PayEntity, Void, PrepayIdResult> {

        private final static String prepay_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

        private CustomProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = CustomProgressDialog.getDialog(activity);
            dialog.setMessage("正在获取预支付订单..");
            dialog.show();
        }

        @Override
        protected PrepayIdResult doInBackground(PayEntity... params) {

            byte[] buf = WXUtil.httpPost(prepay_url, genProductArgs(params[0]));
            if (buf == null || buf.length == 0) {
                return PrepayIdResult.error();
            }
            String content = new String(buf);
            Log.i("", "content = " + content);
            return PrepayIdResult.from(content);
        }

        @Override
        protected void onPostExecute(PrepayIdResult result) {
            super.onPostExecute(result);

            if (result.isSuccess) {
                sendPayReq(result);
            } else {
                ToastUtils.show(activity, "生成预订单失败，" + result.errmsg);
            }

            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    // endregion

    private String genProductArgs(PayEntity entity) {
        List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
        packageParams.add(new BasicNameValuePair("appid", PaymentConstants.WX_APP_ID));
        packageParams.add(new BasicNameValuePair("body", entity.productName));
        packageParams.add(new BasicNameValuePair("mch_id", PaymentConstants.WX_MCH_ID));
        packageParams.add(new BasicNameValuePair("nonce_str", genNonceStr()));
        packageParams.add(new BasicNameValuePair("notify_url", PaymentConstants.WX_CALLBACK_URL));
        packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradeNo()));
        packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
        packageParams.add(new BasicNameValuePair("total_fee", String.valueOf(entity.totalFee.multiply(new BigDecimal(100)).intValue())));
        packageParams.add(new BasicNameValuePair("trade_type", "APP"));

        String sign = XmlUtils.genPackageSign(PaymentConstants.WX_APP_KEY, packageParams);
        packageParams.add(new BasicNameValuePair("sign", sign));

        return XmlUtils.toXml(packageParams);
    }

    private String genOutTradeNo() {
        Random random = new Random();
        return XmlUtils.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private String genNonceStr() {
        Random random = new Random();
        return XmlUtils.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private void sendPayReq(PrepayIdResult result) {
        PayReq request = new PayReq();

        request.appId = PaymentConstants.WX_APP_ID;
        request.partnerId = PaymentConstants.WX_MCH_ID;
        request.prepayId = result.prepayId;
        request.packageValue = "Sign=WXPay";
        request.nonceStr = genNonceStr();
        request.timeStamp = genTimeStamp();
        request.sign = XmlUtils.getAppSign(PaymentConstants.WX_APP_KEY, XmlUtils.getRequestParams(request));

//        request.appId = PaymentConstants.WX_APP_ID;
//        request.partnerId = PaymentConstants.WX_MCH_ID;
//        request.prepayId = result.prepayId;
//        request.packageValue = "Sign=WXPay";
//        request.nonceStr = genNonceStr();
//        request.timeStamp = String.valueOf(genTimeStamp());

//        List<NameValuePair> signParams = new LinkedList<>();
//        signParams.add(new BasicNameValuePair("appid", request.appId));
//        signParams.add(new BasicNameValuePair("noncestr", request.nonceStr));
//        signParams.add(new BasicNameValuePair("package", request.packageValue));
//        signParams.add(new BasicNameValuePair("partnerid", request.partnerId));
//        signParams.add(new BasicNameValuePair("prepayid", request.prepayId));
//        signParams.add(new BasicNameValuePair("timestamp", request.timeStamp));
//
//        request.sign = genAppSign(signParams);

        //api.registerApp(PaymentConstants.WX_APP_ID);
        api.sendReq(request);
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(PaymentConstants.WX_APP_KEY);
        return XmlUtils.getMessageDigest(sb.toString().getBytes());
    }

    private String genTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    private static class PrepayIdResult {

        public String prepayId;
        public boolean isSuccess;
        public String errmsg;
        public int err_code;

        public static PrepayIdResult from(String content) {
            Map<String, String> response = XmlUtils.decodeXml(content);
            if (response == null) {
                return PrepayIdResult.error();
            }

            PrepayIdResult result = new PrepayIdResult();

            result.isSuccess = "SUCCESS".equals(response.get("return_code"));
            if (!result.isSuccess) {
                result.errmsg = response.get("return_msg");
            } else {
                result.isSuccess = "SUCCESS".equals(response.get("result_code"));

                if (result.isSuccess) {
                    result.prepayId = response.get("prepay_id");
                } else {
                    result.errmsg = response.get("err_code_des");
                }
            }

            return result;
        }

        public static PrepayIdResult error() {
            PrepayIdResult result = new PrepayIdResult();
            result.isSuccess = false;
            result.errmsg = "网络错误";

            return result;
        }

        private PrepayIdResult() {}
    }
}