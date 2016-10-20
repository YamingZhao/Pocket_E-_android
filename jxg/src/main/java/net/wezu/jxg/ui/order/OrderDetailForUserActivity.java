package net.wezu.jxg.ui.order;

import android.os.Bundle;

import net.wezu.jxg.R;
import net.wezu.jxg.model.OrderEntity;
import net.wezu.jxg.ui.base.BaseActivity;

/**
 * 用户订单页面
 *
 * Created by snox on 2015/11/15.
 */
public class OrderDetailForUserActivity extends BaseActivity {

    public static final String ORDER_DATA = "order_entity";

    private OrderEntity orderEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_for_user);

        orderEntity = getIntent().getParcelableExtra(ORDER_DATA);

        setTitle("订单详情");
        setDefaultBackButton();


    }
}
