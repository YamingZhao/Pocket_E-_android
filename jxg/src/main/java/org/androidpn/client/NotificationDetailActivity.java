package org.androidpn.client;

import android.os.Bundle;
import android.widget.TextView;

import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.ui.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author snox@live.com
 * @date 2015/10/30.
 */
public class NotificationDetailActivity extends BaseActivity {

    @Bind(R.id.title) TextView txtTitle;

    NotificationIQ notificationIQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationIQ = (NotificationIQ) getIntent().getParcelableExtra(Constants.NOTIFICATION);

        setContentView(R.layout.activity_notification);

        txtTitle.setText(notificationIQ.getTitle());
    }

//    @OnClick(R.id.btn_get) void get() {
//        // 抢单
//
//        String message = notificationIQ.getMessage();
//        RequestManager.getInstance().demoQiangdan(message.split("@")[0], message.split(":")[1]);
//
//        finish();
//    }
}
