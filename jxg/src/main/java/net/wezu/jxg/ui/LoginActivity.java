package net.wezu.jxg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.wezu.framework.util.PreferenceUtil;
import net.wezu.jxg.model.LoginResult;
import net.wezu.jxg.service.UserService;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.ui.user.find_password.FindPasswordActivity;
import net.wezu.jxg.ui.user.register.RegisterActivity;
import net.wezu.jxg.ui.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * @author snox@live.com
 * @date 2015/10/21.
 */
public class LoginActivity extends BaseActivity {

    public static final String APP_UPDATE_USER_SERVER_URL = "http://wx.wezu.net/jxg/user.update.json";
    public static final String APP_UPDATE_WORKER_SERVER_URL = "http://wx.wezu.net/jxg/worker.update.json";

    @Bind(R.id.edit_username) EditText edtUsername;
    @Bind(R.id.edit_password) EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        setTitle("登录");

        // 注册按钮
        setRightButton("注册", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRegisterActivity();
            }
        });

        //edtUsername.setText("13162161023");
        //edtPassword.setText("56785678");

//        if (Application.getInstance().isWorkerPackage()) {
//            UpdateChecker.checkForDialog(this, APP_UPDATE_WORKER_SERVER_URL);
//        } else {
//            UpdateChecker.checkForDialog(this, APP_UPDATE_USER_SERVER_URL);
//        }

        if (getIntent().getBooleanExtra("logout", false)) {
            PreferenceUtil.Clear(this, "saved_password");
        }

        if (Application.getInstance().isLogin()) {
            gotoMainActivity();
        } else {
            Application.getInstance().setLoginResult(null);
            handleLogin();
        }
    }

    @OnClick(R.id.btn_login) void ButtonLoginClick(View v) {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(LoginActivity.this, "用户名必须填写", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "密码必须填写", Toast.LENGTH_LONG).show();
        } else {
            //login(username, MD5.getMD5(password));
            login(username, password);
        }
    }

    /**
     * 处理用户登录，如果有保存用户的登录数据，直接登陆
     */
    private void handleLogin() {

        String username = PreferenceUtil.getString(LoginActivity.this, "saved_username", "");
        String password = PreferenceUtil.getEncryptString(LoginActivity.this, "saved_password");

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            if (!TextUtils.isEmpty(username)) {
                edtUsername.setText(username);
            }
        } else {
            edtUsername.setText(username);
            edtPassword.setText(password);

            login(username, password);
        }
    }

    private void login(final String username, final String password) {

        getLoadingDialog("正在登录").show();

        UserService.login(requestTag, username, password, new RequestManager.ResponseListener<LoginResult>() {
            @Override
            public void success(LoginResult result, String msg) {
                Application application = Application.getInstance();

                application.setLoginResult(result);

                if (Application.getInstance().isValid()) {
                    // 登录成功，保存用户名密码
                    PreferenceUtil.putString(LoginActivity.this, "saved_username", username);
                    PreferenceUtil.putEncryptString(LoginActivity.this, "saved_password", password);

                    UserService.bind(Application.getInstance().getCid(), new RequestManager.ResponseListener<Object>() {
                        @Override
                        public void success(Object result, String msg) {
                            dismissLoadingDialog();
                            gotoMainActivity();
                        }

                        @Override
                        public void error(String msg) {
                            toast(msg);
                            dismissLoadingDialog();
                        }
                    });
                } else {
                    if (application.isWorker()) {
                        toast("此版本为用户客户端，不支持机修工登录");
                    } else {
                        toast("此版本为机修工客户端，不支持用户登录");
                    }
                    application.setLoginResult(null);
                    dismissLoadingDialog();
                }
            }

            @Override
            public void error(String msg) {
                dismissLoadingDialog();
                toast(msg);
            }
        });
    }

    private void gotoMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @OnClick(R.id.button_forget_password) void gotoForgetPasswordActivity() {
        startActivity(new Intent(this, FindPasswordActivity.class));
    }

    private static final int RQ_REGISTER = 368;

    private void gotoRegisterActivity() {
        startActivityForResult(new Intent(this, RegisterActivity.class), RQ_REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case RQ_REGISTER:
                String username = data.getStringExtra("username");
                String password = data.getStringExtra("password");
                login(username, password);
                break;
        }
    }
}
