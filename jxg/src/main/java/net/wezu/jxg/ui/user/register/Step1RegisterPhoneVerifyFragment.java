package net.wezu.jxg.ui.user.register;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import net.wezu.framework.util.StringUtil;
import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.ui.base.BaseFragment;
import net.wezu.jxg.util.FastClickUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * @author snox@live.com
 * @date 2015/10/26.
 */
public class Step1RegisterPhoneVerifyFragment extends BaseFragment {

    @Bind(R.id.edit_mobile) EditText editMobileNo;
    @Bind(R.id.edit_verifycode) EditText editVerifycode;
    @Bind(R.id.btn_get_verifycode) Button btnGetVerifyCode;
    @Bind(R.id.btn_next_step) Button btnNext;

    private TimeCount timer;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        setContentView(R.layout.fragment_register_phone_verify);

        editVerifycode.setEnabled(false);
        btnNext.setEnabled(false);

        setSendEnabled(false);

        timer = new TimeCount(45000, 1000);//构造CountDownTimer对象
    }

    @OnTextChanged(R.id.edit_mobile) void phoneNumberChanged() {
        String no = editMobileNo.getText().toString();

        setSendEnabled(StringUtil.isMobileNO(no));
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

    private void setSendEnabled(boolean enabled) {
        btnGetVerifyCode.setEnabled(enabled);
        btnGetVerifyCode.setTextColor(getResources().getColor(
                enabled ? R.color.app_red : android.R.color.darker_gray));
    }

    @OnClick(R.id.btn_get_verifycode) void getVerifyCode() {
        if (FastClickUtil.isFastClick()) return;

        mobileNo = editMobileNo.getText().toString();

        setSendEnabled(false);

        Map<String, String> parameter = new HashMap<>();
        parameter.put("username", mobileNo);
        parameter.put("type", "R");

        RequestManager.getInstance().post("getverificationcode", null, parameter, String.class, new RequestManager.ResponseListener<String>() {
            @Override
            public void success(String result, String msg) {
                // 获取验证码成功
                ToastUtils.show(getActivity(), "验证码发送成功");

                // 这里需要起一个定时器，启用发送按钮
                editVerifycode.setHint("请输入验证码");
                editVerifycode.setEnabled(true);
                editVerifycode.requestFocus();

                timer.start();

                btnNext.setEnabled(true);
            }

            @Override
            public void error(String msg) {
                ToastUtils.show(getActivity(), TextUtils.isEmpty(msg) ? "发送验证码失败" : msg);
                setSendEnabled(true);
            }
        });
    }

    private String mobileNo;

    @OnClick(R.id.btn_next_step) void verifyCode() {
        String code = editVerifycode.getText().toString();

        if (TextUtils.isEmpty(code)) {
            ToastUtils.show(getActivity(), "请输入验证码");
            return;
        }

        Map<String, String> parameter = new HashMap<>();
        parameter.put("username", mobileNo);
        parameter.put("type", "R");
        parameter.put("code", code);

        RequestManager.getInstance().post("verifycode", null, parameter, String.class, new RequestManager.ResponseListener<String>() {
            @Override
            public void success(String result, String msg) {
                // 验证码成功

                Bundle bundle = new Bundle();
                bundle.putString("mobile", mobileNo);

                Step2RegisterUserFragment fragment = new Step2RegisterUserFragment();
                fragment.setArguments(bundle);

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnNext.getWindowToken(), 0);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content,
                        fragment).commitAllowingStateLoss();
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
            btnGetVerifyCode.setText(millisUntilFinished / 1000 + " 秒后重新获取");
        }

        @Override
        public void onFinish() {
            setSendEnabled(true);
            btnGetVerifyCode.setText("获取验证码");
        }
    }
}
