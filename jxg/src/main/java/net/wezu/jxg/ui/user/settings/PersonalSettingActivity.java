package net.wezu.jxg.ui.user.settings;

import android.content.Intent;
import android.os.Bundle;

import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.model.UserModel;
import net.wezu.jxg.service.ServiceAddressConstant;
import net.wezu.jxg.ui.MainActivity;
import net.wezu.jxg.ui.WebViewActivity;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.LabeledTextItem;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 个人设置
 *
 * Created by snox on 2016/1/6.
 */
public class PersonalSettingActivity extends BaseActivity {

    @Bind(R.id.lti_account_id)
    LabeledTextItem ltiAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_settings);

        setTitle("设置");
        setDefaultBackButton();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateData();
    }

    private void updateData() {
        UserModel userModel = Application.getInstance().getUserModel();
        if (userModel != null) {
            ltiAccount.setValue(userModel.Username);
        }
    }

    @OnClick(R.id.lti_change_password) void changePassword() {
        if (FastClickUtil.isFastClick()) return;

        startActivity(new Intent(this, ChangePasswordActivity.class));
    }

    @OnClick(R.id.lti_feedback) void feedback() {
        if (FastClickUtil.isFastClick()) return;

        Intent intent = new Intent(this, FeedbackActivity.class);

        startActivity(intent);
    }

    @OnClick(R.id.lti_about_us) void aboutus() {
        if (FastClickUtil.isFastClick()) return;

        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("title", "关于我们");
        intent.putExtra("url", ServiceAddressConstant.BASE_ADDRESS + "?PortalId=0&ModuleId=0&TabId=0&method=gethtml&view=aboutus.html");

        startActivity(intent);
    }

    @OnClick(R.id.lti_law) void onLaw() {
        if (FastClickUtil.isFastClick()) return;

        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("title", "法律条款");
        if (Application.getInstance().isWorkerPackage()) {
            intent.putExtra("url", ServiceAddressConstant.BASE_ADDRESS + "?PortalId=0&ModuleId=0&TabId=0&method=gethtml&view=leagelw.html");
        } else {
            intent.putExtra("url", ServiceAddressConstant.BASE_ADDRESS + "?PortalId=0&ModuleId=0&TabId=0&method=gethtml&view=leagel.html");
        }
        startActivity(intent);
    }

    /**
     * 切换账户/注销
     */
    @OnClick(R.id.btn_logout) void logout() {
        if (FastClickUtil.isFastClick()) return;

        finish();

        if (MainActivity.Instance() != null) {
            MainActivity.Instance().logout();
        }
    }
}
