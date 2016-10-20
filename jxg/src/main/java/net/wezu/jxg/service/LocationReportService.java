package net.wezu.jxg.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import net.wezu.framework.eventbus.EventBus;
import net.wezu.jxg.app.Application;

import java.util.List;

/**
 * Created by snox on 2016/4/24.
 */
public class LocationReportService extends Service {

    private LocationClient locationClient = null;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        //TelephonyManager tm = (TelephonyManager) LocationReportService.this.getSystemService(Context.TELEPHONY_SERVICE);
        if (locationClient != null) {
            if (!locationClient.isStarted())
                locationClient.start();
            return;
        }

        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location != null) {

                    EventBus.getDefault().post(location);

                    Application.getInstance().setLocation(location);
                }
            }
        });

        LocationClientOption option = new LocationClientOption();

        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(300 * LocationClientOption.MIN_SCAN_SPAN); // 5min
        option.disableCache(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        option.setIgnoreKillProcess(false);

        locationClient.setLocOption(option);

        //locationClient.start();
    }

    @Override
    public void onDestroy() {
        if (locationClient != null) {
            if (locationClient.isStarted())
                locationClient.stop();

            locationClient = null;
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationClient.start();
        return super.onStartCommand(intent, flags, startId);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }
}
