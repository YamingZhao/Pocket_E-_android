package net.wezu.jxg.map;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snox on 2016/4/25.
 */
public class PoiOverlay extends OverlayManager {

    private static final int MAX_POI_SIZE = 10;

    private PoiResult mPoiResult = null;

    public PoiOverlay(BaiduMap map) {
        super(map);
    }

    public void setData(PoiResult poiResult) {
        mPoiResult = poiResult;
    }

    public PoiResult getPoiResult() {
        return mPoiResult;
    }

    @Override
    public List<OverlayOptions> getOverlayOptions() {
        if (mPoiResult == null || mPoiResult.getAllPoi() == null) {
            return null;
        }

        List<OverlayOptions> markerList = new ArrayList<>();
        int markerSize = 0;

        for (int i = 0; i < mPoiResult.getAllPoi().size() && markerSize < MAX_POI_SIZE; i++) {
            LatLng location = mPoiResult.getAllPoi().get(i).location;
            if (location == null) {
                continue;
            }

            markerSize++;

            Bundle bundle = new Bundle();
            bundle.putInt("index", i);

            markerList.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_mark" + markerSize + ".png"))
                    .extraInfo(bundle)
                    .position(location));
        }
        return markerList;
    }

    public boolean onPoiClick(PoiInfo info) {
        return false;
    }

    @Override
    public final boolean onMarkerClick(Marker marker) {
        if (!mOverlayList.contains(marker)) {
            return false;
        }

        if (marker.getExtraInfo() != null) {
            int index = marker.getExtraInfo().getInt("index");
            return onPoiClick(mPoiResult.getAllPoi().get(index));
        }
        return false;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        return false;
    }
}
