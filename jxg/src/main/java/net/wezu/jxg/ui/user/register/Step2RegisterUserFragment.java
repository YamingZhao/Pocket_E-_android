package net.wezu.jxg.ui.user.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.Category;
import net.wezu.jxg.model.UserModel;
import net.wezu.jxg.service.ServiceAddressConstant;
import net.wezu.jxg.service.UserService;
import net.wezu.jxg.ui.WebViewActivity;
import net.wezu.jxg.ui.base.BaseFragment;
import net.wezu.jxg.ui.base.BaseListAdapter;
import net.wezu.jxg.ui.base.BaseViewHolder;
import net.wezu.jxg.util.DES;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.jxg.util.IDCardUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author snox@live.com
 * @date 2015/10/26.
 */
public class Step2RegisterUserFragment extends BaseFragment {

    private String mobileNo;
    private String gender;

    @Bind(R.id.tv_register_agree) TextView tvRegisterAgree;
    @Bind(R.id.txt_mobile) TextView txtMobile;
    @Bind(R.id.edt_password) EditText edtPassword;
    @Bind(R.id.edt_password_confirm) EditText edtPasswordConfirm;
    @Bind(R.id.edt_nickname) EditText edtNickName;
    @Bind(R.id.rg_gender)  RadioGroup rbGender;
    @Bind(R.id.ll_id_no) View layoutIdNo;
    @Bind(R.id.edt_id_no) EditText etIdNo;

    private BaseListAdapter<Category, CategoryViewHolder> mAdapter;


    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        setContentView(R.layout.fragment_register_user);

        mobileNo = getArguments().getString("mobile");

        txtMobile.setText("手机号码   " + mobileNo);

        rbGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_female:
                        gender = "F";
                        break;
                    case R.id.rb_male:
                        gender = "M";
                        break;
                }
            }
        });

        boolean isWorkerPackage = Application.getInstance().isWorkerPackage();

        layoutIdNo.setVisibility(isWorkerPackage ? View.VISIBLE : View.GONE);
        layoutIdNo.setVisibility(View.GONE);

        if (isWorkerPackage) {
            tvRegisterAgree.setText("点击注册即表示同意《机修工协议》");
//            layoutIdNo.setVisibility(View.VISIBLE);
//            lvCarList.setVisibility(View.VISIBLE);
//
//            mAdapter = new BaseListAdapter<Category, CategoryViewHolder>(getActivity(), R.layout.listitem_category_for_service) {
//
//                @Override
//                protected CategoryViewHolder buildViewHolder(Context context, View convertView) {
//                    return new CategoryViewHolder(getActivity(), convertView);
//                }
//            };
//
//            lvCarList.setAdapter(mAdapter);
//
//            loadData();
        } else {
            tvRegisterAgree.setText("点击注册即表示同意《用户协议》");
//            layoutIdNo.setVisibility(View.GONE);
//            lvCarList.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_register_agree) void onAgreeClick() {
        if (FastClickUtil.isFastClick()) return;

        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("title", "注册协议");
        if (Application.getInstance().isWorkerPackage()) {
            intent.putExtra("url", ServiceAddressConstant.BASE_ADDRESS + "?PortalId=0&ModuleId=0&TabId=0&method=gethtml&view=leagelw.html");
        } else {
            intent.putExtra("url", ServiceAddressConstant.BASE_ADDRESS + "?PortalId=0&ModuleId=0&TabId=0&method=gethtml&view=leagel.html");
        }

        startActivity(intent);
    }

//    private void loadData() {
//        Map<String, String> params = new HashMap<>();
//        params.put("parentid", "0");
//        params.put("type", "service");
//
//        RequestManager.getInstance().getList("listcategories", requestTag, params, Category.class, new RequestManager.ResponseListener<List<Category>>() {
//            @Override
//            public void success(List<Category> result, String msg) {
//                mAdapter.clear();
//                mAdapter.addAll(result);
//
//                //loadCheckedItems();
//            }
//
//            @Override
//            public void error(String msg) {
//                //refreshLayout.setRefreshing(false);
//            }
//        });
//    }
//
//    private String getCheckedIds() {
//
//        String ids = "";
//
//        for (int i = 0; i < mAdapter.getCount(); i++) {
//            Category category = mAdapter.getItem(i);
//
//            if (category.isChecked)
//                ids += (TextUtils.isEmpty(ids) ? "" : ",") + category.CategoryId;
//        }
//
//        return ids;
//    }

    @OnClick(R.id.btn_next_step) void registerUser(View v) {
        if (FastClickUtil.isFastClick()) return;
        //ScreenTools.dismissKeyboard();

        final String password = edtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.show(getActivity(), "请输入密码");
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 8 || password.length() > 12) {
            ToastUtils.show(getActivity(), "密码必须在8 - 12位之间");
            return;
        }

        String passwordConfirm = edtPasswordConfirm.getText().toString();
        if (!password.equals(passwordConfirm)) {
            ToastUtils.show(getActivity(), "两次输入的密码不一样");
            edtPasswordConfirm.requestFocus();
            return;
        }

        String nickName = edtNickName.getText().toString();
        if (TextUtils.isEmpty(nickName)) {
            ToastUtils.show(getActivity(), "请输入姓名");
            edtNickName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(gender)) {
            ToastUtils.show(getActivity(), "请选择性别");
            return;
        }

        final Map<String, String> parameter = new HashMap<>();
        parameter.put("username", DES.encryptDES(mobileNo));
        parameter.put("gender", gender);
        parameter.put("name", nickName);
        parameter.put("password", DES.encryptDES(password));
        parameter.put("passwordConfirm", DES.encryptDES(passwordConfirm));

        if (Application.getInstance().isWorkerPackage()) {
            parameter.put("role", "Worker");

            // 身份证校验
//            String idNo = etIdNo.getText().toString();
//            String infoErrorInfo = IDCardUtil.IDCardValidate(idNo);
//            if (!TextUtils.isEmpty(infoErrorInfo)) {
//                etIdNo.setError(infoErrorInfo);
//                toast(infoErrorInfo);
//                return;
//            }
//
//            parameter.put("identity", idNo);

            Step3WorkerRegisterFragment fragment = new Step3WorkerRegisterFragment();
            fragment.setRegisterInformation(parameter);

            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content,
                    fragment).commitAllowingStateLoss();
        } else {
            UserService.registerUser(requestTag, parameter, new RequestManager.ResponseListener<UserModel>() {
                @Override
                public void success(UserModel result, String msg) {
                    // 验证码成功

                    ToastUtils.show(getActivity(), "注册成功");

                    Bundle bundle = new Bundle();
                    bundle.putString("username", mobileNo);
                    bundle.putString("password", password);

                    Step3UploadAvatarFragment fragment = new Step3UploadAvatarFragment();
                    fragment.setArguments(bundle);

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content,
                            fragment).commitAllowingStateLoss();
                }

                @Override
                public void error(String msg) {
                    ToastUtils.show(getActivity(), !TextUtils.isEmpty(msg) ? msg : "验证失败，请重试");
                }
            });
        }
    }

    public class CategoryViewHolder extends BaseViewHolder<Category> {

        @Bind(R.id.ckb_cate)
        CheckBox checkBox;
        @Bind(R.id.txt_cate_name)
        TextView txtCategoryName;


        public CategoryViewHolder(Context context, View view) {
            super(context, view);
        }

        @Override
        public void setData(final Category data) {

            txtCategoryName.setText(data.Name);
            checkBox.setChecked(data.isChecked);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    data.isChecked = isChecked;

                    //btnConfirm.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
