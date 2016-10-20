package org.androidpn.client;

import android.util.Log;

/**
 * @author snox@live.com
 * @date 2015/10/30.
 */
public class ReconnectionThread extends Thread {

    private static final String LOGTAG = ReconnectionThread.class.getSimpleName();

    private final XmppManager xmppManager;
    private int waiting;

    public ReconnectionThread(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
        waiting = 0;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Log.d(LOGTAG, waiting() + "秒后重连服务器");

                Thread.sleep((long) waiting() * 1000L);
                xmppManager.connect();
                waiting++;
            }
        } catch (final InterruptedException e) {
            xmppManager.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    xmppManager.getConnectionListener().reconnectionFailed(e);
                }
            });
        }
    }

    private int waiting() {
        if (waiting > 20)
            return 60;
        if (waiting > 13)
            return 30;
        return waiting <= 7 ? 10 : 60;
    }
}
