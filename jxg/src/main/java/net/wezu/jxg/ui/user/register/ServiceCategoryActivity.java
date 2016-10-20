package net.wezu.jxg.ui.user.register;

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

import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.Category;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.base.BaseListAdapter;
import net.wezu.jxg.ui.base.BaseViewHolder;
import net.wezu.jxg.ui.user.ListCategoriesActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 机修工注册时，申请维修项目
 * Created by snox on 2016/3/24.
 */
public class ServiceCategoryActivity extends BaseActivity {

    private BaseListAdapter<Category, CategoryViewHolder> mAdapter;

    @Bind(android.R.id.list) ListView lvCarList;
    @Bind(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
    @Bind(R.id.btn_submit) Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_service_categories);

        setDefaultBackButton();
        setTitle("选择申请项目");

        mAdapter = new BaseListAdapter<Category, CategoryViewHolder>(this, R.layout.listitem_category_for_service) {

            @Override
            protected CategoryViewHolder buildViewHolder(Context context, View convertView) {
                return new CategoryViewHolder(ServiceCategoryActivity.this, convertView);
            }
        };

        lvCarList.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        selectedCategories = getIntent().getParcelableArrayListExtra("service_items");

        loadData();
    }

    private ArrayList<Category> selectedCategories;

    private boolean isSelected(Category category) {
        if (selectedCategories == null) return false;

        for (Category c : selectedCategories) {
            if (c.CategoryId == category.CategoryId) return true;
        }
        return false;
    }

    private void loadData() {

        refreshLayout.setRefreshing(true);

        Map<String, String> params = new HashMap<>();
        params.put("type", "service");

        RequestManager.getInstance().getList("listcategories", requestTag, params, Category.class, new RequestManager.ResponseListener<List<Category>>() {
            @Override
            public void success(List<Category> result, String msg) {

                mAdapter.addAll(result);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void error(String msg) {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public class CategoryViewHolder extends BaseViewHolder<Category> {

        @Bind(R.id.ckb_cate) CheckBox checkBox;
        @Bind(R.id.txt_cate_name) TextView txtCategoryName;

        public CategoryViewHolder(Context context, View view) {
            super(context, view);
        }

        @Override
        public void setData(final Category data) {

            txtCategoryName.setText(data.Name);
            checkBox.setChecked(isSelected(data));// data.isChecked);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    data.isChecked = isChecked;

                    btnConfirm.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @OnClick(R.id.btn_submit) void submit() {

        ArrayList<Category> selectedCategories = new ArrayList<>();

        for (int i = 0; i < mAdapter.getCount(); i++) {
            Category category = mAdapter.getItem(i);

            if (category.isChecked)
                selectedCategories.add(category);
        }

        if (selectedCategories.size() > 0) {
            setResult(RESULT_OK, new Intent().putParcelableArrayListExtra("data", selectedCategories));
            finish();
        }
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
}
