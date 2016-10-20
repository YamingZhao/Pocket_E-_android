package net.wezu.jxg.ui.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.wezu.jxg.R;
import net.wezu.jxg.data.PagedResult;
import net.wezu.widget.SwipeRefreshLoadMoreLayout;

import java.util.List;

import butterknife.Bind;

/**
 * Created by snox on 2016/4/23.
 */
public abstract class BasePagedListActivity<T, VH extends BaseViewHolder<T>> extends BaseActivity {

    @Bind(R.id.refresh_layout) SwipeRefreshLoadMoreLayout refreshLayout;

    @Bind(android.R.id.list) ListView listView;

    BaseListAdapter<T, VH> mAdapter;

    private int size = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                loadData(mAdapter.getCount(), size);
            }
        });

        mAdapter = createListAdapter();

        listView.setAdapter(mAdapter);

        ColorDrawable c = new ColorDrawable();
        c.setColor(Color.parseColor("#d4d4d4"));
        listView.setDivider(c);

        //setEmptyView("正在加载数据");

        //refreshData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        refreshData();
    }

    protected void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    public void setRefreshLayoutEnabled(boolean refreshable) {
        refreshLayout.setEnabled(refreshable);
    }

    public void setDividerHeight(int height) {
        listView.setDividerHeight(height);
    }

    protected void setContentView() {
        setContentView(R.layout.activity_refreshable_listview);
    }

    protected void refreshData() {
        clear();

        loadData(0, size);
    }

    protected abstract void loadData(int page, int size);

    private BaseListAdapter<T, VH> createListAdapter() {
        return new BaseListAdapter<T, VH>(this, getListItemLayoutResourceId()) {
            @Override
            protected VH buildViewHolder(Context context, View convertView) {
                return createViewHolder(context, convertView);
            }
        };
    }

    protected void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
    }

    protected void clear() {
        mAdapter.clear();
    }

    protected T getItem(int position) {
        return mAdapter.getItem(position);
    }

    private void addDataItems(List<T> items) {
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

    private boolean containsItem(T item) {
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
