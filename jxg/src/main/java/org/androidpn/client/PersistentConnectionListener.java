package org.androidpn.client;

import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;

import net.wezu.framework.eventbus.EventBus;

/**
 * @author snox@live.com
 * @date 2015/10/30.
 */
public class PersistentConnectionListener implements ConnectionListener {

    private static final String LOGTAG = "连接侦听";

    private final XmppManager xmppManager;

    public PersistentConnectionListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void connectionClosed() {
        Log.d(LOGTAG, "连接断掉了");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.d(LOGTAG, "连接因为错误断掉了");
        if (xmppManager.getConnection() != null &&
                xmppManager.getConnection().isConnected()) {
            xmppManager.getConnection().disconnect();
        }

        Log.d(LOGTAG, "正在启动重连线程");
        xmppManager.startReconnectionThread();

        EventBus.getDefault().post(new ConnectionEvent(XmppManager.name, false));
    }

    @Override
    public void reconnectingIn(int i) {

    }

    @Override
    public void reconnectionSuccessful() {
    }

    @Override
    public void reconnectionFailed(Exception e) {
        EventBus.getDefault().post(new ConnectionEvent(XmppManager.name, false));
    }


}
