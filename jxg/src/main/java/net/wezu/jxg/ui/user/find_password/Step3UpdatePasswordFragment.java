package net.wezu.jxg.ui.user.find_password;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.ui.base.BaseFragment;
import net.wezu.jxg.util.FastClickUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author snox@live.com
 * @date 2015/10/26.
 */
public class Step3UpdatePasswordFragment extends BaseFragment {

    private String mobileNo;
    private String code;

    @Bind(R.id.txt_mobile) TextView txtMobileNo;
    @Bind(R.id.edit_password) EditText editPassword;
    @Bind(R.id.edit_password_comfirm) EditText editPasswordConfirm;

    public Step3UpdatePasswordFragment() {
    }

    public static Step3UpdatePasswordFragment create(String mobileNo, String code) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", mobileNo);
        bundle.putString("code", code);

        Step3UpdatePasswordFragment fragment = new Step3UpdatePasswordFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.fragment_modify_password);

        this.mobileNo = getArguments().getString("mobile");
        this.code = getArguments().getString("code");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtMobileNo.setText("手机号码 " + mobileNo);
    }

    @OnClick(R.id.btn_next_step) void updatePassword() {
        if (FastClickUtil.isFastClick()) return;

        String password = editPassword.getText().toString();
        String passwordConfirm = editPasswordConfirm.getText().toString();
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm)) {
            ToastUtils.show(getActivity(), "请输入密码");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            ToastUtils.show(getActivity(), "两次输入的密码不同");
            return;
        }

        if (password.length() < 8 || password.length() > 12) {
            ToastUtils.show(getActivity(), "密码必须在8 - 12位之间");
            return;
        }

        Map<String, String> parameter = new HashMap<>();
        parameter.put("username", mobileNo);
        parameter.put("newpassword", password);
        parameter.put("code", code);

        RequestManager.getInstance().post("updatepassword", null, parameter, Void.class, new RequestManager.ResponseListener<Void>() {
            @Override
            public void success(Void result, String msg) {
                // 验证码成功

                ToastUtils.show(getActivity(), "修改成功");

                getActivity().finish();
            }

            @Override
            public void error(String msg) {
                ToastUtils.show(getActivity(), TextUtils.isEmpty(msg) ? "修改失败，请重试" : msg);
            }
        });
    }
}
