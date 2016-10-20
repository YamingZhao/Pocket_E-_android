package net.wezu.jxg.ui.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;

import net.wezu.jxg.R;
import net.wezu.jxg.ui.base.BaseListActivity;
import net.wezu.jxg.ui.base.BaseListAdapter;
import net.wezu.jxg.ui.map.MarkOverlay;
import net.wezu.jxg.ui.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;
import com.baidu.mapapi.search.core.PoiInfo;

/**
 * 百度地图
 *
 * Created by snox on 2015/11/15.
 */
public class BaiduMapActivity extends BaseListActivity<PoiInfo, PoiInfoViewHolder> {

    private MarkOverlay mMarkOverlay;

    @Bind(R.id.map_view)
    MapView mapView;

    @Bind(R.id.txt_address)
    TextView txtAddress;

    @Bind(R.id.btn_select)
    Button btnSelect;

    private LatLng position;
    private ReverseGeoCodeResult reverseGeoCodeResult;
    private AddressInformation addressInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("选择位置");
        setDefaultBackButton();

        btnSelect.setEnabled(false);

        BaiduMap map = mapView.getMap();

        mMarkOverlay = new MarkOverlay(this, map);

        mapView.showZoomControls(true);
        mapView.showScaleControl(true);

        Intent intent = getIntent();
        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {

            LatLng latLng = new LatLng(getIntent().getDoubleExtra("latitude", 0), getIntent().getDoubleExtra("longitude", 0));
            updateLatLng(latLng);

            centerLatlng(map, latLng);
            MapStatus.Builder builder = new MapStatus.Builder().overlook(-30);
            builder.zoom(map.getMaxZoomLevel());
            builder.target(latLng);

            MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(builder.build());
            map.animateMapStatus(u, 1000);

            mMarkOverlay.markOnMap(latLng);
        }

        map.setMyLocationEnabled(true);
        map.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                btnSelect.setEnabled(false);
                addressInformation = null;

                updateLatLng(mapStatus.target);
                mMarkOverlay.markOnMap(mapStatus.target);
            }
        });

        setRefreshLayoutEnabled(false);
        setDividerHeight(1);

        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiInfo poiInfo = getItem(position);
                addressInformation = AddressInformation.fromReverseGeoCodeResult(BaiduMapActivity.this.position, reverseGeoCodeResult, poiInfo);

                LatLng latLng = new LatLng(addressInformation.latitude, addressInformation.longitude);

                mMarkOverlay.markOnMap(latLng);

                //centerLatlng(mapView.getMap(), latLng);

                txtAddress.setText(addressInformation.name);
                btnSelect.setEnabled(true);
            }
        });
    }

    private void centerLatlng(BaiduMap map, LatLng latLng) {
        MapStatus.Builder builder = new MapStatus.Builder().overlook(-30);
        builder.zoom(map.getMaxZoomLevel());
        builder.target(latLng);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_baidu_map);
    }

    @Override
    protected void refreshData() {

    }

    @Override
    protected boolean equalItem(PoiInfo item1, PoiInfo item2) {
        return false;
    }

    @Override
    protected int getListItemLayoutResourceId() {
        return R.layout.listitem_poi_info;
    }

    @Override
    protected PoiInfoViewHolder createViewHolder(Context context, View convertView) {
        return new PoiInfoViewHolder(context, convertView);
    }

    private void updateLatLng(LatLng position) {
        this.position = position;

        final GeoCoder geoCoder = GeoCoder.newInstance();

        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

                reverseGeoCodeResult = result;
                txtAddress.setText(result.getAddress());

                clear();
                addDataItems(result.getPoiList());

                geoCoder.destroy();
            }
        });
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(position));
    }

    @OnClick(R.id.btn_select) void select() {

        if (addressInformation == null) {
            toast("请选择");
            return;
        }

        setResult(RESULT_OK, new Intent().putExtra("address", addressInformation));
        finish();
    }

    @Override
    protected void onDestroy() {
        mapView.getMap().setMyLocationEnabled(false);

        super.onDestroy();
    }
}
