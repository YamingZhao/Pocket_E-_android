package net.wezu.jxg.ui.user.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.service.UserService;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.util.FastClickUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by snox on 2016/4/9.
 */
public class ChangePasswordActivity extends BaseActivity {

    @Bind(R.id.et_password_current) EditText etPasswordCurrent;
    @Bind(R.id.et_password_new) EditText etPasswordNew;
    @Bind(R.id.et_password_confirm) EditText etPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_password);
        setTitle("修改密码");
        setDefaultBackButton();
    }

    @OnClick(R.id.btn_submit) void submit() {
        if (FastClickUtil.isFastClick()) return;

        String passwordOld = etPasswordCurrent.getText().toString();
        if (TextUtils.isEmpty(passwordOld)) {
            etPasswordCurrent.setError("请输入密码");
            return;
        }
        final String passwordNew = etPasswordNew.getText().toString();
        if (passwordNew.length() <8 || passwordNew.length() >16) {
            etPasswordNew.setError("请输入新密码，长度8 - 16位");
            return;
        }
        String passwordComfirm= etPasswordConfirm.getText().toString();
        if (!passwordComfirm.equals(passwordNew)) {
            etPasswordConfirm.setError("两次输入的密码不一致");
            return;
        }

        UserService.changePassword(requestTag, passwordOld, passwordNew, new RequestManager.ResponseListener<Object>() {
            @Override
            public void success(Object result, String msg) {
                toast("密码修改成功");
                RequestManager.getInstance().updateToken(passwordNew);
                finish();
            }

            @Override
            public void error(String msg) {
                toast(msg);
            }
        });
    }
}
