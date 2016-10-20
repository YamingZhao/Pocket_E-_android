package net.wezu.jxg.pay.wechat;

import net.wezu.jxg.pay.PayInit;

public class WeChatPayInit extends PayInit {

    private String partner;

    private String seller;

    private String sellerKey;

    public WeChatPayInit(String partner, String seller, String sellerKey) {
        this.partner = partner;
        this.seller = seller;
        this.sellerKey = sellerKey;
    }

    /**
     * APP ID
     * @return
     */
    public String getPartner() {
        return partner;
    }

    /**
     * 商户号
     * @return
     */
    public String getSeller() {
        return seller;
    }

    /**
     * API KEY
     * @return
     */
    public String getSellerKey() {
        return sellerKey;
    }
}

