package net.wezu.jxg.ui.user.cars;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.wezu.framework.util.ToastUtils;
import net.wezu.widget.LabeledTextItem;
import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.CarBrand;
import net.wezu.jxg.model.CarModal;
import net.wezu.jxg.model.CarSeries;
import net.wezu.jxg.model.UserCar;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.widget.MaterialDialog;
import net.wezu.widget.ToggleButton;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 添加车辆
 *
 * @author snox@live.com
 * @date 2015/11/7.
 */
public class UserCarDetailActivity extends BaseActivity {

    @Bind(R.id.btn_select_brand) LabeledTextItem txtBrand;
    @Bind(R.id.btn_select_serial) LabeledTextItem txtSeries;
    @Bind(R.id.btn_select_model) LabeledTextItem txtModel;
    @Bind(R.id.btn_select_year) LabeledTextItem txtYear;
    @Bind(R.id.btn_select_color) LabeledTextItem txtColor;
    @Bind(R.id.tb_isdefault) ToggleButton btnIsDefault;
    @Bind(R.id.edt_plateno) EditText edt_plateno;
    @Bind(R.id.btn_delete_car)
    Button btnDelete;

    private UserCar mCar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_car_detail);

        setTitle("添加车辆");

        setDefaultBackButton();

        mCar = getIntent().getParcelableExtra("car");
        if (mCar == null) {
            mCar = new UserCar();
        } else {
            txtBrand.setValue(mCar.BrandName);
            txtSeries.setValue(mCar.SeriesName);
            txtModel.setValue(mCar.ModalName);
            txtColor.setValue(mCar.Color);
            txtYear.setValue(String.valueOf(mCar.Year));
            edt_plateno.setText(mCar.PlateNo);

            btnDelete.setVisibility(View.VISIBLE);
            if (mCar.isDefault()) {
                btnIsDefault.setToggleOn();
            } else {
                btnIsDefault.setToggleOff();
            }
        }

        btnIsDefault.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                mCar.IsDefault = on ? 1 : 0;
            }
        });

    }

    private CarBrand brand;
   // private CarBrand brand1;
    private CarSeries series;
    private CarModal model;

    private static final int REQUEST_BRAND = 1;
    private static final int REQUEST_SERIAL = 2;
    private static final int REQUEST_MODEL = 3;
    private static final int REQUEST_COLOR = 4;

    /**
     * 选择品牌
     */
    @OnClick(R.id.btn_select_brand) void selectBrand() {
        startActivityForResult(new Intent(this, BrandSelectActivity.class), REQUEST_BRAND);
    }

    /**
     * 选择车系
     */
    @OnClick(R.id.btn_select_serial) void selectSerial() {
        if (brand == null) {
            ToastUtils.show(this, "请先选择品牌");
            return;
        }
        startActivityForResult(new Intent(this, SeriesSelectActivity.class).putExtra("brand", brand), REQUEST_SERIAL);
    }

    /**
     * 选择车型
     */
    @OnClick(R.id.btn_select_model) void selectModel() {
        if (series == null) {
            ToastUtils.show(this, "请先选择车系");
            return;
        }

        startActivityForResult(new Intent(this, ModalSelectActivity.class).putExtra("series", series), REQUEST_MODEL);
    }

    /**
     * 选择颜色
     */
    @OnClick(R.id.btn_select_color) void selectColor() {
        startActivityForResult(new Intent(this, ColorSelectActivity.class), REQUEST_COLOR);
    }

    /**
     * 提交
     */
    @OnClick(R.id.btn_submit_car) void submitCar() {

        if (mCar.CarModelId <= 0) {

            if (brand == null) {
                ToastUtils.show(this, "请选择品牌");
                return;
            }
            if (series == null) {
                ToastUtils.show(this, "请选择车系");
                return;
            }
            if (model == null) {
                ToastUtils.show(this, "请选择车型");
                return;
            }

            return;
        }

        if (TextUtils.isEmpty(mCar.Color)) {
            ToastUtils.show(this, "请选择颜色");
            return;
        }

        String plateNo = edt_plateno.getText().toString();
        if (TextUtils.isEmpty(plateNo)
                // http://www.blogjava.net/weiwei/articles/401703.html
                || !plateNo.matches("(^[\u4E00-\u9FA5]{1}[A-Z0-9]{6}$)|(^[A-Z]{2}[A-Z0-9]{2}[A-Z0-9\u4E00-\u9FA5]{1}[A-Z0-9]{4}$)|(^[\u4E00-\u9FA5]{1}[A-Z0-9]{5}[挂学警军港澳]{1}$)|(^[A-Z]{2}[0-9]{5}$)|(^(08|38){1}[A-Z0-9]{4}[A-Z0-9挂学警军港澳]{1}$)")) {
            ToastUtils.show(this, "请正确填写车牌");
            return;
        }

        Map<String, String> params = new HashMap<>();

        if (mCar.UserCarId > 0) {
            params.put("id", String.valueOf(mCar.UserCarId));
        }
        params.put("plateno", plateNo);
        params.put("carmodelid", String.valueOf(mCar.CarModelId));
        params.put("color", mCar.Color);
        params.put("isdefault", String.valueOf(mCar.IsDefault));

        RequestManager.getInstance().post("addusercar", requestTag, params, UserCar.class, new RequestManager.ResponseListener<UserCar>() {

            @Override
            public void success(UserCar result, String msg) {

                ToastUtils.show(UserCarDetailActivity.this, "保存成功");

                UserCarDetailActivity.this.setResult(RESULT_OK);
                UserCarDetailActivity.this.finish();
            }

            @Override
            public void error(String msg) {
                ToastUtils.show(UserCarDetailActivity.this, "保存失败");
            }
        });
    }

    @OnClick(R.id.btn_delete_car) void deleteCar() {
        if (mCar.UserCarId <= 0) {
            ToastUtils.show(this, "删除错误了。。。。我不该出现");
        }

        new MaterialDialog(this)
                .setTitle("删除确认")
                .setMessage("您确认需要删除 " + mCar.ModalName + " (" + mCar.BrandName + ") 吗？" )
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", String.valueOf(mCar.UserCarId));

                        RequestManager.getInstance().post("deleteusercar", requestTag, params, Object.class, new RequestManager.ResponseListener<Object>() {

                            @Override
                            public void success(Object result, String msg) {

                                ToastUtils.show(UserCarDetailActivity.this, "删除成功");

                                UserCarDetailActivity.this.setResult(RESULT_OK);
                                UserCarDetailActivity.this.finish();
                            }

                            @Override
                            public void error(String msg) {
                                ToastUtils.show(UserCarDetailActivity.this, "删除失败");
                            }
                        });
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_BRAND:
                //brand = data.getParcelableExtra("brand1");
                brand = data.getParcelableExtra("brand");
                txtBrand.setValue(brand.BrandName);
                mCar.BrandName = brand.BrandName;
                mCar.CarModelId = 0;

                series = null;
                model = null;
                txtSeries.setValue("");
                txtModel.setValue("");
                txtYear.setValue("");

                break;

            case REQUEST_SERIAL:
                series = data.getParcelableExtra("series");
                txtSeries.setValue(series.SeriesName);
                mCar.CarModelId = 0;

                model = null;
                txtModel.setValue("");
                txtYear.setValue("");
                break;

            case REQUEST_MODEL:
                model = data.getParcelableExtra("model");
                txtModel.setValue(model.ModalName);
                txtYear.setValue(String.valueOf(model.Year));
                mCar.CarModelId = model.CarModalId;
                break;

            case REQUEST_COLOR:
                mCar.Color = data.getStringExtra("color");
                txtColor.setValue(mCar.Color);
                break;
        }
    }
}
