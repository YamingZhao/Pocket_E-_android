package net.wezu.jxg.ui.user.find_password;

import android.os.Bundle;

import net.wezu.jxg.ui.base.BaseFragmentActivity;
import net.wezu.jxg.ui.user.find_password.Step1VerifyPhoneNumberFragment;

/**
 * @author snox@live.com
 * @date 2015/10/24.
 */
public class FindPasswordActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("忘记密码");
        setDefaultBackButton();

        setFragment(new Step1VerifyPhoneNumberFragment());
    }
}
