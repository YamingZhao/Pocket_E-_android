package net.wezu.jxg.ui.user;

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
import net.wezu.jxg.model.UserCategory;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.base.BaseListActivity;
import net.wezu.jxg.ui.base.BaseListAdapter;
import net.wezu.jxg.ui.base.BaseViewHolder;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.dialog.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 维修项目列表
 *
 * @author snox@live.com
 * @date 2015/10/27.
 */
public class ListCategoriesActivity extends BaseListActivity<Category, ListCategoriesActivity.CategoryViewHolder> {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_user_list_categories);
//
//        setTitle("维修项目");
//
//        setDefaultBackButton();
//    }


    @Override
    protected void setContentView() {
        super.setContentView();

        setTitle("维修项目");
        setDefaultBackButton();
    }

    @Override
    protected int getListItemLayoutResourceId() {
        return R.layout.listitem_category_for_service;
    }

    @Override
    protected CategoryViewHolder createViewHolder(Context context, View convertView) {
        return new CategoryViewHolder(this, convertView);
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
            checkBox.setChecked(data.isChecked);
            checkBox.setVisibility(View.GONE);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (FastClickUtil.isFastClick()) return;

                    data.isChecked = isChecked;

                    //btnConfirm.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    protected void refreshData() {

        setRefreshing(true);

        Map<String, String> params = new HashMap<>();
        params.put("type", "service");

        RequestManager.getInstance().getList("listcategories", requestTag, params, Category.class, new RequestManager.ResponseListener<List<Category>>() {
            @Override
            public void success(List<Category> result, String msg) {

                allCategories = result;
                loadCheckedItems();
            }

            @Override
            public void error(String msg) {
                setRefreshing(false);
            }
        });
    }

    @Override
    protected boolean equalItem(Category item1, Category item2) {
        return false;
    }

    private List<Category> allCategories;
    private Category addCategory(int id) {
        for (Category category : allCategories) {
            if (category.CategoryId == id) {
                return category;
            }
        }
        return null;
    }

    private void loadCheckedItems() {
        RequestManager.getInstance().getList("getuserservice", requestTag, null, UserCategory.class, new RequestManager.ResponseListener<List<UserCategory>>() {
            @Override
            public void success(List<UserCategory> result, String msg) {

                clear();

                List<Category> categories = new ArrayList<Category>();
                for (UserCategory uc : result) {
                    categories.add(addCategory(uc.ServiceId));
                }

                addDataItems(categories);
            }

            @Override
            public void error(String msg) {
                setRefreshing(false);
            }
        });
    }

//    private void setItemChecked(int id) {
//        for (int i = 0; i < mAdapter.getCount(); i++) {
//            if (mAdapter.getItem(i).CategoryId == id) {
//                mAdapter.getItem(i).isChecked = true;
//            }
//        }
//    }
}
