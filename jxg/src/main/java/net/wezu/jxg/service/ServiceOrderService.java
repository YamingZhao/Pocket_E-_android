package net.wezu.jxg.service;

import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;

import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.PagedResult;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.Comment;
import net.wezu.jxg.model.OrderDispute;
import net.wezu.jxg.model.OrderEntity;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.model.OrderLog;
import net.wezu.jxg.model.ProfileProperty;
import net.wezu.jxg.model.ServicesAndCars;
import net.wezu.jxg.model.Withdraw;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务订单业务服务层
 *
 * Created by snox on 2015/11/17.
 */
public final class ServiceOrderService {

    private ServiceOrderService() { }

    /**
     * 获取服务订单列表
     * @param requestTag
     * @param serviceOrderStatus 服务订单的状态，CREATED 已创建 CATCHED  已抢单 CONFIRMED 已确认 SERVCING 正在服务 CANCELLED 已经取消 WORKDONE 完工 CLOSED 已经完成
     * @param listener
     */
    public static void list(String requestTag, int start, int size, String serviceOrderStatus, RequestManager.ResponseListener<PagedResult<OrderListItemModel>> listener) {
        list(requestTag, start, size, serviceOrderStatus, null, listener);
    }

    public static void list(String requestTag, int start, int size, String serviceOrderStatus, Map<String, String> params, RequestManager.ResponseListener<PagedResult<OrderListItemModel>> listener) {
        if (params == null) {
            params = new HashMap<>();
        }

        if (!TextUtils.isEmpty(serviceOrderStatus)) {
            params.put("status", serviceOrderStatus);
        }
        RequestManager.getInstance().getPagedList("getserviceorders", start, size, requestTag, params, OrderListItemModel.class, listener);
    }

    public static void create(String requestTag, Map<String, String> orderParams, Map<String, File> files, RequestManager.ResponseListener<OrderEntity> listener) {
        //RequestManager.getInstance().post("createserviceorder", requestTag, orderParams, OrderEntity.class, listener);
        RequestManager.getInstance().upload(requestTag, "createserviceorder", orderParams, files, OrderEntity.class, listener);
    }

    public static void getServicesAndCars(String category, RequestManager.ResponseListener<ServicesAndCars> listener) {

        Map<String, String> params = new HashMap<>();
        params.put("category", category);

        RequestManager.getInstance().post("getservicesandcars", null, params, ServicesAndCars.class, listener);
        //RequestManager.getInstance().upload(requestTag, "getservicesandcars", orderParams, files, OrderEntity.class, listener);
    }

    /**
     * 抢单
     * @param requestTag 请求标识
     * @param orderId 服务订单编号
     * @param listener 监听器
     */
    public static void get(String requestTag, int orderId, RequestManager.ResponseListener<OrderEntity> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));

        RequestManager.getInstance().post("getserviceorderdetail", requestTag, params, OrderEntity.class, listener);
    }

    /**
     * 抢单
     * @param requestTag 请求标识
     * @param orderId 服务订单编号
     * @param listener 监听器
     */
    public static void getLogs(String requestTag, int orderId, RequestManager.ResponseListener<List<OrderLog>> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));

        RequestManager.getInstance().getList("getserviceorderlogs", requestTag, params, OrderLog.class, listener);
    }

    /**
     * 抢单
     * @param requestTag 请求标识
     * @param orderId 服务订单编号
     * @param listener 监听器
     */
    public static void catchOrder(String requestTag, int orderId, LatLng latLng, String workerlocation, RequestManager.ResponseListener<OrderListItemModel> listener) {

        if (!Application.getInstance().isWorker()) {
            // throw new IllegalStateException();
            listener.error("只有机修工才能抢单");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        if (latLng != null) {
            params.put("lng", String.valueOf(latLng.longitude));
            params.put("lat", String.valueOf(latLng.latitude));
        }
        if (!TextUtils.isEmpty(workerlocation)) {
            params.put("workerlocation", workerlocation);
        }

        RequestManager.getInstance().post("catchserviceorder", requestTag, params, OrderListItemModel.class, listener);
    }

    /**
     * 重新推送
     *
     * @param requestTag 请求标识
     * @param orderId 订单编号
     * @param listener 请求监听
     */
    public static void resend(String requestTag, int orderId, RequestManager.ResponseListener<OrderListItemModel> listener) {
        if (Application.getInstance().isWorker()) {
            listener.error("只有用户才能重新推送服务订单");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        params.put("action", "PUSH_ORDER");

        RequestManager.getInstance().post("processserviceorder", requestTag, params, OrderListItemModel.class, listener);
    }

    /**
     * 确认订单
     * @param requestTag 请求标识
     * @param orderId 服务订单编号
     * @param listener 监听器
     */
    public static void confirm(String requestTag, int orderId, RequestManager.ResponseListener<OrderListItemModel> listener) {

        if (Application.getInstance().isWorker()) {
            listener.error("只有用户才能确认服务订单");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        params.put("action", "CONFIRM_ORDER");

        RequestManager.getInstance().post("processserviceorder", requestTag, params, OrderListItemModel.class, listener);
    }

    /**
     * 确认订单
     * @param requestTag 请求标识
     * @param orderId 服务订单编号
     * @param listener 监听器
     */
    public static void addFee(String requestTag, int orderId, BigDecimal fee, String description, RequestManager.ResponseListener<OrderListItemModel> listener) {

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        params.put("action", "ADD_FEE");
        params.put("fee", "" + fee);
        params.put("description", description);

        RequestManager.getInstance().post("processserviceorder", requestTag, params, OrderListItemModel.class, listener);
    }

    /**
     * 确认订单
     * @param requestTag 请求标识
     * @param orderId 服务订单编号
     * @param listener 监听器
     */
    public static void changeWorker(String requestTag, int orderId, RequestManager.ResponseListener<OrderListItemModel> listener) {

        if (Application.getInstance().isWorker()) {
            listener.error("只有用户才能确认服务订单");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        params.put("action", "CANCEL_WORKER");

        RequestManager.getInstance().post("processserviceorder", requestTag, params, OrderListItemModel.class, listener);
    }

    /**
     * 开始服务
     * @param requestTag 请求标识
     * @param orderId 服务订单编号
     * @param listener 监听器
     */
    public static void startService(String requestTag, int orderId, RequestManager.ResponseListener<OrderListItemModel> listener) {

        if (!Application.getInstance().isWorker()) {
            listener.error("只有机修工才能开始服务");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        params.put("action", "START_SERVICE");

        RequestManager.getInstance().post("processserviceorder", requestTag, params, OrderListItemModel.class, listener);
    }

    /**
     * 完工
     * @param requestTag 请求标识
     * @param orderId 服务订单编号
     * @param listener 监听器
     */
    public static void done(String requestTag, int orderId, RequestManager.ResponseListener<OrderListItemModel> listener) {

        if (Application.getInstance().isWorker()) {
            listener.error("只有用户才能确认完工");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        params.put("action", "WORK_DONE");

        RequestManager.getInstance().post("processserviceorder", requestTag, params, OrderListItemModel.class, listener);
    }

    /**
     * 请求线下支付
     * @param requestTag 请求标识
     * @param orderId 服务订单编号
     * @param listener 监听器
     */
    public static void requestOfflinePayment(String requestTag, int orderId, RequestManager.ResponseListener<OrderListItemModel> listener) {

        if (Application.getInstance().isWorker()) {
            listener.error("只有用户才能请求线下支付");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        params.put("action", "REQUEST_OFFLINEPAYMENT");

        RequestManager.getInstance().post("processserviceorder", requestTag, params, OrderListItemModel.class, listener);
    }

    /**
     * 线下支付
     * @param requestTag 请求标识
     * @param orderId 服务订单编号
     * @param listener 监听器
     */
    public static void confirmOfflinePayment(String requestTag, int orderId, RequestManager.ResponseListener<OrderListItemModel> listener) {

        if (!Application.getInstance().isWorker()) {
            listener.error("只有机修工才能确认线下付款");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        //params.put("action", "WORK_DONE");

        RequestManager.getInstance().post("confirmorderoffinepayment", requestTag, params, OrderListItemModel.class, listener);
    }



    /**
     * 确认订单
     * @param requestTag 请求标识
     * @param orderId 服务订单编号
     * @param listener 监听器
     */
    public static void cancel(String requestTag, int orderId, RequestManager.ResponseListener<OrderListItemModel> listener) {

        if (Application.getInstance().isWorker()) {
            listener.error("只有用户才能确认服务订单");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        params.put("action", "CANCEL_ORDER");

        RequestManager.getInstance().post("processserviceorder", requestTag, params, OrderListItemModel.class, listener);
    }

    public static void addComment(String requestTag,
                                  int orderId, int attribute, int speed, int quality, int clean,
                                  String words,
                                  RequestManager.ResponseListener<Comment> listener) {
        if (Application.getInstance().isWorker()) {
            listener.error("只有用户才能评价服务订单");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        params.put("comment", words);
        params.put("rateattitude", String.valueOf(attribute));
        params.put("ratespeed", String.valueOf(speed));
        params.put("ratequality", String.valueOf(quality));
        params.put("rateclean", String.valueOf(clean));
        params.put("type", "service");


        RequestManager.getInstance().post("postcomment", requestTag, params, Comment.class, listener);
    }

    public static void postServiceComplain(String requestTag, int orderId, String type, String feedback, RequestManager.ResponseListener<OrderDispute> listener) {
        if (Application.getInstance().isWorker()) {
            listener.error("只有用户才能申诉服务订单");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));
        params.put("description", feedback);
        params.put("type", type);


        RequestManager.getInstance().post("postservicecomplain", requestTag, params, OrderDispute.class, listener);
    }

    public static void getServiceComplain(String requestTag, int orderId, RequestManager.ResponseListener<OrderDispute> listener) {
        if (Application.getInstance().isWorker()) {
            listener.error("只有用户才能申诉服务订单");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));

        RequestManager.getInstance().post("getservicecomplain", requestTag, params, OrderDispute.class, listener);
    }

    public static void getWithDrawList(String requestTag, String clearedStatus, RequestManager.ResponseListener<Withdraw> listener) {
        if (!Application.getInstance().isWorker()) {
            listener.error("只有机修工才能提现");
            return;
        }

        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(clearedStatus)) {
            params.put("clearedstatus", clearedStatus);
        }


        RequestManager.getInstance().post("getwithdrawlist", requestTag, params, Withdraw.class, listener);
    }

    public static void requestWithdraw(String requestTag, int orderId, RequestManager.ResponseListener<OrderListItemModel> listener) {
        if (!Application.getInstance().isWorker()) {
            listener.error("只有机修工才能提现");
            return;
        }

        if (!isPaymentAccountVerified()) {
            listener.error("付款账户没有验证");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(orderId));

        RequestManager.getInstance().post("requestwithdraw", requestTag, params, OrderListItemModel.class, listener);
    }

    public static void requestWithdrawAll(String requestTag, RequestManager.ResponseListener<Object> listener) {
        if (!Application.getInstance().isWorker()) {
            listener.error("只有机修工才能提现");
            return;
        }

        if (!isPaymentAccountVerified()) {
            listener.error("付款账户没有验证");
            return;
        }

        Map<String, String> params = new HashMap<>();

        RequestManager.getInstance().post("requestwithdrawall", requestTag, params, Object.class, listener);
    }

    private static boolean isPaymentAccountVerified() {
        return "true".equals(Application.getInstance().getUserModel().getProperty(ProfileProperty.PN_PaymentAccountVerified));
    }



    /**
     * 机修工评论
     * @param requestTag 请求标识
     * @param workerId 服务订单编号
     * @param listener 监听器
     */
    public static void getWorkerComment(String requestTag, int workerId, RequestManager.ResponseListener<List<Comment>> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("workerid", String.valueOf(workerId));

        //RequestManager.getInstance().getList("getworkerservicecomments", requestTag, params, Comment.class, listener);
        RequestManager.getInstance().getList("getworkercomments", requestTag, params, Comment.class, listener);
    }
}
