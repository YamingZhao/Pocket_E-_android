package net.wezu.jxg.pay.wechat;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.tencent.mm.sdk.modelpay.PayReq;

import net.wezu.framework.util.MD5;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by snox on 2016/5/11.
 */
public class XmlUtils {
    public static String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<").append(params.get(i).getName()).append(">")
                    .append(params.get(i).getValue())
                    .append("</").append(params.get(i).getName()).append(">");
        }
        sb.append("</xml>");

        Log.e("orion", sb.toString());
        return sb.toString();
    }


    public static Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if (!"xml".equals(nodeName)) {
                            //实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }


    /**
     * 生成签名
     */

    private static String genPackageSign(WeChatPayInit mInit, List<NameValuePair> params) {
        return genPackageSign(mInit.getSellerKey(), params);
    }

    public static String genPackageSign(String sellerKey, List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(sellerKey);


        String packageSign = getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.d("orion", packageSign);
        return packageSign;
    }


    /**
     * App签名
     * @param mInit
     * @param params
     * @return
     */
    public static String getAppSign(WeChatPayInit mInit, List<NameValuePair> params){
        return getAppSign(mInit.getSellerKey(), params);
    }

    public static String getAppSign(String sellerKey, List<NameValuePair> params){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(sellerKey);

        return getMessageDigest(sb.toString().getBytes());
    }


    public static String genProductArgs(WeChatPayInit mInit, WeChatProduct product) {

        try {
            String nonceStr = product.getNonce();


            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("appid", mInit.getPartner()));
            if (!TextUtils.isEmpty(product.getAttach())){
                packageParams.add(new BasicNameValuePair("attach", product.getAttach()));
            }
            packageParams.add(new BasicNameValuePair("body", product.getSubject()));
            packageParams.add(new BasicNameValuePair("detail", product.getBody()));
            packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
            packageParams.add(new BasicNameValuePair("mch_id", mInit.getSeller()));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams.add(new BasicNameValuePair("notify_url", product.getAsynServer()));
            packageParams.add(new BasicNameValuePair("out_trade_no", product.getOutTradeNo()));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", product.getSpbillIp()));
            packageParams.add(new BasicNameValuePair("total_fee", product.getPrice()));
            packageParams.add(new BasicNameValuePair("trade_type", product.getTradeType()));



            String sign = XmlUtils.genPackageSign(mInit, packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));


            String xmlstring = XmlUtils.toXml(packageParams);

//            return xmlstring;

            return new String(xmlstring.getBytes(), "ISO8859-1");

        } catch (Exception e) {
            Log.e("WeChat", "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }


    }


    public static List<NameValuePair> getRequestParams(PayReq req){
        List<NameValuePair> signParams = new LinkedList<>();

        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        return signParams;
    }

    public static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
}
