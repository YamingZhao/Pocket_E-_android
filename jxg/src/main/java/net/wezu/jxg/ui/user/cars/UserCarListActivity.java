package net.wezu.jxg.ui.user.cars;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.wezu.jxg.ui.base.BaseListActivity;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.widget.RoundImageview.RoundedNetImageView;
import net.wezu.jxg.R;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.UserCar;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.base.BaseListAdapter;
import net.wezu.jxg.ui.base.BaseViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author snox@live.com
 * @date 2015/10/24.
 */
public class UserCarListActivity extends BaseListActivity<UserCar, UserCarListActivity.UserCarViewHolder> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("用户车辆列表");

        setDefaultBackButton();

        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetail(getItem(position));
            }
        });
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_user_car_list);
    }

    @Override
    protected boolean equalItem(UserCar item1, UserCar item2) {
        return false;
    }

    @Override
    protected int getListItemLayoutResourceId() {
        return R.layout.listitem_user_car;
    }

    @Override
    protected UserCarViewHolder createViewHolder(Context context, View convertView) {
        return new UserCarViewHolder(context, convertView);
    }


    @Override
    protected void refreshData() {
        getLoadingDialog("正在加载用户车辆信息").show();

        RequestManager.getInstance().getCars(requestTag, new RequestManager.ResponseListener<List<UserCar>>() {
            @Override
            public void success(List<UserCar> result, String msg) {

                clear();

                addDataItems(result);

                dismissLoadingDialog();
            }

            @Override
            public void error(String msg) {

                dismissLoadingDialog();
                setRefreshing(false);
            }
        });
    }

    private static final int REQUEST_CAR_DETAIL = 99;

    /**
     * 添加新车
     */
    @Nullable
    @OnClick(R.id.btn_add_new_car) void addNewCar() {
        if (FastClickUtil.isFastClick()) return;

        startActivityForResult(new Intent(this, UserCarDetailActivity.class), REQUEST_CAR_DETAIL);
    }

    private void showDetail(UserCar car) {
        startActivityForResult(new Intent(this, UserCarDetailActivity.class).putExtra("car", car), REQUEST_CAR_DETAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        refreshData();
    }

    public class UserCarViewHolder extends BaseViewHolder<UserCar> {

        @Bind(R.id.iv_car_image) RoundedNetImageView imageView;
        @Bind(R.id.tv_model) TextView txtBrandName;
        @Bind(R.id.et_plate) TextView txtPlateNo;
        @Bind(R.id.lbl_is_default) TextView tvIsDefault;

        public UserCarViewHolder(Context context, View view) {
            super(context, view);
        }

        @Override
        public void setData(UserCar data) {
            imageView.setImageUrl(data.SeriesLogo, Application.getInstance().getImageLoader());

            txtBrandName.setText(data.BrandName + " " + data.SeriesName);
//            txtSeries.setText(data.SeriesName);
//            txtYear.setText(String.valueOf(data.Year));
//            txtModel.setText(data.ModalName);
//            txtColor.setText(data.Color);
            txtPlateNo.setText(data.PlateNo);
            tvIsDefault.setVisibility(data.isDefault() ? View.VISIBLE : View.GONE);
        }
    }
}
