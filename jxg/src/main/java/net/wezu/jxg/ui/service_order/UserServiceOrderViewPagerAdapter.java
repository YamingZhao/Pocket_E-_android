package net.wezu.jxg.ui.service_order;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.wezu.jxg.R;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.ui.service_order.user.ServiceOrderListFragment;
import net.wezu.viewpagerindicator.view.indicator.IndicatorViewPager;

/**
 * Created by snox on 2016/3/17.
 */
public class UserServiceOrderViewPagerAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

    private LayoutInflater inflate;

    private String[] tabNames = {"待抢", "进行中", "待付款", "完工", "回收站"};
    //private String[] tabNames = {"可抢", "已接"};

    public UserServiceOrderViewPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);

        inflate = LayoutInflater.from(context.getApplicationContext());
    }

    @Override
    public int getCount() {
        return tabNames.length;
    }

    @Override
    public View getViewForTab(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = inflate.inflate(R.layout.tab_top, container, false);
        }

        TextView textView = (TextView) convertView;
        textView.setText(tabNames[position]);
        return convertView;
    }

    @Override
    public Fragment getFragmentForPage(int position) {

        switch (position) {
            // 待抢
            case 0: return new ServiceOrderListFragment() {

                @Override
                protected String getServiceStatus() {
                    return "CREATED";
                }

                @Override
                protected void onOrderItemClicked(OrderListItemModel data) {
                    super.onOrderItemClicked(data);


                    ServiceOrderDetailActivity.showOrder(getActivity(), data.OrderId);
                }
            };

            // 已抢
            case 1: return new ServiceOrderListFragment() { //,CONFIRMED,SERVICING") {


                @Override
                protected String getServiceStatus() {
                    return "CATCHED,CONFIRMED";
                }

                @Override
                protected void onOrderItemClicked(final OrderListItemModel data) {
                    super.onOrderItemClicked(data);

                    ServiceOrderDetailActivity.showOrder(getActivity(), data.OrderId);
                }
            };

            // 已确认
            case 2: return new ServiceOrderListFragment() { //,CONFIRMED,SERVICING");

                @Override
                protected String getServiceStatus() {
                    return "SERVICING,WORKDONE";
                }

                @Override
                protected void onOrderItemClicked(final OrderListItemModel data) {
                    super.onOrderItemClicked(data);

                    ServiceOrderDetailActivity.showOrder(getActivity(), data.OrderId);
                }
            };

            // 取消
            case 3: return new ServiceOrderListFragment() { //,CONFIRMED,SERVICING");

                @Override
                protected String getServiceStatus() {
                    return "CLOSED";
                }

                @Override
                protected void onOrderItemClicked(final OrderListItemModel data) {
                    super.onOrderItemClicked(data);

                    ServiceOrderDetailActivity.showOrder(getActivity(), data.OrderId);
                }
            };
            // 取消
            case 4: return new ServiceOrderListFragment() { //,CONFIRMED,SERVICING");

                @Override
                protected String getServiceStatus() {
                    return "CANCELLED,TIMEOUT";
                }

                @Override
                protected void onOrderItemClicked(final OrderListItemModel data) {
                    super.onOrderItemClicked(data);

                    ServiceOrderDetailActivity.showOrder(getActivity(), data.OrderId);
                }
            };
        }

        throw new IllegalArgumentException("position");
    }
}
