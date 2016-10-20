package net.wezu.jxg.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.wezu.jxg.ui.payment.PaymentConstants;

/**
 * 微信支付回调
 * Created by 崔进东 on 2016/5/11.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        api = WXAPIFactory.createWXAPI(this, PaymentConstants.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("WX PAY");
//            builder.setMessage(String.format("微信支付结果：%s", resp.errStr + ";code=" + String.valueOf(resp.errCode)));
//            builder.show();

            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    break;

                case BaseResp.ErrCode.ERR_COMM:
                    Toast.makeText(WXPayEntryActivity.this, "支付失败，错误码 -1", Toast.LENGTH_SHORT).show();
                    break;

                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    Toast.makeText(WXPayEntryActivity.this, "支付取消", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(WXPayEntryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    break;
                //case BaseResp.ErrCode.ERR_AUTH_DENIED
            }
            finish();
        }
    }
}
