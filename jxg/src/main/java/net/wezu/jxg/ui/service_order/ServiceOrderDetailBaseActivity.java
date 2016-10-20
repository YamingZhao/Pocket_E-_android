package net.wezu.jxg.ui.service_order;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.OrderEntity;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.model.OrderLog;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.ui.base.BaseActivity;

import java.math.BigDecimal;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.Bind;

/**
 * 服务订单基类
 * Created by snox on 2016/3/27.
 */
public abstract class ServiceOrderDetailBaseActivity extends BaseActivity {

    protected final static String ORDER_ID = "order_id";
    protected final static String ORDER_ENTITY = "order_entity";

    @Bind(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;

    protected int orderId;
    protected OrderEntity orderEntity;

    protected OrderListItemModel orderListItemModel;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        setTitle("订单详情");
        setDefaultBackButton();

        Intent intent = getIntent();

        orderId = intent.getIntExtra(ORDER_ID, -1);
        if (orderId == -1) {
            orderListItemModel = intent.getParcelableExtra(ORDER_ENTITY);
            if (orderListItemModel == null) {
                //showError("非法访问");
                return;
            } else {
                orderId = orderListItemModel.OrderId;
            }
        }

//        orderEntity = intent.getParcelableExtra(ORDER_ENTITY);
//
//        if (orderEntity != null) {
//            orderId = orderEntity.order.OrderId;
//            setData(orderEntity);
//        } else {
//            loadOrder(orderId);
//        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadOrder();
            }
        });

        loadOrder(orderId);
    }

    protected final void reloadOrder() {
        if (orderId != -1) {
            loadOrder(orderId);
        }
    }

    // 加载订单
    protected void loadOrder(int orderId) {
        refreshLayout.setRefreshing(true);
        getLoadingDialog("正在加载订单详情").show();

        ServiceOrderService.get(requestTag, orderId, new RequestManager.ResponseListener<OrderEntity>() {
            @Override
            public void success(OrderEntity result, String msg) {
                refreshLayout.setRefreshing(false);
                orderEntity = result;
                setData(result);
                dismissLoadingDialog();
            }

            @Override
            public void error(String msg) {
                refreshLayout.setRefreshing(false);
                //showError(msg);
                dismissLoadingDialog();
            }
        });
    }

    protected abstract void setData(OrderEntity order);

    protected void loadOrderLogs(final LinearLayout container, int orderId) {
        container.removeAllViews();
        ServiceOrderService.getLogs(requestTag, orderId, new RequestManager.ResponseListener<List<OrderLog>>() {
            @Override
            public void success(List<OrderLog> result, String msg) {
                if (result != null) {
                    for (OrderLog log : result) {
                        addLog(container, log);
                    }
                }
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });
    }

    private void addLog(LinearLayout container, OrderLog log) {

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.listitem_order_log, null);

        ((TextView)v.findViewById(R.id.txt_log_name)).setText(log.Message);
        ((TextView)v.findViewById(R.id.txt_log_date)).setText(log.CreatedOn);

        container.addView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    protected String formatDistance(double distance) {
        if (distance / 1000 > 0) {
            return String.valueOf(new BigDecimal(distance / 1000).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()) + " 千米";
        } else {
            return String.valueOf(new BigDecimal(distance).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()) + " 米";
        }
    }

    protected void call(String phoneNumber) {
        Intent intent = new Intent("android.intent.action.CALL", Uri
                .parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}
