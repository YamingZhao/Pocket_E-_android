package net.wezu.jxg.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.service.UserService;
import net.wezu.jxg.ui.MainActivity;
import net.wezu.jxg.ui.service_order.ServiceOrderDetailActivity;;

import net.wezu.framework.eventbus.EventBus;
import net.wezu.jxg.ui.service_order.worker.WorkerServiceOrderDetailActivity;

/**
 * @author snox@live.com
 * @date 2015/10/29.
 */
public class GTPushReceiver extends BroadcastReceiver {

    //public static StringBuilder payloadData = new StringBuilder();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("PReceiver", "onReceive() action" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {

            case PushConsts.GET_MSG_DATA:

                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    try {
                        String data = new String(payload);

                        Gson gson = new Gson();
                        PushMessage msg = gson.fromJson(data, PushMessage.class);

                        processPushMessage(context, msg);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;

            case PushConsts.GET_CLIENTID:
                if (Application.getInstance().isLogin()) {
                    UserService.bind(bundle.getString("clientid"), null);
                }
                break;

            case PushConsts.THIRDPART_FEEDBACK:
                break;

            default:
                break;
        }
    }

    private int id = 0;

    private void processPushMessage(Context context, PushMessage msg)
    {
        switch (msg.cmd) {
            case PushMessageCommand.KICK_OUT:
                notify(context, id++, "用户登录状态变更", msg.msg);
                if (Application.getInstance().isLogin() && MainActivity.Instance() != null) {
                    MainActivity.Instance().logout(false);
                } else {
                    Application.getInstance().setLoginResult(null);
                }
                break;

            case PushMessageCommand.NEWORDER_TO_WORKER:
                notifyServiceOrder(context, "机修工 - 新订单可抢", msg);
                break;
            case PushMessageCommand.ORDER_WORKER_START_TO_USER:
                notifyServiceOrder(context, "机修工 - 服务订单开工", msg);
                break;
            case PushMessageCommand.ORDER_CANCELED_TO_WORKER:
                notifyServiceOrder(context, "机修工 - 服务订单取消", msg);
                break;
            case PushMessageCommand.ORDER_OFFLINEPAY_TO_WORKER:
                notifyServiceOrder(context, "机修工 - 服务订单线下支付", msg);
                break;
            case PushMessageCommand.ORDER_OFFLINEPAY_TO_USER:
                notifyServiceOrder(context, "机修工 - 线下付款已经收到", msg);
                break;
            case PushMessageCommand.ORDER_CATCHED_TO_USER:
                notifyServiceOrder(context, "机修工 - 服务订单抢单提示", msg);
                break;
            case PushMessageCommand.ORDER_WORKER_ADD_TIPFEE:
                notifyServiceOrder(context, "用户 - 机修工追单", msg);
                break;
            case PushMessageCommand.ORDER_PAYED_TO_WORKER:
                notifyServiceOrder(context, "机修工 - 订单已经付款", msg);
                break;

            default: {
                notify(context, "Unknown Command", msg.cmd);
                break;
            }
        }

        ToastUtils.show(context, msg.cmd + " => " + msg.msg);
    }

    private int getOid(PushMessage msg) {
        try {
            return Integer.parseInt(msg.oid);
        } catch (Exception ex) {
            return 0;
        }
    }

    private PendingIntent getPendingIntent(Context context, int oid) {

        return oid > 0
                ? PendingIntent.getActivity(context, 0,
                    Application.getInstance().isWorkerPackage()
                            ? WorkerServiceOrderDetailActivity.getServiceStartIntent(context, oid)
                            : ServiceOrderDetailActivity.getServiceStartIntent(context, oid)
                , PendingIntent.FLAG_UPDATE_CURRENT)
                : null;
    }


    private void notifyServiceOrder(Context context, String title, PushMessage message) {
        EventBus.getDefault().post(message);

        int oid = getOid(message);

        if (oid > 0 && oid != Application.getInstance().getCurrentViewOrderId()) {
            notify(context, id, title, message.msg, getPendingIntent(context, oid));
        } else {
            notify(context, id, title, message.msg);
        }
    }


    private void notify(Context context, String title, String msg) {
        notify(context, id++, title, msg, null);
    }

    private void notify(Context context, String title, String msg, PendingIntent pendingIntent) {
        notify(context, id++, title, msg, pendingIntent);
    }

    private void notify(Context context, int id, String title, String msg) {
        notify(context, id, title, msg, null);
    }

    private void notify(Context context, int id, String title, String msg, PendingIntent pendingIntent) {

//        if (isNotificationEnabled()) {
//            if (isNotificationToastEnabled()) {
//                ToastUtils.show(context, notificationIQ.getMessage());
//            }

        Notification notification = new Notification();
        notification.icon = R.mipmap.ic_launcher;
        notification.defaults = Notification.DEFAULT_LIGHTS;
//        if (isNotificationSoundEnabled()) {
            notification.defaults |= Notification.DEFAULT_SOUND;
//        }
//        if (isNotificationVibrateEnabled()) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
//        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.when = System.currentTimeMillis();
        notification.tickerText =  msg;



        notification.setLatestEventInfo(context, title, msg, pendingIntent);


        //notificationIQ.setIndex(random.nextInt());
        NotificationManager notificationManager = (NotificationManager) context.getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }
}
