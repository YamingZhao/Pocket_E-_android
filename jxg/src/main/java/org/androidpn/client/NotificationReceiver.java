package org.androidpn.client;

import android.content.Context;
import android.content.Intent;

/**
 * @author snox@live.com
 * @date 2015/10/30.
 */
public class NotificationReceiver extends android.content.BroadcastReceiver {

    private static final String LOGTAG = "NotificationReceiver";

    public NotificationReceiver() { }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (Constants.ACTION_SHOW_NOTIFICATION.equals(action)) {
            NotificationIQ notification = intent.getParcelableExtra(Constants.NOTIFICATION);

            Notifier notifier = new Notifier(context);
            notifier.notify(notification);
        }
    }
}
