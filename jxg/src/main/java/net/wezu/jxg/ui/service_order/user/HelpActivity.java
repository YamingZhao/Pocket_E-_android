package net.wezu.jxg.ui.service_order.user;

import android.os.Bundle;

import net.wezu.jxg.R;
import net.wezu.jxg.ui.base.BaseActivity;

/**
 * Created by snox on 2016/4/26.
 */
public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        setTitle("帮助");
        setDefaultBackButton();
    }
}
