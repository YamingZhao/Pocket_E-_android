package org.androidpn.client;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;

import java.util.Random;

import net.wezu.framework.eventbus.EventBus;

/**
 * @author snox@live.com
 * @date 2015/10/30.
 */
public class Notifier {

    private static final String LOGTAG = "Notifier";

    private static final Random random = new Random(System.currentTimeMillis());

    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final NotificationManager notificationManager;

    public Notifier(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(
                Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notify(NotificationIQ notificationIQ) {

        if (isNotificationEnabled()) {
            if (isNotificationToastEnabled()) {
                ToastUtils.show(context, notificationIQ.getMessage());
            }

            Notification notification = new Notification();
            notification.icon = R.mipmap.ic_launcher;
            notification.defaults = Notification.DEFAULT_LIGHTS;
            if (isNotificationSoundEnabled()) {
                notification.defaults |= Notification.DEFAULT_SOUND;
            }
            if (isNotificationVibrateEnabled()) {
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.when = System.currentTimeMillis();
            notification.tickerText = notificationIQ.getMessage();

            Intent intent = new Intent(context, NotificationDetailActivity.class);
            intent.putExtra(Constants.NOTIFICATION, notificationIQ);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            notification.setLatestEventInfo(context, notificationIQ.getTitle(), notificationIQ.getMessage(), contentIntent);


            notificationIQ.setIndex(random.nextInt());
            notificationManager.notify(notificationIQ.getIndex(), notification);

            EventBus.getDefault().post(notificationIQ);
        }
    }

    private int getDefaultIcon() {
        return sharedPreferences.getInt(Constants.NOTIFICATION_ICON, 0);
    }

    private boolean isNotificationEnabled() {
        return sharedPreferences.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED, true);
    }

    private boolean isNotificationSoundEnabled() {
        return sharedPreferences.getBoolean(Constants.SETTINGS_SOUND_ENABLED, true);
    }

    private boolean isNotificationVibrateEnabled() {
        return sharedPreferences.getBoolean(Constants.SETTINGS_VIBRATE_ENABLED, true);
    }

    private boolean isNotificationToastEnabled() {
        return sharedPreferences.getBoolean(Constants.SETTINGS_TOAST_ENABLED, false);
    }
}
