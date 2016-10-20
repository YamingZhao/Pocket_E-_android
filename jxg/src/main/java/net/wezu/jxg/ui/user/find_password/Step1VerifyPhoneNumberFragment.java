package net.wezu.jxg.ui.user.find_password;

import android.os.Bundle;
import android.widget.EditText;

import net.wezu.framework.util.StringUtil;
import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.ui.base.BaseFragment;
import net.wezu.jxg.util.FastClickUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author snox@live.com
 * @date 2015/10/24.
 */
public class Step1VerifyPhoneNumberFragment extends BaseFragment {

    @Bind(R.id.edit_mobile) EditText editMobile;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.framgment_findpassword_step1);
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        editMobile.setText("13162161023");
//    }

    @OnClick(R.id.btn_next_step) void onNextStep() {
        if (FastClickUtil.isFastClick()) return;

        String input = editMobile.getText().toString();

        if (!StringUtil.isMobileNO(input)) {
            ToastUtils.show(getActivity(), "请输入有效的手机号码");
            return;
        }

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content,
                Step2VerifyCodeFragment.create(input)).commitAllowingStateLoss();
    }
}
