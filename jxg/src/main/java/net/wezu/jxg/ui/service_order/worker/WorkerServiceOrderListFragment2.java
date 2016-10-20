package net.wezu.jxg.ui.service_order.worker;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import net.wezu.framework.eventbus.EventBus;
import net.wezu.framework.eventbus.Subscribe;
import net.wezu.framework.eventbus.ThreadMode;
import net.wezu.jxg.R;
import net.wezu.jxg.data.PagedResult;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.receiver.PushMessage;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.ui.base.BaseListFragment;
import net.wezu.jxg.ui.service_order.ServiceOrderStatusChangedEvent;
import net.wezu.jxg.ui.service_order.user.ServiceOrderListFragment;
import net.wezu.jxg.util.FastClickUtil;

/**
 * 机修工服务订单列表，新
 * Created by snox on 2016/4/16.
 */
public class WorkerServiceOrderListFragment2 extends BaseListFragment<OrderListItemModel, WorkerOrderListItemViewHolder> {

    public static WorkerServiceOrderListFragment2 fromStatus(String status) {
        WorkerServiceOrderListFragment2 fragment = new WorkerServiceOrderListFragment2();
        Bundle bundle = new Bundle();
        bundle.putString("service_status", status);
        fragment.setArguments(bundle);
        return fragment;
    }

    public WorkerServiceOrderListFragment2() {
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        //if (!EventBus.getDefault().isRegistered(this)) {
        EventBus.getDefault().register(this);
        //}
    }

    protected String getServiceStatus() {
        return getArguments().getString("service_status");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                toast(msg);
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
        return R.layout.listitem_order_worker2;
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

        WorkerServiceOrderDetailActivity.showOrder(getActivity(), data);
    }

    protected boolean onOrderItemLongClicked(OrderListItemModel data) {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServiceOrderStatusChangedEvent event) {
        refreshData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushMessage msg) {
        refreshData();
    }
}