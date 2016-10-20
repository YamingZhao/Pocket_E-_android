package net.wezu.jxg.app;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.igexin.sdk.PushManager;
import com.tencent.bugly.crashreport.CrashReport;

import net.wezu.framework.util.ToastUtils;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.LoginResult;
import net.wezu.jxg.model.UserAddress;
import net.wezu.jxg.model.UserModel;
import net.wezu.jxg.service.LocationReportService;
import net.wezu.jxg.service.UserService;

import butterknife.ButterKnife;

/**
 * @author snox@live.com
 * @date 2015/10/21.
 */
public class Application extends net.wezu.framework.app.Application {

    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    private boolean isWorkerPackage;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        setUserAvatarUpdated();

        // 网络请求初始化
        RequestManager.getInstance().init(this);

        CrashReport.initCrashReport(getApplicationContext(), "900014630", false);

        ButterKnife.setDebug(false);//BuildConfig.DEBUG);

        SDKInitializer.initialize(this);

        // 个推初始化
        PushManager.getInstance().initialize(this.getApplicationContext());

        isWorkerPackage = getIsWorkerPackage();

        if (Application.getInstance().isWorkerPackage()) {
            startService(new Intent(this, LocationReportService.class));
        }
    }

    @Override
    public void onTerminate() {
        if (Application.getInstance().isWorkerPackage()) {
            stopService(new Intent(this, LocationReportService.class));
        }

        super.onTerminate();
    }

    private BDLocation location;

    public BDLocation getLocation() {
        return location;
    }

    public LatLng getLatLng() {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void setLocation(BDLocation location) {
        this.location = location;

        if (isWorkerPackage() && isLogin()) {
            UserService.updateLocation(location, new RequestManager.ResponseListener<Void>() {
                @Override
                public void success(Void result, String msg) {
                    //Toast.makeText(this, "UPDATE LOCATION SUCCESS", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void error(String msg) {
                    //Toast.makeText(getApplicationContext(), "UPDATE LOCATION Fail", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        ToastUtils.show(this, "Low Memory");
    }

    /**
     * 获取个推的用户ID
     * @return 个推的用户ID
     */
    public String getCid() {
        return PushManager.getInstance().getClientid(getApplicationContext());
    }

    private LoginResult loginResult;

    public UserModel getUserModel() {
        return loginResult.getUserModel();
    }

    private boolean isWorker;

    public void setLoginResult(LoginResult loginModel) {
        this.loginResult = loginModel;
        isWorker = loginModel != null && loginModel.getUserModel().Roles.contains("Worker");
    }

    public void setUserModel(UserModel userModel) {
        if (isLogin())
            loginResult.setUserModel(userModel);
    }

    public void setUserAddress(UserAddress address) {
        if (isLogin())
            loginResult.setCompanyAddress(address);
    }

    public UserAddress getUserAddress() {
        return loginResult.getCompanyAddress();
    }

    public boolean isLogin() {
        return loginResult !=null;
    }

    public String getUserAvatar() {
        if(TextUtils.isEmpty(loginResult.getUserModel().Profile.Website))
            return "";
        else
            return loginResult.getUserModel().Profile.Website + "#t=" + lastrandom;
    }

    private long lastrandom;

    public void setUserAvatarUpdated() {
        lastrandom = System.currentTimeMillis();
    }

    /**
     * 当前登录用户是否为机修工
     * @return
     */
    public boolean isWorker() {
        return isWorker;
    }

    public boolean isValid() {
        return isWorker && isWorkerPackage || !isWorker && !isWorkerPackage;
    }

    public boolean isWorkerPackage() {
        return isWorkerPackage;
    }

    private boolean getIsWorkerPackage() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("is_worker").equals("worker");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private int currentViewOrderId;

    public int getCurrentViewOrderId() {
        return currentViewOrderId;
    }

    public void setCurrentViewOrderId(int currentViewOrderId) {
        this.currentViewOrderId = currentViewOrderId;
    }
}
