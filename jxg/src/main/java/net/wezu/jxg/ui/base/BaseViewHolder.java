package net.wezu.jxg.ui.base;

import android.content.Context;
import android.view.View;

import butterknife.ButterKnife;

/**
 * @author snox@live.com
 * @date 2015/9/20.
 */
public abstract class BaseViewHolder<T> {

    protected final View mView;
    protected final Context mContext;


    public BaseViewHolder(Context context, View view) {
        mView = view;
        mContext = context;

        ButterKnife.bind(this, view);
    }

    public abstract void setData(T data);
}
