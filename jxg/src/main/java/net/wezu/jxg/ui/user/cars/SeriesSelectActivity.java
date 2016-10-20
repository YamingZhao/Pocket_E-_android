package net.wezu.jxg.ui.user.cars;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.wezu.framework.util.ToastUtils;
import net.wezu.widget.RoundImageview.RoundedNetImageView;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.CarBrand;
import net.wezu.jxg.model.CarSeries;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.base.BaseListAdapter;
import net.wezu.jxg.ui.base.BaseViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * @author snox@live.com
 * @date 2015/11/8.
 */
public class SeriesSelectActivity extends BaseActivity {

    @Bind(android.R.id.list)
    ListView listView;

    private BaseListAdapter<CarSeries,  AreaHolder> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common_list_select);

        setTitle("车型选择");
        setDefaultBackButton();

        mAdapter = new BaseListAdapter<CarSeries, AreaHolder>(this, R.layout.listitem_brand) {
            @Override
            protected AreaHolder buildViewHolder(Context context, View convertView) {
                return new AreaHolder(SeriesSelectActivity.this, convertView);
            }
        };


        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CarSeries model = mAdapter.getItem(position);

                SeriesSelectActivity.this.setResult(RESULT_OK, new Intent().putExtra("series", model));
                SeriesSelectActivity.this.finish();
            }
        });

        CarBrand brand = getIntent().getParcelableExtra("brand");
        if (brand != null && brand.CardBrandId > 0) {
            load(brand.CardBrandId);
        } else {
            ToastUtils.show(this, "数据错误，请先选择品牌");
        }
    }

    private void load(int cardBrandId) {
        Map<String, String> params = new HashMap<>();
        params.put("brandid", String.valueOf(cardBrandId));

        RequestManager.getInstance().getList("listseries", requestTag, params, CarSeries.class, new RequestManager.ResponseListener<List<CarSeries>>() {
            @Override
            public void success(List<CarSeries> result, String msg) {
                mAdapter.clear();
                mAdapter.addAll(result);
            }

            @Override
            public void error(String msg) {

            }
        });
    }

    public class AreaHolder extends BaseViewHolder<CarSeries> {

        private TextView txtPlateNo;
        RoundedNetImageView image;

        public AreaHolder(Context context, View view) {
            super(context, view);

            txtPlateNo = (TextView) view.findViewById(R.id.txt_brand_name);
            image = (RoundedNetImageView) view.findViewById(R.id.iv_brand_logo);
        }

        @Override
        public void setData(CarSeries data) {
            txtPlateNo.setText(data.SeriesName);
            image.setImageUrl(data.SeriesLogo, Application.getInstance().getImageLoader());
        }
    }
}
