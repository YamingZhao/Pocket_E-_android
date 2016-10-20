package net.wezu.jxg.ui.user.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.Category;
import net.wezu.jxg.ui.base.BaseFragment;
import net.wezu.jxg.ui.base.BaseListAdapter;
import net.wezu.jxg.ui.base.BaseViewHolder;
import net.wezu.jxg.ui.user.ListCategoriesActivity;
import net.wezu.widget.dialog.CustomProgressDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 注册服务
 * Created by snox on 2016/3/1.
 */
public class Step4RegisterServiceFragment extends BaseFragment {

    private String token;

    @Bind(android.R.id.list)
    ListView lvCarList;
    private BaseListAdapter<Category, CategoryViewHolder> mAdapter;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @Bind(R.id.btn_confirm)
    Button btnConfirm;

    @Override
    public void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        token = getArguments().getString("token");

        setContentView(R.layout.fragment_list_categories);

        mAdapter = new BaseListAdapter<Category, CategoryViewHolder>(getActivity(), R.layout.listitem_category_for_service) {

            @Override
            protected CategoryViewHolder buildViewHolder(Context context, View convertView) {
                return new CategoryViewHolder(getActivity(), convertView);
            }
        };

        lvCarList.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        loadData();
    }

    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        RequestManager.getInstance().getList("listcategories", requestTag, params, Category.class, new RequestManager.ResponseListener<List<Category>>() {
            @Override
            public void success(List<Category> result, String msg) {
                mAdapter.clear();
                mAdapter.addAll(result);

                //loadCheckedItems();
            }

            @Override
            public void error(String msg) {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private String getCheckedIds() {

        String ids = "";

        for (int i = 0; i < mAdapter.getCount(); i++) {
            Category category = mAdapter.getItem(i);

            if (category.isChecked)
                ids += (TextUtils.isEmpty(ids) ? "" : ",") + category.CategoryId;
        }

        return ids;
    }

    @OnClick(R.id.btn_confirm) void confirm() {

        final CustomProgressDialog progress = CustomProgressDialog.getDialog(getActivity());
        progress.setMessage("正在提交数据");
        progress.show();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("items", getCheckedIds());
        parameters.put("token", token);

        RequestManager.getInstance().post("updateuserservice", null, parameters, String.class, new RequestManager.ResponseListener<String>() {
            @Override
            public void success(String result, String msg) {
                toast("更新成功");
                progress.dismiss();

                getActivity().setResult(Activity.RESULT_OK, new Intent().putExtra("token", getArguments().getString("username")).putExtra("password", getArguments().getString("password")));
                getActivity().finish();
            }

            @Override
            public void error(String msg) {
                toast(TextUtils.isEmpty(msg) ? "更新失败" : msg);
                progress.dismiss();
            }
        });

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
