package org.androidpn.client;

import android.content.Intent;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

/**
 * @author snox@live.com
 * @date 2015/10/30.
 */
public class NotificationPacketListener implements PacketListener {

    private static final String LOGTAG = "NotificationPacketListener";

    private final XmppManager xmppManager;

    public NotificationPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void processPacket(Packet packet) {

        if (packet instanceof NotificationIQ) {
            NotificationIQ notification = (NotificationIQ) packet;

            if (notification.getChildElementXML().contains("androidpn:iq:notification")) {

                //notification.getFrom();

                Intent intent = new Intent(Constants.ACTION_SHOW_NOTIFICATION);
                intent.putExtra(Constants.NOTIFICATION, notification);
                //intent.putExtra(Constants.NOTIFICATION, notification.getFrom());

                xmppManager.getContext().sendBroadcast(intent);
            }
        }
    }
}
