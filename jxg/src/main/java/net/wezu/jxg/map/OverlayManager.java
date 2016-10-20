package net.wezu.jxg.map;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnPolylineClickListener;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类提供一个能够显示和管理多个Overlay的基类
 * <p>
 * 复写{@link #getOverlayOptions()} 设置欲显示和管理的Overlay列表
 * </p>
 * <p>
 * 通过
 * {@link com.baidu.mapapi.map.BaiduMap#setOnMarkerClickListener(com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener)}
 * 将覆盖物点击事件传递给OverlayManager后，OverlayManager才能响应点击事件。
 * <p>
 * 复写{@link #onMarkerClick(com.baidu.mapapi.map.Marker)} 处理Marker点击事件
 * </p>
 */
public abstract class OverlayManager implements OnMarkerClickListener, OnPolylineClickListener {

    BaiduMap mBaiduMap = null;
    private List<OverlayOptions> mOverlayOptionList = null;

    List<Overlay> mOverlayList = null;

    public OverlayManager(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;

        if (mOverlayOptionList == null) {
            mOverlayOptionList = new ArrayList<>();
        }

        if (mOverlayList == null) {
            mOverlayList = new ArrayList<>();
        }
    }

    public abstract List<OverlayOptions> getOverlayOptions();

    public final void addToMap() {
        if (mBaiduMap == null) {
            return;
        }

        removeFromMap();
        List<OverlayOptions> overlayOptions = getOverlayOptions();
        if (overlayOptions != null) {
            mOverlayOptionList.addAll(overlayOptions);
        }

        for (OverlayOptions option : mOverlayOptionList) {
            mOverlayList.add(mBaiduMap.addOverlay(option));
        }
    }

    public final void removeFromMap() {
        if (mBaiduMap == null) {
            return;
        }

        for (Overlay marker : mOverlayList) {
            marker.remove();
        }
        mOverlayOptionList.clear();
        mOverlayList.clear();
    }

    public void zoomToSpan() {
        if (mBaiduMap == null) {
            return;
        }

        if (mOverlayList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Overlay overlay : mOverlayList) {
                if (overlay instanceof Marker) {
                    builder.include(((Marker) overlay).getPosition());
                }
            }

            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
        }
    }
}
