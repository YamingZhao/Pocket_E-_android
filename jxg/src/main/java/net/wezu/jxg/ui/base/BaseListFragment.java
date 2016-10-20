package net.wezu.jxg.ui.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.data.PagedResult;
import net.wezu.widget.SwipeRefreshLoadMoreLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * Created by snox on 2015/11/17.
 */
public abstract class BaseListFragment<T, VH extends BaseViewHolder<T>> extends BaseFragment {

    @Bind(R.id.refresh_layout)
    SwipeRefreshLoadMoreLayout refreshLayout;

    @Bind(android.R.id.list)
    public ListView listView;

    BaseListAdapter<T, VH> mAdapter;

    private int size = 6;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        setContentView();

        //设置卷内的颜色
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshData();
            }
        });

        refreshLayout.setOnLoadListener(new SwipeRefreshLoadMoreLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                loadData(getCount(), size);
            }
        });



        mAdapter = createListAdapter();

        listView.setAdapter(mAdapter);

        View empty = findViewById(R.id.empty);
        if (empty != null) {
            listView.setEmptyView(empty);
        }

        setDividerHeight(12);

        //setEmptyView("正在加载数据");

        refreshData();
    }

    protected void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
    }

    protected void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        listView.setOnItemLongClickListener(listener);
    }

    protected void toast(String msg) {
        ToastUtils.show(getActivity(), msg);
    }

    public void setDividerHeight(int height) {
        listView.setDividerHeight(height);
    }

    protected void setContentView() {
        setContentView(R.layout.refreshable_listview);
    }

    protected void refreshData() {
        refreshLayout.setRefreshing(true);
        clear();
        loadData(0, size);
    }

    protected void loadData(int page, int size) { }

    private BaseListAdapter<T, VH> createListAdapter() {
        return new BaseListAdapter<T, VH>(getActivity(), getListItemLayoutResourceId()) {
            @Override
            protected VH buildViewHolder(Context context, View convertView) {
                return createViewHolder(context, convertView);
            }
        };
    }

    protected void clear() {
        mAdapter.clear();
    }

    protected void setError(String msg) {
        refreshLayout.setRefreshing(false);

        refreshLayout.setEmptyView(msg, true, 0);
        toast(msg);
    }

    public int getCount() {
        return mAdapter.getCount();
    }

    protected ArrayList<T> getItems() {
        ArrayList<T> items = new ArrayList<>();

        for (int i = 0; i < mAdapter.getCount(); i++) {
            items.add(mAdapter.getItem(i));
        }
        return items;
    }

    protected void addDataItems(List<T> items) {
        if (items != null) {
            mAdapter.setNotifyOnChange(false);
            for (T item : items) {
                if (!containsItem(item)) {
                    mAdapter.add(item);
                }
            }
            mAdapter.notifyDataSetChanged();
        }

        refreshLayout.setRefreshing(false);
        refreshLayout.setLoading(false);
    }

    protected void addDataItems(PagedResult<T> result) {
        addDataItems(result.rows);
        refreshLayout.setCanLoadmore(result.total > mAdapter.getCount());
    }

    public void setRefreshing(boolean isRefreshing) {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(isRefreshing);
        }
    }

    public void removeItem(T item) {
        mAdapter.remove(item);
    }

    protected boolean containsItem(T item) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (equalItem(item, mAdapter.getItem(i)))
                return true;
        }
        return false;
    }

    /**
     * 是否为相同项
     * @param item1
     * @param item2
     * @return
     */
    protected abstract boolean equalItem(T item1, T item2);

    /**
     * 获取列表项的布局资源ID
     * @return
     */
    protected abstract int getListItemLayoutResourceId();

    /**
     * 创建视图缓存
     *
     * @param context
     * @param convertView
     * @return
     */
    protected abstract VH createViewHolder(Context context, View convertView);
}
