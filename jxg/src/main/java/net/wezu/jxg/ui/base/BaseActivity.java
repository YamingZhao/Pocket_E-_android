package net.wezu.jxg.ui.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.dialog.FlippingLoadingDialog;
import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.framework.app.AppManager;
import net.wezu.jxg.data.RequestManager;

import butterknife.ButterKnife;

/**
 * @author snox@live.com
 * @date 2015/10/21.
 */
public abstract class BaseActivity extends FragmentActivity {

    protected FlippingLoadingDialog mLoadingDialog;

    protected String requestTag;

    protected void setDefaultBackButton() {
        setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.setEnabled(false);
                v.setVisibility(View.GONE);
                BaseActivity.this.finish();
            }
        });
    }

    public void showOrHideActionBar(boolean isShow) {
        View v = findViewById(R.id.action_bar);
        if (v != null) {
            v.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    protected void hideBackButton() {
        View v = findViewById(R.id.actionbar_back);
        if (v != null) {
            v.setVisibility(View.GONE);
        }
    }

    protected void setBackButton(View.OnClickListener listener) {
        View v = findViewById(R.id.actionbar_back);
        if (v != null) {
            v.setVisibility(View.VISIBLE);
            v.setOnClickListener(listener);
        }
    }

    protected void setRightButton(String text, View.OnClickListener listener) {
        Button button = (Button) findViewById(R.id.actionbar_second_button);
        if (button != null) {
            button.setText(text);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(listener);
        }
    }

    protected void hideRightButton() {
        Button button = (Button) findViewById(R.id.actionbar_second_button);
        if (button != null) {
            button.setVisibility(View.GONE);
        }
    }

    protected void setTitle(String title) {
        TextView v = (TextView) findViewById(R.id.actionbar_title);
        if (v != null) {
            v.setText(title);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        requestTag = getClass().getName();

        AppManager.getAppManager().addActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public void finish() {
        //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        super.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        RequestManager.getInstance().cancelAll(requestTag);
    }

    public FlippingLoadingDialog getLoadingDialog(String message) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new FlippingLoadingDialog(this, message);
        } else {
            mLoadingDialog.setTitle(message);
        }
        return mLoadingDialog;
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public void toast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            ToastUtils.show(this, msg);
        }
    }
}
