package net.wezu.jxg.ui.service_order.worker;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.ui.base.BaseViewPagerFragment;
import net.wezu.jxg.ui.base.FragmentPagerAdapter;
import net.wezu.jxg.util.FastClickUtil;

/**
 * 机修工的服务页面
 *
 * Created by snox on 2015/11/17.
 */
public class WorkerServiceFragment extends BaseViewPagerFragment {

    public WorkerServiceFragment() {
        super();
    }

    @Override
    protected FragmentPagerAdapter setAdapter(FragmentPagerAdapter adapter) {
        adapter.addFragment("抢单", new WorkerServiceOrderToCatchListFragment() {

            @Override
            protected void onOrderItemClicked(final OrderListItemModel data) {
                WorkerServiceOrderDetailActivity.showOrder(getActivity(), data);
            }

//            @Override
//            protected boolean onOrderItemLongClicked(final OrderListItemModel data) {
//
//                // TODO FIXME 这里需要补充机修工的地理位置信息
//                ServiceOrderService.catchOrder(requestTag, data.OrderId, null, "", new RequestManager.ResponseListener<OrderListItemModel>() {
//
//                    @Override
//                    public void success(OrderListItemModel result, String msg) {
//
//                        removeItem(data);
//
//                        ToastUtils.show(getActivity(), "抢单成功");
//                    }
//
//                    @Override
//                    public void error(String msg) {
//                        ToastUtils.show(getActivity(), msg);
//                    }
//                });
//
//                return true;
//            }
        });
        adapter.addFragment("已抢", new WorkerServiceOrderListFragment() { //,WORKDONE,CLOSED") {


            @Override
            protected String getServiceStatus() {
                return "CATCHED,CONFIRMED,SERVICING";
            }

            @Override
            protected void onOrderItemClicked(final OrderListItemModel data) {
                WorkerServiceOrderDetailActivity.showOrder(getActivity(), data);
            }
        });
        return adapter;
    }
}
