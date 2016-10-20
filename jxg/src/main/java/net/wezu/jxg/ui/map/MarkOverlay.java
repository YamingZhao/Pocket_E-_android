package net.wezu.jxg.ui.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;

/**
 * Created by snox on 2015/11/23.
 */
public class MarkOverlay implements OnMarkerClickListener, OnMarkerDragListener, OnGetGeoCoderResultListener {

    private Context context;
    private BaiduMap baiduMap;
    private Marker marker;

    private GeoCoder geoCoder;

    private InfoWindow infoWindow;

    private boolean unknown = true;

    public MarkOverlay(Context context, BaiduMap baiduMap) {
        this.context = context;
        this.baiduMap = baiduMap;
        if (baiduMap != null) {
            baiduMap.setOnMarkerClickListener(this);
            baiduMap.setOnMarkerDragListener(this);
        }
    }

    public void markOnMap(LatLng latLng) {
        if (baiduMap != null) {
            removeFromMap();

            BitmapDescriptor flag = BitmapDescriptorFactory.fromResource(R.mipmap.ic_flag);
            marker = (Marker) baiduMap.addOverlay((new MarkerOptions()).icon(flag).position(latLng));
            marker.setDraggable(true);
            unknown = true;
            //onMarkOverlayClick(marker);
        }
    }

    public void removeFromMap() {
        if (baiduMap != null && marker != null) {
            marker.remove();
        }
    }

    private void onMarkOverlayClick(final Marker marker) {
        marker.setToTop();

        if (unknown) {
            geoCoder = GeoCoder.newInstance();
            geoCoder.setOnGetGeoCodeResultListener(this);
            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(marker.getPosition()));

            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_loading, null);
            infoWindow = new InfoWindow(linearLayout, marker.getPosition(), -70);
        }

        baiduMap.showInfoWindow(infoWindow);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null) {
            toa("查询失败");
            baiduMap.hideInfoWindow();
        } else if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            toa("错误码：" + result.error);
        } else {
            unknown = false;
            geoCoder.destroy();

            baiduMap.hideInfoWindow();

            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_infowin, null);
            ((TextView) linearLayout.findViewById(R.id.tv_infowin_title)).setText(result.getAddress());

            ReverseGeoCodeResult.AddressComponent ac = result.getAddressDetail();
            ((TextView) linearLayout.findViewById(R.id.tv_infowin_desc)).setText(ac.city + ac.district + ac.street);

//            linearLayout.findViewById(R.id.tv_infowin_retrieve).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    retrieve(marker.getPosition());
//                    baiduMap.hideInfoWindow();
//                }
//            });
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (this.marker.equals(marker)) {
            onMarkerClick(marker);
        }
        return false;
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        baiduMap.hideInfoWindow();
    }

    private void toa(String msg) {
        ToastUtils.show(context, msg);
    }
}
