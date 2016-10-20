package net.wezu.jxg.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.widget.dialog.FlippingLoadingDialog;

import butterknife.ButterKnife;

/**
 * @author snox@live.com
 * @date 2015/10/25.
 */
public class BaseFragment extends net.wezu.viewpagerindicator.fragment.BaseFragment {

    protected FragmentActivity activity;

    protected String requestTag;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        requestTag = getClass().getSimpleName();

        super.onCreateView(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        if (!TextUtils.isEmpty(requestTag)) {
            RequestManager.getInstance().cancelAll(requestTag);
        }
        super.onDestroyView();
    }

    protected void toast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            ToastUtils.show(getActivity(), msg);
        }
    }

    protected FlippingLoadingDialog mLoadingDialog;

    public FlippingLoadingDialog getLoadingDialog(String message) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new FlippingLoadingDialog(getActivity(), message);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (FragmentActivity) activity;
    }

    protected void showNext(Fragment fragment, int id) {
        showNext(fragment, id, null, true);
    }

    protected void showNext(Fragment fragment, int id, Bundle b) {
        showNext(fragment, id, b, true);
    }

    protected void showNext(Fragment fragment, int id, Bundle b, boolean isAddBackStack) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
        fragmentTransaction.replace(id, fragment);
        if (b != null) {
            fragment.setArguments(b);
        }
        if (isAddBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void back() {
        activity.getSupportFragmentManager().popBackStackImmediate();
    }
}
