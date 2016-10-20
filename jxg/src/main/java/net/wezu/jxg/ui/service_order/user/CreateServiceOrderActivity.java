package net.wezu.jxg.ui.service_order.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.R;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.OrderEntity;
import net.wezu.jxg.model.Product;
import net.wezu.jxg.model.ServicesAndCars;
import net.wezu.jxg.model.UserCar;
import net.wezu.jxg.service.ServiceOrderService;
import net.wezu.jxg.ui.SelectPictureDialog;
import net.wezu.jxg.ui.base.BaseActivity;
import net.wezu.jxg.ui.map.AddressInformation;
import net.wezu.jxg.ui.map.BaiduMapPoiSearchActivity;
import net.wezu.jxg.ui.payment.WXUtil;
import net.wezu.jxg.ui.service_order.ServiceOrderDetailActivity;
import net.wezu.jxg.util.FastClickUtil;
import net.wezu.jxg.util.NumicUtil;
import net.wezu.widget.RoundImageview.RoundedImageView;
import net.wezu.widget.dialog.ChoiceDialog;
import net.wezu.widget.dialog.CustomProgressDialog;
import net.wezu.widget.dialog.DialogUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by i310736(Yaming.Zhao@sap.com) on 9/10/2016.
 * 创建服务订单，二级页面（点击某个功能点后进入的创建订单页面）
 */

public class CreateServiceOrderActivity extends BaseActivity {

    private static final String SERVICE_TYPE = "service_type";
    private static final String CATEGORY_NAME = "category_name";
    //private static final String CATEGORY_DESCRIPTION = "category_desc";
    private static final String CAR_AND_PRODUCT = "data";

    private static final int REQUST_LOCATION = 4;

    /**
     * 预约服务
     */
    public static final int ST_PREORDER = 1;
    public static final int ST_SERVICE  = 2;

    private int mServiceType;

    private String mCategoryName;

    private LocationClient locationClient;
    //private BDLocation location;

    @Bind(R.id.tv_description)
    TextView txtDescription;
    @Bind(R.id.txt_province) TextView txtProvince;
    @Bind(R.id.tv_city) TextView txtCity;
    @Bind(R.id.txt_district) TextView txtDistrict;
    @Bind(R.id.txt_street) TextView txtStreet;
    @Bind(R.id.txt_poi) TextView txtStreet2;

    @Bind(R.id.txt_user_car) TextView txtUserCar;
    @Bind(R.id.txt_product) TextView txtProduct;

    @Bind(R.id.txt_amount) TextView txtAmount;

    @Bind(R.id.ll_service_date)
    View layoutServiceDate;
    @Bind(R.id.txt_service_date) TextView txtServiceDate;

    @Bind(R.id.txt_remark)
    EditText edtRemark;

    @Bind(R.id.image_upload_1)
    RoundedImageView imageUpload1;
    @Bind(R.id.image_upload_2) RoundedImageView imageUpload2;
    @Bind(R.id.image_upload_3) RoundedImageView imageUpload3;

    private UserCar mCurrentCar;
    private Product mProduct;

    private Date mServiceDate;

    public static void createService(final Activity activity, final int serviceType, final String categoryName) {


        ServiceOrderService.getServicesAndCars(categoryName, new RequestManager.ResponseListener<ServicesAndCars>() {
            @Override
            public void success(ServicesAndCars result, String msg) {
//                Map<String, String> services = new HashMap<>();
//
//                // 紧急类
//                services.put("换胎", "请把车停在安全地区，把警示牌放在车后10米处，呼叫前请确认备胎完好（如无备胎请提前告知）");
//                services.put("帮电", "请把车停在安全地区，把警示牌放在车后10米处，呼叫前请尽量把车头放置在空旷处方便搭电瓶线");
//                services.put("送油", "请把车停在安全地区，把警示牌放在车后10米处，呼叫前请确认需要的油品种类");
//                services.put("开锁", "请把车停在安全地区，把警示牌放在车后10米处");
//                services.put("事故咨询", "请把车停在安全地区，把警示牌放在车后10米处");
//                services.put("补胎", "请把车停在安全地区，把警示牌放在车后10米处");
//                services.put("拖车", "请把车停在安全地区，把警示牌放在车后10米处，呼叫前请确认车辆故障程度以及位置");
//                services.put("其他", "请把车停在安全地区，把警示牌放在车后10米处，呼叫前请尽量清楚的观察好故障部位以及故障现象");
//
//
//                // 预约类
//                services.put("保养", "请将车辆停在空旷安全地区");
//                services.put("检查", "请将车辆停在空旷安全地区");
//                services.put("配钥匙", "请将车辆停在空旷安全地区 如果有钥匙源码（新车时候的一个塑料片上有一窜号码）请准备号");
//                services.put("喷漆", "此业务由于设备需求，请预约后到指定地点联系工人");
//                services.put("改装", "此业务由于设备需求，请预约后到指定地点联系工人");
//                services.put("代办", "请在办理业务前和业务员确认所需材料原件复印件");
//
//
//
//                String description = services.containsKey(categoryName) ? services.get(categoryName) : "";

                activity.startActivity(new Intent(activity, CreateServiceOrderActivity.class)
                        .putExtra(CreateServiceOrderActivity.SERVICE_TYPE, serviceType)
                        .putExtra(CreateServiceOrderActivity.CATEGORY_NAME, categoryName)
                        .putExtra(CreateServiceOrderActivity.CAR_AND_PRODUCT, result));
                //.putExtra(CreateServiceOrderActivity.CATEGORY_DESCRIPTION, description)
            }

            @Override
            public void error(String msg) {
                try {
                    int oid = Integer.parseInt(msg);

                    ServiceOrderDetailActivity.showOrder(activity, oid);

                } catch (Exception e) {
                    ToastUtils.show(activity, msg);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_service_order);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(IMAGE_FILE_1)) image1 = savedInstanceState.getString(IMAGE_FILE_1);
            if (savedInstanceState.containsKey(IMAGE_FILE_2)) image2 = savedInstanceState.getString(IMAGE_FILE_2);
            if (savedInstanceState.containsKey(IMAGE_FILE_3)) image3 = savedInstanceState.getString(IMAGE_FILE_3);
        }

        Intent intent = getIntent();

        mServiceType = intent.getIntExtra(SERVICE_TYPE, ST_SERVICE);

        mCategoryName = intent.getStringExtra(CATEGORY_NAME);
        if (TextUtils.isEmpty(mCategoryName)) {
            throw new IllegalArgumentException("服务类型不能为空");
        }

//        String description = intent.getStringExtra(CATEGORY_DESCRIPTION);
//        if (TextUtils.isEmpty(description)) {
//            txtDescription.setVisibility(View.GONE);
//        } else {
//            txtDescription.setVisibility(View.VISIBLE);
//            txtDescription.setText(description);
//        }

        layoutServiceDate.setVisibility(mServiceType == ST_PREORDER ? View.VISIBLE : View.GONE);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 2);
        mServiceDate = cal.getTime();
        updateServiceDate();

        setTitle(mCategoryName);
        setDefaultBackButton();

        imageUpload1.setImageResource(R.mipmap.icon_upload_default);
        imageUpload2.setImageResource(R.mipmap.icon_upload_default);
        imageUpload3.setImageResource(R.mipmap.icon_upload_default);

        if (!TextUtils.isEmpty(image1)) setImageBitmap(imageUpload1, image1);
        if (!TextUtils.isEmpty(image2)) setImageBitmap(imageUpload2, image2);
        if (!TextUtils.isEmpty(image3)) setImageBitmap(imageUpload3, image3);

        selectPictureDialog = new SelectPictureDialog(this, new SelectPictureDialog.OnGetImageFile() {
            @Override
            public void onGet(String imageFilePath) {
                switch (selectPictureDialog.getId()) {
                    case R.id.image_upload_1:
                        image1 = imageFilePath;
                        setImageBitmap(imageUpload1, image1);
                        break;
                    case R.id.image_upload_2:
                        image2 = imageFilePath;
                        setImageBitmap(imageUpload2, image2);
                        break;
                    case R.id.image_upload_3:
                        image3 = imageFilePath;
                        setImageBitmap(imageUpload3, image3);
                        break;
                }

                if (isStarted && locationClient != null) locationClient.start();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(IMAGEFILEPATH)) {
            if (selectPictureDialog != null) {
                selectPictureDialog.setId(savedInstanceState.getInt(IMAGEFILEID));
                selectPictureDialog.setCameraFile(new File(savedInstanceState.getString(IMAGEFILEPATH)));
            }
        }

        //  定位相关 --  Start

        locationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedLocationPoiList(true);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);

        locationClient.setLocOption(option);

        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {

                updateLocation(bdLocation);
            }
        });

        locationClient.start();

        BDLocation location = locationClient.getLastKnownLocation();
        updateLocation(location);

        //locationClient.requestLocation();

        //  定位相关 --  End

        ServicesAndCars result = getIntent().getParcelableExtra(CAR_AND_PRODUCT);

        cars = result.cars;
        products = result.products;

        updateCurrentCar(cars.get(0));
        updateProduct(products.get(0));
    }

    private void updateLocation(BDLocation location) {
        if (location == null) return;

        //updateLatLng(location.getLatitude(), location.getLongitude());

        setAddressInformation(AddressInformation.fromBdLocation(location));
    }

    //private LatLng position;

    private void updateLatLng(double lat, double lng) {
        updateLatLng(new LatLng(lat, lng));
    }

    private void updateLatLng(final LatLng position) {
        //this.position = position;

        final GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                    toast("定位失败");
                    finish();
                    return;
                }
                //获取反向地理编码结果

                setAddressInformation(AddressInformation.fromReverseGeoCodeResult(position, result));

                geoCoder.destroy();
            }
        });
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(position));
    }

    private AddressInformation addressInformation;

    /**
     * 设置地址信息
     * @param addressInformation
     */
    private void setAddressInformation(AddressInformation addressInformation) {
        this.addressInformation = addressInformation;

        if (txtCity != null) txtCity.setText(addressInformation.city);
        if (txtProvince != null) txtProvince.setText(addressInformation.province);
        if (txtDistrict != null) txtDistrict.setText(addressInformation.district);
        if (txtStreet != null) txtStreet.setText(addressInformation.street);
        if (txtStreet2 != null) txtStreet2.setText(addressInformation.name);
    }

    private List<Product> products;
    private List<UserCar> cars;

//    /**
//     * 加载服务分类
//     */
//    private void loadProducts() {
//        Map<String, String> params = new HashMap<>();
//        params.put("type", "service");
//        params.put("category", mCategoryName);
//
//        RequestManager.getInstance().getList("searchservice", requestTag, params, Product.class, new RequestManager.ResponseListener<List<Product>>() {
//            @Override
//            public void success(List<Product> result, String msg) {
//                products = result;
//                if (result.size() > 0) {
//                    updateProduct(result.get(0));
//                } else {
//                    ToastUtils.show(CreateServiceOrderActivity.this, "该类别下没有服务");
//                }
//            }
//
//            @Override
//            public void error(String msg) {
//
//            }
//        });
//    }

    /**
     * 加载用户的车辆列表
     */
//    private void loadCars() {
//        RequestManager.getInstance().getCars(requestTag, new RequestManager.ResponseListener<List<UserCar>>() {
//            @Override
//            public void success(List<UserCar> result, String msg) {
//                cars = result;
//                if (result.size() > 0) {
//                    updateCurrentCar(result.get(0));
//                } else {
//                    ToastUtils.show(CreateServiceOrderActivity.this, "用户没有注册车辆");
//                }
//            }
//
//            @Override
//            public void error(String msg) {
//                ToastUtils.show(CreateServiceOrderActivity.this, msg);
//            }
//        });
//    }

    private void updateCurrentCar(UserCar car) {
        mCurrentCar = car;
        if (txtUserCar != null) {
            txtUserCar.setText(car.BrandName + car.ModalName);
        }
    }

    @OnClick({R.id.txt_change_user_car, R.id.txt_user_car}) void changeCar() {

        if (FastClickUtil.isFastClick()) return;

        if (cars != null && cars.size() > 0) {
            String[] txtCars = new String[cars.size()];
            for (int i = 0; i < cars.size(); i++) {
                txtCars[i] = cars.get(i).ModalName;
            }

            DialogUtil.showChoiceDialog(this, txtCars, null, new ChoiceDialog.OnDialogItemClickListener() {
                @Override
                public void click(int position) {
                    updateCurrentCar(cars.get(position));
                }
            });
        }
    }

    private void updateProduct(Product product) {
        mProduct = product;
        if (txtProduct != null) {
            txtProduct.setText(product.ProductName);
            txtDescription.setText(product.Description);
            txtAmount.setText(NumicUtil.formatDouble(product.Price));// product.Price.toString());
        }
    }

    @Override
    protected void onDestroy() {
        if (locationClient != null) {
            if (locationClient.isStarted())
                locationClient.stop();
            locationClient = null;
        }
        super.onDestroy();
    }

    /**
     * 更换位置
     */
    @OnClick(R.id.btn_change_location) void changeLocation() {
        if (FastClickUtil.isFastClick()) return;

        //startActivityForResult(new Intent(this, BaiduMapActivity.class).putExtra("latitude", addressInformation.latitude).putExtra("longitude", addressInformation.longitude), REQUST_LOCATION);
        BaiduMapPoiSearchActivity.startActivityForResult(this, addressInformation, REQUST_LOCATION);
    }

    @OnClick({R.id.txt_change_product, R.id.txt_product}) void changeProduct() {
        if (FastClickUtil.isFastClick()) return;

        if (products != null && products.size() > 0) {
            String[] txtCars = new String[products.size()];
            for (int i = 0; i < products.size(); i++) {
                txtCars[i] = products.get(i).ProductName;
            }

            DialogUtil.showChoiceDialog(this, txtCars, null, new ChoiceDialog.OnDialogItemClickListener() {
                @Override
                public void click(int position) {
                    updateProduct(products.get(position));
                }
            });
        }
    }

    /**
     * 提交订单
     */
    @OnClick(R.id.btn_submit) void submitOrder() {

        if (FastClickUtil.isFastClick()) return;

        if (mProduct == null) {
            ToastUtils.show(this, "请选择服务项目");
            return;
        }

        if (mCurrentCar == null) {
            ToastUtils.show(this, "请选择车辆");
            return;
        }

        if (addressInformation == null) {
            ToastUtils.show(this, "请选择地址");
            return;
        }

        Map<String, String> orderParams = new HashMap<>();
        orderParams.put("productid", String.valueOf(mProduct.ProductId));
        orderParams.put("carmodalid", String.valueOf(mCurrentCar.CarModelId));
        orderParams.put("lng", String.valueOf(addressInformation.longitude));
        orderParams.put("lat", String.valueOf(addressInformation.latitude));

        if (mServiceType == ST_PREORDER) {
            orderParams.put("servicetime", txtServiceDate.getText().toString());
        }
        orderParams.put("plateno", mCurrentCar.PlateNo);
        orderParams.put("destination", ""); //fixme 目的地，某些服务需要。。。。
        orderParams.put("remark", edtRemark.getText().toString());
        orderParams.put("tipfee", "0");

        orderParams.put("location", addressInformation.toLocation());//地点

        Map<String, File> files = new HashMap<>();
        if (!TextUtils.isEmpty(image1)) {
            files.put("image1", new File(image1));
        }
        if (!TextUtils.isEmpty(image2)) {
            files.put("image2", new File(image2));
        }
        if (!TextUtils.isEmpty(image3)) {
            files.put("image3", new File(image3));
        }

        final CustomProgressDialog progress = CustomProgressDialog.getDialog(this);
        progress.setMessage("正在创建订单");
        progress.show();

        ServiceOrderService.create(requestTag, orderParams, files, new RequestManager.ResponseListener<OrderEntity>() {

            @Override
            public void success(OrderEntity result, String msg) {
                showOrder(result);
                progress.dismiss();
            }

            @Override
            public void error(String msg) {
                toast(msg);
                progress.dismiss();
            }
        });
    }

    private void showOrder(OrderEntity entity) {
        ServiceOrderDetailActivity.showOrder(this, entity.order.OrderId);
        finish();
    }

    /**
     * 选择预约时间
     */
    @OnClick(R.id.ll_service_date) void selectServiceDate() {

        if (FastClickUtil.isFastClick()) return;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        List<Date> dates = new ArrayList<>();

        for (int i = 2; i < 40; i++) {
            calendar.add(Calendar.HOUR, 1);

            int hour = calendar.get(Calendar.HOUR);
        }

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mServiceDate.setYear(year - 1900);
                mServiceDate.setMonth(monthOfYear);
                mServiceDate.setDate(dayOfMonth);

                updateServiceDate();
            }
        }, mServiceDate.getYear() + 1900, mServiceDate.getMonth(), mServiceDate.getDate());

        dialog.show();
    }

    private void updateServiceDate() {
        txtServiceDate.setText(new SimpleDateFormat("yyyy-MM-dd hh:00:00").format(mServiceDate));
    }

    private void setImageBitmap(ImageView image, String path) {

        Bitmap bitmap = WXUtil.extractThumbNail(path, 800, 800, true);
        if (bitmap != null) {
            image.setImageBitmap(bitmap);
        }
    }

    private PopupWindow popupWindow;

    private SelectPictureDialog selectPictureDialog;

    private String image1;
    private String image2;
    private String image3;

    private boolean isStarted;

    @OnClick({R.id.image_upload_1, R.id.image_upload_2, R.id.image_upload_3})
    void uploadImage(final View view) {

        if (FastClickUtil.isFastClick()) return;

        isStarted = locationClient.isStarted();
        if (isStarted) locationClient.stop();

        selectPictureDialog.setId(view.getId());
        selectPictureDialog.show(view);
    }

    public static final String IMAGEFILEPATH = "ImageFilePath";
    public static final String IMAGEFILEID = "ImageFileID";
    public static final String LOCATION_STARTED = "location_is_started";
    public static final String IMAGE_FILE_1 = "IMAGE_FILE_1";
    public static final String IMAGE_FILE_2 = "IMAGE_FILE_2";
    public static final String IMAGE_FILE_3 = "IMAGE_FILE_3";

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(IMAGEFILEPATH)) {
            if (selectPictureDialog != null) {
                selectPictureDialog.setId(savedInstanceState.getInt(IMAGEFILEID));
                selectPictureDialog.setCameraFile(new File(savedInstanceState.getString(IMAGEFILEPATH)));
            }
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (selectPictureDialog != null && selectPictureDialog.getCameraFile() != null) {
            outState.putBoolean(LOCATION_STARTED, isStarted);
            outState.putInt(IMAGEFILEID, selectPictureDialog.getId());
            outState.putString(IMAGEFILEPATH, selectPictureDialog.getCameraFile().getPath());

            if (!TextUtils.isEmpty(image1)) outState.putString(IMAGE_FILE_1, image1);
            if (!TextUtils.isEmpty(image2)) outState.putString(IMAGE_FILE_2, image2);
            if (!TextUtils.isEmpty(image3)) outState.putString(IMAGE_FILE_3, image3);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        if (selectPictureDialog != null) {
            selectPictureDialog.onActivityResult(requestCode, data);
        }

        switch (requestCode) {
            case REQUST_LOCATION:

                locationClient.stop();

                AddressInformation addressInformation = data.getParcelableExtra(BaiduMapPoiSearchActivity.ADDRESS_FIELD);
                setAddressInformation(addressInformation);
                break;
        }
    }

}
