package net.wezu.jxg.ui.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import net.wezu.jxg.R;
import net.wezu.jxg.map.PoiOverlay;
import net.wezu.jxg.ui.base.BaseActivity;

import butterknife.Bind;

/**
 * Created by snox on 2016/1/17.
 */
public class BaiduMapPoiSearchActivity extends BaseActivity {

    public final static String ADDRESS_FIELD = "address";

    public static void startActivityForResult(Activity activity, AddressInformation address, int requestCode) {
        activity.startActivityForResult(new Intent(activity, BaiduMapPoiSearchActivity.class).putExtra(ADDRESS_FIELD, address), requestCode);
    }

    @Bind(R.id.map_view) MapView mapView;
    @Bind(R.id.et_keyword) AutoCompleteTextView etKeyword;
    @Bind(R.id.tv_message) TextView tvMessaeg;

    private BaiduMap mBaiduMap;
    private PoiSearch mPoiSearch = null;
    //private MapStatusUpdate msu;
    private SuggestionSearch mSuggestionSearch;

    private ArrayAdapter<String> suggestAdapter;

    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_baidu_map_poi_search);

        AddressInformation address = getIntent().getParcelableExtra(ADDRESS_FIELD);

        city = address.city;

        mBaiduMap = mapView.getMap();

        /**
         * 坐标
         */
        LatLng point = new LatLng(address.latitude, address.longitude);
        //double mapX = getIntent().getDoubleExtra("mapX", 0);
        //double mapY = getIntent().getDoubleExtra("mapY", 0);
        //point = new LatLng(mapY, mapX);

        //msu = MapStatusUpdateFactory.zoomIn();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(point, (float) 15));

        //show(point);

        mPoiSearch = PoiSearch.newInstance();

        //search();

        suggestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        etKeyword.setAdapter(suggestAdapter);

        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String addressKeyword = s.toString();

                tvMessaeg.setText(addressKeyword);

                if (!TextUtils.isEmpty(addressKeyword)) {
                    mSuggestionSearch.requestSuggestion(new SuggestionSearchOption().keyword(addressKeyword).city(city));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(
                                    etKeyword.getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                    search();

                    return true;
                }
                return false;
            }
        });

        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {

                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }

                suggestAdapter.clear();
                tvMessaeg.setText(etKeyword.getText().toString() + suggestionResult.getAllSuggestions().size() + "条");

                for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
                    if (info.key != null)
                        suggestAdapter.add(info.key);
                }
                suggestAdapter.notifyDataSetChanged();

                etKeyword.showDropDown();
            }
        });

        OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(final PoiResult poiResult) {
                if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    return;
                }

                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    mBaiduMap.clear();

                    PoiOverlay overlay = new PoiOverlay(mBaiduMap) {
                        @Override
                        public boolean onPoiClick(final PoiInfo poi) {
//                            super.onPoiClick(i);
//
//                            final PoiInfo poi = getPoiResult().getAllPoi().get(i);

                            final GeoCoder geoCoder = GeoCoder.newInstance();

                            geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                                @Override
                                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                                }

                                @Override
                                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

                                    final AddressInformation addressInfo = AddressInformation.fromReverseGeoCodeResult(result, poi);

                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                                    View contentView = inflater.inflate(R.layout.component_address, null, false);

                                    ((TextView) contentView.findViewById(R.id.txt_province)).setText(addressInfo.province);
                                    ((TextView) contentView.findViewById(R.id.tv_city)).setText(addressInfo.city);
                                    ((TextView) contentView.findViewById(R.id.txt_district)).setText(addressInfo.district);
                                    ((TextView) contentView.findViewById(R.id.txt_poi)).setText(addressInfo.name);
                                    ((TextView) contentView.findViewById(R.id.txt_street)).setText(addressInfo.street);

                                    contentView.findViewById(R.id.btn_select_address).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            setResult(RESULT_OK, getIntent().putExtra(ADDRESS_FIELD, addressInfo));
                                            finish();
                                        }
                                    });

                                    LatLng ll = poi.location;
                                    // 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
                                    InfoWindow mInfoWindow = new InfoWindow(contentView, ll, -47);
                                    // 显示InfoWindow
                                    mBaiduMap.showInfoWindow(mInfoWindow);

                                    geoCoder.destroy();
                                }
                            });
                            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(poi.location));

                            return true;
                        }
                    };


                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(poiResult);
                    overlay.addToMap();
                    overlay.zoomToSpan();;
                }
            }

            @Override
            public void onGetPoiDetailResult(final PoiDetailResult poiDetailResult) {
//                if (poiDetailResult != null && poiDetailResult.error == SearchResult.ERRORNO.NO_ERROR) {
//                    mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//                        @Override
//                        public boolean onMarkerClick(Marker marker) {
//                            // 创建InfoWindow展示的view
//                            TextView text = new TextView(
//                                    getApplicationContext());
//                            text.setBackgroundResource(R.color.white);
//                            text.setText(poiDetailResult.getAddress());
//                            text.setTextSize(12);
//                            text.setPadding(10, 10, 10, 20);
//                            //text.setTextColor(R.color.black);
//                            poiDetailResult.
//                            // 定义用于显示该InfoWindow的坐标点
//                            // LatLng pt = new LatLng(39.86923,
//                            // 116.397428);
//                            LatLng ll = marker.getPosition();
//                            // 创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
//                            InfoWindow mInfoWindow = new InfoWindow(
//                                    text, ll, -47);
//                            // 显示InfoWindow
//                            mBaiduMap.showInfoWindow(mInfoWindow);
//                            return true;
//                        }
//                    });
//                }
            }
        };

        mPoiSearch.setOnGetPoiSearchResultListener(listener);
    }

    private void show(LatLng point) {
        /**
         * 定义Maker坐标点
         */

        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_location);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        /**
         * 绘制图层
         */
        OverlayOptions circleOptions = new CircleOptions().center(point)
                .radius(1500).fillColor(0x99CBCAE0);
        mBaiduMap.addOverlay(circleOptions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        mPoiSearch.destroy();

        super.onDestroy();
    }

    private void search() {
        mPoiSearch.searchInCity(new PoiCitySearchOption().city(city).keyword(etKeyword.getText().toString()).pageNum(0));
    }
}
