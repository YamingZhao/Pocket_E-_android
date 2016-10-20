package net.wezu.jxg.ui.user.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import net.wezu.jxg.R;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.base.BaseFragmentActivity;
import net.wezu.jxg.ui.user.register.Step1RegisterPhoneVerifyFragment;

import java.util.List;

/**
 * @author snox@live.com
 * @date 2015/10/26.
 */
public class RegisterActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_fragment);

        setTitle("注册");
        setDefaultBackButton();
        setFragment(new Step1RegisterPhoneVerifyFragment());
    }

//    private void setFragment(Fragment fragment) {
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_content, fragment)
//                .commitAllowingStateLoss();
//    }

    private static final String TAG = "RegisterActivity";

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        FragmentManager fm = getSupportFragmentManager();
//        int index = requestCode >> 16;
//        if (index != 0) {
//            index--;
//
//            if (fm.getFragments() == null || index < 0
//                    || index >= fm.getFragments().size()) {
//                Log.w(TAG, "Activity result fragment index out of range: 0x"
//                        + Integer.toHexString(requestCode));
//                return;
//            }
//            Fragment frag = fm.getFragments().get(index);
//            if (frag == null) {
//                Log.w(TAG, "Activity result no fragment exists for index: 0x"
//                        + Integer.toHexString(requestCode));
//            } else {
//                handleResult(frag, requestCode, resultCode, data);
//            }
//            return;
//        }
//    }
//
//    private void handleResult(Fragment frag, int requestCode, int resultCode,
//                              Intent data) {
//        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
//
//        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
//        if (frags != null) {
//            for (Fragment f : frags) {
//                if (f != null)
//                    handleResult(f, requestCode, resultCode, data);
//            }
//        }
//    }
}
