package org.androidpn.client;

/**
 * @author snox@live.com
 * @date 2015/10/30.
 */
public class ConnectionEvent {

    public String name;

    public boolean isConnected;

    public ConnectionEvent(String name, boolean isConnected) {
        this.name = name;
        this.isConnected = isConnected;
    }
}