package org.androidpn.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author snox@live.com
 * @date 2015/10/30.
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    private static final String LOGTAG = "ConnectivityReceiver";

    private NotificationService service;

    public ConnectivityReceiver(NotificationService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {

            if (networkInfo.isConnected()) {
                service.connect();
            }
        } else {
            service.disconnect();
        }
    }
}
