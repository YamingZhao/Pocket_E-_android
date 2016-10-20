package net.wezu.jxg.ui.alipay;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;

import com.alipay.sdk.app.PayTask;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.util.alipay.PayResult;

/**
 * Created by snox on 2015/11/25.
 */
public class AlipayActivity extends Activity {

    static final int SDK_PAY_FLAG = 1;
    static final int SDK_CHECK_FLAG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        check();
    }

    private void check() {
        Runnable checkRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask payTask = new PayTask(AlipayActivity.this);

                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist ? "OK" : "FAIL";
                mHandler.sendMessage(msg);
            }
        };
    }

    private void pay() {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(AlipayActivity.this);

                String result = alipay.pay(getIntent().getExtras().getString("payInfo"));

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((String) msg.obj);

                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 支付成功
                    } else if (TextUtils.equals(resultStatus, "8000")) {
                        // 支付结果确认中
                    } else {
                        // 支付失败
                    }
                    finish();

                    break;

                case SDK_CHECK_FLAG:
                    if (String.valueOf(msg.obj).equals("OK")) {
                        pay();
                    } else {
                        ToastUtils.show(AlipayActivity.this, "您的手机暂无支付宝认证账户，请在支付宝客户端中添加后再试");
                    }
                    break;
            }
        }
    };
}
