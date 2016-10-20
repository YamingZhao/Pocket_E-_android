package net.wezu.jxg.ui.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.Collection;

/**
 * @author snox@live.com
 * @date 2015/9/20.
 */
public abstract class BaseListAdapter<T, VH extends BaseViewHolder<T>> extends ArrayAdapter<T> {

    private LayoutInflater mInflater;
    private int mResource;
    private Context mContext;

    public BaseListAdapter(Context context, int resource) {
        super(context, resource);

        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        VH holder = null;

        if (convertView == null || convertView.getTag() == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            holder = buildViewHolder(mContext, convertView);
            convertView.setTag(holder);
        } else {
            holder = (VH) convertView.getTag();
        }

        if (holder != null) {
            holder.setData(getItem(position));
        }

        return convertView;
    }

    public void addAll(Collection<? extends T> collection) {
        for (T item: collection) {
            add(item);
        }
        notifyDataSetChanged();
    }

    /**
     * 构建一个视图缓存器
     *
     * @return 视图
     */
    protected abstract VH buildViewHolder(Context context, View convertView);
}
