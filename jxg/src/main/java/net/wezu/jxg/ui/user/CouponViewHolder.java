package net.wezu.jxg.ui.user;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import net.wezu.jxg.R;
import net.wezu.jxg.model.Coupon;
import net.wezu.jxg.ui.base.BaseViewHolder;

import butterknife.Bind;

/**
 * 优惠券项
 * Created by snox on 2016/1/16.
 */
public class CouponViewHolder extends BaseViewHolder<Coupon> {

    @Bind(R.id.tv_name) TextView tvName;
    @Bind(R.id.tv_description) TextView tvDescription;

    public CouponViewHolder(Context context, View view) {
        super(context, view);
    }

    @Override
    public void setData(Coupon data) {
        tvName.setText(data.CouponName);
        tvDescription.setText(data.Description);
    }
}
