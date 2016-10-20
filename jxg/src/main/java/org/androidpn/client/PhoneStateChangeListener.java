package org.androidpn.client;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 电话状态改变侦听
 * 
 * @author snox@live.com
 * @date 2015/10/30.
 */
public class PhoneStateChangeListener extends PhoneStateListener {
    private static final String  LOGTAG = "电话状态侦听";
    
    private final NotificationService service;
    
    public PhoneStateChangeListener(NotificationService service) {
        this.service = service;
    }

    @Override
    public void onDataConnectionStateChanged(int state) {
        super.onDataConnectionStateChanged(state);

        Log.d(LOGTAG, "数据连接状态改变");
        Log.d(LOGTAG, "当前状态 " + getState(state));
        
        if (state == TelephonyManager.DATA_CONNECTED) {
            service.connect();
        }
    }

    private String getState(int state) {
        switch (state) {
            case 0: // '\0'
                return "DATA_DISCONNECTED";
            case 1: // '\001'
                return "DATA_CONNECTING";
            case 2: // '\002'
                return "DATA_CONNECTED";
            case 3: // '\003'
                return "DATA_SUSPENDED";
        }
        return "DATA_<UNKNOWN>";
    }
}
