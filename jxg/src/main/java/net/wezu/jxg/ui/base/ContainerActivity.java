package net.wezu.jxg.ui.base;

import android.os.Bundle;

import net.wezu.jxg.R;

/**
 * @author snox@live.com
 * @date 2015/10/24.
 */
public class ContainerActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container);
    }
}
