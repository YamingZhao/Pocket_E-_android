package net.wezu.jxg.service;

/**
 * @author snox@live.com
 * @date 2015/10/22.
 */
public final class ServiceAddressConstant {

    public static final String PROTOCOL_CHARSET = "utf-8";

    public static String ROOT_ADDRESS = "http://121.43.109.121";
    public static String BASE_ADDRESS = ROOT_ADDRESS + "/DesktopModules/JXGAPI/Service.ashx";

    public static String ACTION_LOGIN = "?&PortalId=0&ModuleId=0&TabId=0&method=login";

    public static String ALIPAY_CALLBACK = "http://121.43.109.121/DesktopModules/JXGAPI/AliPayNotify.ashx";
}
