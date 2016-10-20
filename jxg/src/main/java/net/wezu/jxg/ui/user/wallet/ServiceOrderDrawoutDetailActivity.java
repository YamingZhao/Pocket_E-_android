package net.wezu.jxg.ui.user.wallet;

import android.os.Bundle;
import android.widget.ImageView;

import net.wezu.jxg.R;
import net.wezu.jxg.model.OrderListItemModel;
import net.wezu.jxg.ui.base.BaseActivity;

import butterknife.Bind;

/**
 * 订单申请明细页面
 * Created by snox on 2016/3/30.
 */
public class ServiceOrderDrawoutDetailActivity extends BaseActivity {

    @Bind(R.id.image)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_order_drawout);

        setDefaultBackButton();
        setTitle("我的钱包");

        OrderListItemModel order = getIntent().getParcelableExtra("OrderListItemModel");

        switch (order.ClearedStatus) {
            case "SUBMITTED": imageView.setImageResource(R.mipmap.withdraw_pending_approve); break;
            case "REJECTED":
                imageView.setImageResource(R.mipmap.withdraw_rejuct);

                break;
        }
    }
}
