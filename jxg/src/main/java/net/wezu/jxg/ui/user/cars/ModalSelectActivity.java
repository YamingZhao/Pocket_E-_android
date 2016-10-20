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
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.CarModal;
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
public class ModalSelectActivity extends BaseActivity {

    @Bind(android.R.id.list) ListView listView;

    BaseListAdapter<CarModal, AreaHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_car_modal_select);

        setTitle("选择车型");
        setDefaultBackButton();

        mAdapter = new BaseListAdapter<CarModal, AreaHolder>(this, R.layout.listitem_brand) {
            @Override
            protected AreaHolder buildViewHolder(Context context, View convertView) {
                return new AreaHolder(ModalSelectActivity.this, convertView);
            }
        };


        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CarModal model = mAdapter.getItem(position);

                ModalSelectActivity.this.setResult(RESULT_OK, new Intent().putExtra("model", model));
                ModalSelectActivity.this.finish();
            }
        });

        CarSeries series = getIntent().getParcelableExtra("series");
        if (series != null && series.CarSeriesId > 0) {
            load(series.CarSeriesId);
        } else {
            ToastUtils.show(this, "数据错误，请先选择品牌");
        }
    }

    private void load(int carSeriesId) {
        Map<String, String> params = new HashMap<>();
            params.put("seriesid", String.valueOf(carSeriesId));



        RequestManager.getInstance().getList("listmodels", requestTag, params, CarModal.class, new RequestManager.ResponseListener<List<CarModal>>() {
            @Override
            public void success(List<CarModal> result, String msg) {
                mAdapter.clear();
                mAdapter.addAll(result);
            }

            @Override
            public void error(String msg) {

            }
        });
    }

    public class AreaHolder extends BaseViewHolder<CarModal> {

        private TextView txtPlateNo;
        RoundedNetImageView image;

        public AreaHolder(Context context, View view) {
            super(context, view);

            txtPlateNo = (TextView) view.findViewById(R.id.txt_brand_name);
            image = (RoundedNetImageView) view.findViewById(R.id.iv_brand_logo);
        }

        @Override
        public void setData(CarModal data) {
            txtPlateNo.setText(data.ModalName);

        }
    }
}
