package net.wezu.jxg.ui.service_order.worker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;

import net.wezu.framework.eventbus.Subscribe;
import net.wezu.framework.eventbus.ThreadMode;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.PagedResult;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.data.ServiceOrderStatus;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.receiver.PushMessage;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.ui.service_order.ServiceOrderStatusChangedEvent;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 机修工待抢单列表
 * Created by snox on 2016/3/13.
 */
public class WorkerServiceOrderToCatchListFragment extends WorkerServiceOrderListFragment {

    private static final String TAG = "抢单列表";

    @Bind(R.id.tv_current_address) TextView tvCurrentAddress;

    @Bind(R.id.btn_filter) View btnFilter;
    @Bind(R.id.filter_panel) View filterPanel;
    @Bind(R.id.spinner_distance) Spinner spinnerDistance;
    @Bind(R.id.spinner_type) Spinner spinnerServiceType;

//    @Bind(R.id.map_view) MapView mapView;
//    private BaiduMap baiduMap;

//    private LocationClient locationClient;
    private BDLocation location;

    private static WorkerServiceOrderToCatchListFragment instance;

    public WorkerServiceOrderToCatchListFragment() {
        instance = this;
    }

    @Override
    protected String getServiceStatus() {
        return ServiceOrderStatus.CREATED;
    }

    public static WorkerServiceOrderToCatchListFragment getInstance() {
        return instance;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.fragment_list_order_created);
    }

    private String category = "";
    private String distance = "";



    @Override
    public void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        location = Application.getInstance().getLocation();

        loadIgnoreList();

        filterPanel.setVisibility(View.GONE);

        final ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.filter_service_type, R.layout.simple_spinner_dropdown_item);
        spinnerServiceType.setAdapter(adapter);
        spinnerServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: " + adapter.getItem(position));

                String selectedCate = adapter.getItem(position).toString();

                if (selectedCate.equals("全部")) selectedCate = "";

                if (!selectedCate.equals(category)) {
                    category = selectedCate;
                    refreshData();
                }
                 //adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.filter_distance, R.layout.simple_spinner_dropdown_item);
        spinnerDistance.setAdapter(adapter2);
        spinnerDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: " + adapter2.getItem(position));

                String selectedDsitance = adapter2.getItem(position).toString().replace("公里", "");

                if (selectedDsitance.equals("不限")) selectedDsitance = "";

                if (!selectedDsitance.equals(distance)) {
                    distance = selectedDsitance;
                    refreshData();
                }
                //adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        locationClient = new LocationClient(getApplicationContext());
//        LocationClientOption option = new LocationClientOption();
//
//        option.setOpenGps(true);
//        option.setCoorType("bd09ll");
//        option.setScanSpan(5000);
//
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//        option.setIsNeedAddress(true);
//
//        locationClient.setLocOption(option);
//
//        locationClient.registerLocationListener(new BDLocationListener() {
//            @Override
//            public void onReceiveLocation(BDLocation bdLocation) {
//                updateLocation(bdLocation);
//            }
//        });
//
//        locationClient.start();

//        mapView.setLogoPosition(LogoPosition.logoPostionRightBottom);
//        mapView.showScaleControl(false);
//        mapView.showZoomControls(false);
//
//        baiduMap = mapView.getMap();
//        baiduMap.setMyLocationEnabled(true);

        updateLocation(Application.getInstance().getLocation());
    }

    @Override
    public void onDestroy() {
//        if (locationClient != null) {
//            if (locationClient.isStarted()) {
//                locationClient.stop();
//            }
//            locationClient = null;
//        }
        super.onDestroy();
    }

    @Override
    protected void loadData(int start, int size) {

        setRefreshing(true);

        Map<String, String> params = new HashMap<>();
        if (location != null) {
            params.put("lat", String.valueOf(location.getLatitude()));
            params.put("lng", String.valueOf(location.getLongitude()));
        }

        if (!TextUtils.isEmpty(category)) {
            params.put("servicetype", category);
        }
        if (!TextUtils.isEmpty(distance)) {
            params.put("distance", distance);
        }

        ServiceOrderService.list(requestTag, start, size, ServiceOrderStatus.CREATED, params, new RequestManager.ResponseListener<PagedResult<OrderListItemModel>>() {
            @Override
            public void success(PagedResult<OrderListItemModel> result, String msg) {
                addDataItems(result);
            }

            @Override
            public void error(String msg) {
                toast(msg);
                setRefreshing(false);
            }
        });
    }

    public BDLocation getLocation() {
        return location;
    }

    @Override
    protected void addDataItems(List<OrderListItemModel> items) {
        if (location != null) {
            for (OrderListItemModel item : items) {
                item.wlat = location.getLatitude();
                item.wlng = location.getLongitude();
                item.WorkerLocation = location.getAddrStr();
            }
        }
        super.addDataItems(items);
    }

    private void updateLocation(BDLocation location) {
        if (location == null) return;
        this.location = location;

        tvCurrentAddress.setText(location.getAddrStr());

//        MyLocationData locData = new MyLocationData.Builder()
//                .accuracy(location.getRadius())
//                .direction(100)
//                .latitude(location.getLatitude())
//                .longitude(location.getLongitude())
//                .build();
//        baiduMap.setMyLocationData(locData);
//
//        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//        MapStatus.Builder builder = new MapStatus.Builder();
//        builder.target(ll).zoom(16.0f);
//        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BDLocation location) {
        updateLocation(location);
    }

    @Override
    protected WorkerOrderListItemViewHolder createViewHolder(Context context, View convertView) {
        return new WorkerOrderListItemViewHolder(context, convertView) {
            @Override
            public void setData(final OrderListItemModel data) {
                super.setData(data);

                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (FastClickUtil.isFastClick()) return;

                        onOrderItemClicked(data);
                    }
                });

                mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (FastClickUtil.isFastClick()) return false;

                        return onOrderItemLongClicked(data);
                    }
                });
            }

            @Override
            protected void ignore(final OrderListItemModel order) {
                if (FastClickUtil.isFastClick()) return;

                new MaterialDialog(getActivity())
                        .setTitle("忽略确认")
                        .setMessage("您是否需要忽略本条服务订单？")
                        .setPositiveButton("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ignoreList.add(order.OrderId);

                                saveIgnoreList();

                                removeItem(order);
                            }
                        })
                        .setNegativeButton("取消")
                        .show();
            }
        };
    }

    @OnClick(R.id.btn_filter) void showHideFilter() {
        if (FastClickUtil.isFastClick()) return;

        if (filterPanel.getVisibility() == View.GONE) {
            filterPanel.setVisibility(View.VISIBLE);
        } else {
            filterPanel.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushMessage msg) {
        refreshData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServiceOrderStatusChangedEvent msg) {
        refreshData();
    }

    @Override
    protected boolean containsItem(OrderListItemModel item) {
        return ignoreList != null && ignoreList.contains(item.OrderId) || super.containsItem(item);
    }

    private IgnoreList ignoreList;

    private void loadIgnoreList() {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("worker_ignore_list", Context.MODE_PRIVATE);

        String data = sp.getString("ignore_list", "{}");

        ignoreList = new Gson().fromJson(data, IgnoreList.class);
    }

    private void saveIgnoreList() {
        if (ignoreList == null) return;

        String data = new Gson().toJson(ignoreList);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("worker_ignore_list", Context.MODE_PRIVATE);
        sp.edit().putString("ignore_list", data).commit();
    }

    class IgnoreList {
        public List<Integer> orderids;

        public boolean contains(Integer id) {
            return orderids != null && orderids.contains(id);
        }

        public void add(Integer id) {
            if (orderids == null)
                orderids = new ArrayList<>();

            orderids.add(id);
        }
    }
}
