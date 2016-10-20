package net.wezu.jxg.ui.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.Coupon;
import net.wezu.jxg.service.UserService;
import net.wezu.jxg.ui.base.BaseListActivity;

import java.util.List;

/**
 * 优惠券列表
 *
 * Created by snox on 2016/1/16.
 */
public class MyCouponListActivity extends BaseListActivity<Coupon, CouponViewHolder> {

    private static String TAG_SELECTOR = "SELECTOR";

    public static void startActivity(Activity activity) {
        activity.startActivity(new Intent(activity, MyCouponListActivity.class));
    }

    public static void startActivityForResult(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, MyCouponListActivity.class).putExtra(TAG_SELECTOR, true), requestCode);
    }

    private boolean isSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isSelector = getIntent().getBooleanExtra(TAG_SELECTOR, false);

        if (isSelector) {
            setTitle("选择优惠券");
        } else {
            setTitle("我的优惠券");
        }
        setDefaultBackButton();
        setDividerHeight(1);
    }

    @Override
    protected void refreshData() {

        String condition = isSelector ? "false" : "all";

        UserService.getProductCoupons(requestTag,  condition, condition, new RequestManager.ResponseListener<List<Coupon>>() {
            @Override
            public void success(List<Coupon> result, String msg) {
                clear();
                addDataItems(result);

                setRefreshing(false);
            }

            @Override
            public void error(String msg) {
                setRefreshing(false);
                toast(msg);
            }
        });
    }

    @Override
    protected boolean equalItem(Coupon item1, Coupon item2) {
        return false;
    }

    @Override
    protected int getListItemLayoutResourceId() {
        return R.layout.listitem_coupon;
    }

    @Override
    protected CouponViewHolder createViewHolder(Context context, View convertView) {
        return new CouponViewHolder(context, convertView);
    }
}
