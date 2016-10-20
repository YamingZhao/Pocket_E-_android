package net.wezu.jxg.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

/**
 * @author snox@live.com
 * @date 2015/10/23.
 */
public class NotificationController {

    private long firstTime = 0;
    private long secondTime = 0;

    private NotificationManager manager;

    private NotificationController() {

        manager = (NotificationManager) Application.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showNotification(String title, String subtitle, Intent notificationIntent) {

        Notification notification = new Notification();
        secondTime = new Date().getTime();
        if (secondTime - firstTime < 2000) {

        } else {
            notification.vibrate = new long[]{50, 100, 50, 100};
            notification.defaults = Notification.DEFAULT_SOUND;
        }
        firstTime = secondTime;

        //notification.icon =
        notification.when = System.currentTimeMillis();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

//        Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("");
//        notificationIntent.putExtra("bundle", bundle);
//        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        Context context = Application.getInstance().getApplicationContext();

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, title, subtitle, pendingIntent);

        //manager.notify(, notification);
    }
}
