package net.wezu.jxg.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.util.alipay.PayResult;
import net.wezu.jxg.util.alipay.SignUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by snox on 2015/11/18.
 */
public class AlipayDemoActivity extends FragmentActivity {

    // 商户PID
    public static final String PARTNER = "2088021710879100";
    // 商户收款账号2xw4w
    public static final String SELLER = "1725571723@qq.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICXQIBAAKBgQCppfnbvwu0eCxsd5blwJFEkp/ryK3/cphCg35+2EHi9gm/HzsrHcJmSbLxC6HgjEb/KeqS074DCa36Sfp1z/rEscXrKIaGk11MPjXJeRvemzVF+IpVx609ougyXWsTBqkYLstH9w4vFgM22nQoboe/LwjHXKhzv1pDXq/kLvZqbQIDAQABAoGAcTREKpKtaNvYKPkURHPfe/arqFbdZNw/JgNA4bvFG4I6tsJMZVUSZ9c+BQNSHdj9dz+tPBT3cmd/JMlAzGTgXFH2eAwiGEabbv+e/IydnVjyxUnDuPL3o3OwoUqIltrK5OXLlxzKB7jmUMahKo2vhjYq/G+3VXLImUuVhuQDjAECQQDdILl1pF+actDr7VLhrGs9B6cVjgIWjUw3eSoF+VSEjX9b1+zBdCtLKEXtpSwOmNFvmyEvTvhkbhaiOIH7qpFBAkEAxGbxwncqRRB5thdQPVIHjHI86ekiRQ871mr/2MGbFZ0/43Sgij3NW6TRhP8263oEN3LJBtsmNF5w8axDrmBiLQJBAIA9NTEDhao1gfIA7TsRWhPHt19pvs5DXXsiWnuicCB7SuwBmj1K2Ly6e2tpidZhS7yU07by7lyX96pWaROBhMECQEeo5/z0Tc/1OvNdfkFhdK+h3ufZ0E8yR7rsP31u5o70WSM8onbOJeeSM4A7Pmacln1EvGRsAXyKGNuU5vzw3eECQQCZDaO4xX2QwyqEcnZxuDvm1FyvfyAQ6eI08ApVNNMgdItWshWm9T9NbzNfhYMLDiqIgY7ddIxkfBniZvQONuBN";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);

                    String resultStatus = payResult.getResultStatus();

                    if (TextUtils.equals(resultStatus, "9000")) {
                        ToastUtils.show(AlipayDemoActivity.this, "支付成功");
                    } else if (TextUtils.equals(resultStatus, "8000")) {
                        ToastUtils.show(AlipayDemoActivity.this, "支付结果确认中");
                    } else {
                        ToastUtils.show(AlipayDemoActivity.this, "支付失败");
                    }
                    break;

                case SDK_CHECK_FLAG:
                    ToastUtils.show(AlipayDemoActivity.this, "检查结果为：" + msg.obj);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alipay_demo);
    }

    public void check(View v) {
        Runnable checkRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造 PayTask 对象
                PayTask payTask = new PayTask(AlipayDemoActivity.this);
                // 调用查询接口，获取查询对象
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();
    }

    public void pay(View v) {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {

            ToastUtils.show(this, "参数配置不正确");
            return;
        }

        String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"" + getSignType();

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造 PayTask 对象
                PayTask alipay = new PayTask(AlipayDemoActivity.this);

                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        try {
            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getOrderInfo(String subject, String body, String price) {
// 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        String url = "http://alipay.tunnel.mobi/hibernateTest/index.jsp";
        url = "http://121.43.109.121/DesktopModules/JXGAPI/AliPayNotify.ashx";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + url + "\"";

        // 服务接口名称， 固定值
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
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());

        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + Math.abs(r.nextInt());
        return key.substring(0, 15);
    }

    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    /**
     * get the sdk version. 获取SDK版本号
     *
     */
    public void getSDKVersion(View v) {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }
}
