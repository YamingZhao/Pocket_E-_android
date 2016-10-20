package net.wezu.jxg.ui.user.profile;

import android.os.Bundle;

import net.wezu.jxg.R;
import net.wezu.jxg.ui.user.profile.ChangProfileBaseActivity;
import net.wezu.jxg.util.FastClickUtil;

import butterknife.OnClick;

/**
 * @author snox@live.com
 * @date 2015/10/25.
 */
public class ChangeVatActivity extends ChangProfileBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_vat);

        setTitle("选择增值税");

        setDefaultBackButton();
    }

    @OnClick(R.id.vat_true) void vat1() {
        if (FastClickUtil.isFastClick()) return;

        updateProfile("VAT", "true");
    }
    @OnClick(R.id.vat_false) void vat2() {
        if (FastClickUtil.isFastClick()) return;

        updateProfile("VAT", "false");
    }
}
