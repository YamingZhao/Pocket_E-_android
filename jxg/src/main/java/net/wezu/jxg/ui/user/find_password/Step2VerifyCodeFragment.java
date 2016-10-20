package net.wezu.jxg.ui.user.find_password;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
public class Step2VerifyCodeFragment extends BaseFragment {

    private String mobileNo;
    @Bind(R.id.txt_mobile) TextView txtMobileNo;
    @Bind(R.id.edit_verifycode) EditText editMobileNo;

    @Bind(R.id.btn_send) Button btnSend;

    private TimeCount timer;

    public Step2VerifyCodeFragment() {
    }

    public static Step2VerifyCodeFragment create(String mobileNo) {
        Bundle bundle = new Bundle();
        bundle.putString("mobile", mobileNo);

        Step2VerifyCodeFragment fragment = new Step2VerifyCodeFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.fragment_verify_code_auth);

        this.mobileNo = getArguments().getString("mobile");

        timer = new TimeCount(45000, 1000);

        setSendEnabled(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtMobileNo.setText(mobileNo);

        sendVerifyCode();
    }

    @Override
    public void onDestroyView() {
        try {
            timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    @OnClick(R.id.btn_send) void sendVerifyCode() {
        if (FastClickUtil.isFastClick()) return;

        Map<String, String> parameter = new HashMap<>();
        parameter.put("username", mobileNo);
        parameter.put("type", "P");

        setSendEnabled(false);

        RequestManager.getInstance().post("getverificationcode", null, parameter, String.class, new RequestManager.ResponseListener<String>() {
            @Override
            public void success(String result, String msg) {
                // 获取验证码成功
                ToastUtils.show(getActivity(), "验证码发送成功");

                // 这里需要起一个定时器，启用发送按钮

                timer.start();
            }

            @Override
            public void error(String msg) {
                ToastUtils.show(getActivity(), TextUtils.isEmpty(msg) ? "发送验证码失败" : msg);
                setSendEnabled(true);
            }
        });
    }

    private void setSendEnabled(boolean enabled) {
        btnSend.setEnabled(enabled);
        btnSend.setTextColor(getResources().getColor(
                enabled ? R.color.app_red : android.R.color.darker_gray));
    }


    @OnClick(R.id.btn_next_step) void verifyCode() {

        final String code = editMobileNo.getText().toString();
        if (TextUtils.isEmpty(code)) {
            return;
        }

        Map<String, String> parameter = new HashMap<>();
        parameter.put("username", mobileNo);
        parameter.put("type", "P");
        parameter.put("code", code);

        RequestManager.getInstance().post("verifycode", null, parameter, String.class, new RequestManager.ResponseListener<String>() {
            @Override
            public void success(String result, String msg) {
                // 验证码成功

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content,
                        Step3UpdatePasswordFragment.create(mobileNo, code)).commitAllowingStateLoss();
            }

            @Override
            public void error(String msg) {
                ToastUtils.show(getActivity(), "验证失败，请重试");
            }
        });
    }

    class TimeCount extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            setSendEnabled(false);
            btnSend.setText(millisUntilFinished / 1000 + " 秒后重新获取");
        }

        @Override
        public void onFinish() {
            setSendEnabled(true);
            btnSend.setText("获取验证码");
        }
    }
}
