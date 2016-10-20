package net.wezu.jxg.ui.map;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;

import net.wezu.jxg.R;
import net.wezu.jxg.ui.base.BaseViewHolder;

import butterknife.Bind;

public class PoiInfoViewHolder extends BaseViewHolder<PoiInfo> {

    @Bind(R.id.tv_title) TextView tvTitle;
    @Bind(R.id.tv_address) TextView tvAddress;

    public PoiInfoViewHolder(Context context, View view) {
        super(context, view);
    }

    @Override
    public void setData(PoiInfo data) {
        tvTitle.setText(data.name);
        tvAddress.setText(data.address);
    }
}
