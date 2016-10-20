package net.wezu.jxg.pay;

import android.content.Context;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by snox on 2016/5/11.
 */
public class MobilePayTool {

    public static boolean checkWechatInvalidPay(final Context context, String app_id) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, app_id);
        return iwxapi.isWXAppInstalled();
    }
}
