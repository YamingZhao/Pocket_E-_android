package net.wezu.jxg.service;

import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;

import net.wezu.jxg.app.Application;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.Coupon;
import net.wezu.jxg.model.FavoriteItem;
import net.wezu.jxg.model.FeedBackItem;
import net.wezu.jxg.model.LoginResult;
import net.wezu.jxg.model.UserAddress;
import net.wezu.jxg.model.UserModel;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户相关接口
 *
 * Created by snox on 2015/12/19.
 */
public final class UserService {

    public static void login(String requestTag, String username, String password, RequestManager.ResponseListener<LoginResult> listener) {
        RequestManager.getInstance().login(requestTag, username, password, listener);
    }

    public static void getAddresses(String requestTag,String type, RequestManager.ResponseListener<List<UserAddress>> listener) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("type", type);
//        parameters.put("type", "billing");

        RequestManager.getInstance().getList("getuseraddresses", requestTag, parameters, UserAddress.class, listener);
    }

    public static void changePassword(String requestTag, String oldPassword, String newPassword, RequestManager.ResponseListener<Object> listener) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("oldpassword", oldPassword);
        parameters.put("newpassword", newPassword);

        RequestManager.getInstance().post("changepassword", requestTag, parameters, Object.class, listener);
    }

    public static void getProductCoupons(String requestTag, String expired, String used, RequestManager.ResponseListener<List<Coupon>> listener) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("type", "product");
        parameters.put("expired", expired);
        parameters.put("used", used);

        RequestManager.getInstance().getList("getusercoupons", requestTag, parameters, Coupon.class, listener);
    }

    public static void bind(String cid, RequestManager.ResponseListener<Object> listener) {
        if (TextUtils.isEmpty(cid) || !Application.getInstance().isLogin()) return;

        Map<String, String> param = new HashMap<>();
        param.put("cid", cid);

        RequestManager.getInstance().post("bindcid", null, param, Object.class, listener);
    }

    public static void unbind(RequestManager.ResponseListener<Object> listener) {
        RequestManager.getInstance().post("unbindcid", null, null, Object.class, listener);
    }

    public static void addFavoriteProduct(int id, RequestManager.ResponseListener<FavoriteItem> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("id", String.valueOf(id));
        param.put("type", "product");

        RequestManager.getInstance().post("addfavorite", null, param, FavoriteItem.class, listener);
    }

    public static void getMessages(RequestManager.ResponseListener<MessageResult> listener) {
        RequestManager.getInstance().post("getmessages", null, null, MessageResult.class, listener);
    }

    public static void registerUser(String requestTag, Map<String, String> parameter, RequestManager.ResponseListener<UserModel> listener) {
        register(requestTag, parameter, null, listener);
    }

    public static void register(String requestTag, Map<String, String> parameter, Map<String, File> files, RequestManager.ResponseListener<UserModel> listener) {
        RequestManager.getInstance().upload(requestTag, "register", parameter, files, UserModel.class, listener);
    }

    public static void feedback(String requestTag, String comment, RequestManager.ResponseListener<FeedBackItem> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("comment", comment);
        RequestManager.getInstance().post("postfeedback", requestTag, param, FeedBackItem.class, listener);
    }

    public static void updateLocation(BDLocation location, RequestManager.ResponseListener<Void> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("lat", String.valueOf(location.getLatitude()));
        param.put("lng", String.valueOf(location.getLongitude()));
        RequestManager.getInstance().post("updatelocation", null, param, Void.class, listener);
    }
}
