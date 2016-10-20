package net.wezu.jxg.ui.payment;

import net.wezu.jxg.service.ServiceAddressConstant;

import java.math.BigDecimal;

/**
 * Created by snox on 2015/12/2.
 */
public class PayEntity {

    public int orderId;

    public String outTradeNo;
    public String subject;
    public String body;
    public BigDecimal totalFee;
    public String productName;
    //public String notifyUrl;
    public String timeOut;//设置未付款交易的超时时间

//    // 商户id
//    public String ALIPAY_DEFAULT_PARTNER;
//    // 收款支付宝账号
//    public String ALIPAY_DEFAULT_SELLER;
//    // 商户私钥，自助生成
//    public String ALIPAY_PRIVATE_KEY;
//    // 支付宝公钥
//    public String ALIPAY_PUBLIC_KEY;
//
//
//    public String WX_APPID;
//    public String WX_PARTNERID;
//    public String WX_PREPAY_ID;
//    public String WX_NONCE_STR;
//    public String WX_TIMESTAMP;
//    public String WX_WX_PACKAGE;
//    public String WX_WX_SIGN;
//
//    public PayEntity() {
//        ALIPAY_DEFAULT_PARTNER = PaymentConstants.ALIPAY_DEFAULT_PARTNER;
//        ALIPAY_DEFAULT_SELLER = PaymentConstants.ALIPAY_DEFAULT_SELLER;
//        ALIPAY_PRIVATE_KEY = PaymentConstants.ALIPAY_PRIVATE_KEY;
//        ALIPAY_PUBLIC_KEY = PaymentConstants.ALIPAY_PUBLIC_KEY;
//    }
}
