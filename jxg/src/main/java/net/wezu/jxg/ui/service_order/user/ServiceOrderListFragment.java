package net.wezu.jxg.ui.service_order.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import net.wezu.framework.eventbus.Subscribe;
import net.wezu.framework.eventbus.ThreadMode;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.data.PagedResult;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.ui.base.BaseListFragment;

import net.wezu.framework.eventbus.EventBus;
import net.wezu.jxg.ui.service_order.OrderListItemViewHolder;
import net.wezu.jxg.ui.service_order.ServiceOrderDetailActivity;
import net.wezu.jxg.ui.service_order.ServiceOrderStatusChangedEvent;
import net.wezu.jxg.util.FastClickUtil;

/**
 * 服务订单列表
 *
 * Created by snox on 2015/11/17.
 */
public class ServiceOrderListFragment extends BaseListFragment<OrderListItemModel, OrderListItemViewHolder> {

    public ServiceOrderListFragment() {
    }

    public static ServiceOrderListFragment fromState(String state) {
        ServiceOrderListFragment fragment = new ServiceOrderListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("service_status", state);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected String getServiceStatus() {
        return getArguments().getString("service_status");
    }

    @Override
    protected void loadData(int start, int size) {
        setRefreshing(true);

        ServiceOrderService.list(requestTag, start, size, getServiceStatus(), new RequestManager.ResponseListener<PagedResult<OrderListItemModel>>() {
            @Override
            public void success(PagedResult<OrderListItemModel> result, String msg) {
                addDataItems(result);
            }

            @Override
            public void error(String msg) {
                setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected boolean equalItem(OrderListItemModel item1, OrderListItemModel item2) {
        return item1.OrderId == item2.OrderId;
    }

    @Override
    protected int getListItemLayoutResourceId() {
//        if (Application.getInstance().isWorkerPackage()) {
//            return R.layout.listitem_order_worker_detail;
//        } else {
            return R.layout.listitem_order;
//        }
    }

    @Override
    protected OrderListItemViewHolder createViewHolder(Context context, View convertView) {
        return new OrderListItemViewHolder(context, convertView) {
            @Override
            public void setData(final OrderListItemModel data) {
                super.setData(data);

                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onOrderItemClicked(data);
                    }
                });

                mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return onOrderItemLongClicked(data);
                    }
                });
            }
        };
    }

    protected void onOrderItemClicked(OrderListItemModel data) {
        if (FastClickUtil.isFastClick()) return;

        ServiceOrderDetailActivity.showOrder(getActivity(), data.OrderId);
    }

    protected boolean onOrderItemLongClicked(OrderListItemModel data) {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServiceOrderStatusChangedEvent event) {
        refreshData();
    }
}
