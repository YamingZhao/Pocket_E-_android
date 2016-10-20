package net.wezu.jxg.ui.map;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.map.DrivingRouteOverlay;
import net.wezu.jxg.ui.base.BaseActivity;

import butterknife.Bind;

/**
 * Created by snox on 2016/4/26.
 */
public class RoutePlanActivity extends BaseActivity {

    @Bind(R.id.map_view) MapView mapView;

    private BaiduMap baiduMap;

    private RoutePlanSearch search = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_routeplan);

        setTitle("驾车路线");
        setDefaultBackButton();

        baiduMap = mapView.getMap();

        search = RoutePlanSearch.newInstance();
        search.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                } else {
                    for (int i = 0; i < result.getRouteLines().size(); i++) {
                        DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap) {

                        };

                        overlay.setData(result.getRouteLines().get(i));
                        overlay.addToMap();
                        overlay.zoomToSpan();

                        baiduMap.setOnMarkerClickListener(overlay);
                    }
                }
            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });

        LatLng from = getIntent().getParcelableExtra("from");
        LatLng to = getIntent().getParcelableExtra("to");

        if (from != null && to != null) {
            search.drivingSearch(new DrivingRoutePlanOption()
                    .from(PlanNode.withLocation(from))
                    .to(PlanNode.withLocation(to)));
        }
    }
}
