package net.wezu.jxg.ui.service_order;

import android.os.Bundle;

import net.wezu.jxg.app.Application;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.ui.base.BaseFragment;
import net.wezu.jxg.ui.base.BaseViewPagerActivity;
import net.wezu.jxg.ui.base.FragmentPagerAdapter;
import net.wezu.jxg.ui.service_order.user.ServiceOrderListFragment;
import net.wezu.jxg.ui.service_order.worker.WorkerServiceOrderDetailActivity;
import net.wezu.jxg.ui.service_order.worker.WorkerServiceOrderListFragment2;
import net.wezu.jxg.util.FastClickUtil;

/**
 * 服务订单列表
 *
 * Created by snox on 2015/11/16.
 */
public class ServiceOrderActivity extends BaseViewPagerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("服务订单");
        setDefaultBackButton();
    }

    @Override
    protected FragmentPagerAdapter setAdapter(FragmentPagerAdapter adapter) {
        if (!Application.getInstance().isWorkerPackage()) {
            adapter.addFragment("待抢", getFragment("CREATED"));
        }
        adapter.addFragment("进行中", getFragment("CATCHED,CONFIRMED,SERVICING,WORKDONE"));
        //adapter.addFragment("待付款", getFragment("SERVICING,WORKDONE"));
        adapter.addFragment("完工", getFragment("CLOSED"));
//        if (!Application.getInstance().isWorkerPackage()) {
//            adapter.addFragment("回收站", getFragment("CANCELLED,TIMEOUT"));
//        }
        return adapter;
    }

    private BaseFragment getFragment(final String status) {
        if (Application.getInstance().isWorkerPackage()) {
            return WorkerServiceOrderListFragment2.fromStatus(status);
        } else {
            return ServiceOrderListFragment.fromState(status);
        }
    }
}