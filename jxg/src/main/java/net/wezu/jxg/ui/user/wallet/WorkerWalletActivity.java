package net.wezu.jxg.ui.user.wallet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.wezu.framework.eventbus.EventBus;
import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.base.BaseFragment;
import net.wezu.jxg.ui.base.BaseViewPagerActivity;
import net.wezu.jxg.ui.base.FragmentPagerAdapter;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.viewpagerindicator.view.indicator.Indicator;
import net.wezu.viewpagerindicator.view.indicator.IndicatorViewPager;
import net.wezu.viewpagerindicator.view.indicator.slidebar.ColorBar;
import net.wezu.viewpagerindicator.view.indicator.slidebar.ScrollBar;
import net.wezu.viewpagerindicator.view.indicator.transition.OnTransitionTextListener;
import net.wezu.widget.MaterialDialog;

import butterknife.Bind;

/**
 * 我的钱包
 * Created by snox on 2016/3/30.
 */
public class WorkerWalletActivity extends BaseViewPagerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDefaultBackButton();
        setTitle("我的钱包");
        setRightButton("一键提现", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) return;
                withdrawAll();
            }
        });
    }

    @Override
    protected FragmentPagerAdapter setAdapter(FragmentPagerAdapter adapter) {
        adapter.addFragment("待提现", getFragment("NONE"));
        adapter.addFragment("提现中", getFragment("SUBMITTED"));
        adapter.addFragment("已完成", getFragment("CLEARED"));
        adapter.addFragment("申请失败", getFragment("REJECTED"));
        return adapter;
    }

    private BaseFragment getFragment(String clearedStatus) {
        WorkerWalletListFragment fragment = new WorkerWalletListFragment();

        Bundle bundle = new Bundle();
        bundle.putString("CLEAR_STATE", clearedStatus);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void withdrawAll() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog
                .setTitle("一键提现")
                .setMessage("只有15天前完成在线支付的订单才能提现")
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        withdraw();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    private void withdraw() {
        if (FastClickUtil.isFastClick()) return;

        getLoadingDialog("正在提交提现申请").show();
        ServiceOrderService.requestWithdrawAll(requestTag, new RequestManager.ResponseListener<Object>() {
            @Override
            public void success(Object result, String msg) {
                dismissLoadingDialog();
                EventBus.getDefault().post(new WalletRefreshMessage());
            }

            @Override
            public void error(String msg) {
                toast(msg);
                dismissLoadingDialog();
            }
        });
    }
}
