package net.wezu.jxg.data;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.wezu.framework.util.PictureUtil;
import net.wezu.framework.util.ToastUtils;
import net.wezu.framework.volley.MultipartRequest.MultipartRequest;
import net.wezu.framework.volley.MultipartRequest.MultipartRequestParams;
import net.wezu.framework.volley.VolleyErrorHelper;
import net.wezu.jxg.app.Application;
import net.wezu.jxg.model.LoginResult;
import net.wezu.jxg.service.ServiceAddressConstant;
import net.wezu.jxg.model.UserCar;
import net.wezu.jxg.util.DES;
import net.wezu.jxg.util.FormatUtil;

import org.androidpn.client.XmppManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP 请求管理器
 *
 * @author snox@live.com
 * @date 2015/10/22.
 */
public class RequestManager {
    private static RequestManager sInstance = new RequestManager();

    private RequestManager() { }

    public static RequestManager getInstance() {
        return sInstance;
    }

    private RequestQueue mRequestQueue;
    private Context mContext;

    public void init(Context context) {
        mRequestQueue = Application.getInstance().getRequestQueue(); //Volley.newRequestQueue(context);
        mContext = context;
    }

    private String _token;

    public void login(String requestTag, final String username, final String password, final ResponseListener<LoginResult> listener) {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("username", username);
        parameter.put("password", password);
        parameter.put("method", "login");
        if (Application.getInstance().isWorkerPackage()) {
            parameter.put("role", "Worker");
        }

        final String tempToken = username + "," + password;

        LoginRequest request = new LoginRequest(Request.Method.POST,
                ServiceAddressConstant.BASE_ADDRESS, encodeParameters(parameter),
                new Response.Listener<LoginResult>() {
                    @Override
                    public void onResponse(LoginResult response) {
                        //token = tempToken;
                        updateToken(username, password);

                        if (listener != null) {
                            listener.success(response, "");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null) {
                            listener.error(VolleyErrorHelper.getMessage(error));
                        }
                    }
                });

        addRequest(request, requestTag);
    }

    private String username;

    public void updateToken(String username, String password) {
        this.username = username;
        updateToken(password);
    }

    public void updateToken(String password) {
        this._token = username + "," + password;
    }

    public String getToken() {
        return _token;
    }

    public String getEncryptedToken() {
        if (TextUtils.isEmpty(_token)) {
            return null;
        } else {
            String source = _token + "," + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(System.currentTimeMillis()));
            return DES.encryptDES(source);
        }
    }

    public void getCars(String requestTag, ResponseListener<List<UserCar>> listener) {
        getList("getcars", requestTag, null, UserCar.class, listener);
    }

    public <T> void getList(String method, String requestTag, Map<String, String> parameter, final Class<T> cls, final ResponseListener<List<T>> listener) {

        if (parameter == null)
            parameter = new HashMap<>();

        parameter.put("method", method);

        String token = getEncryptedToken();

        if (!parameter.containsKey("token") && !TextUtils.isEmpty(token)) {
            parameter.put("token", token);
        }

        UispRequest request = new UispRequest(Request.Method.POST,
                ServiceAddressConstant.BASE_ADDRESS, encodeParameters(parameter),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

                        try {
                            List<T> data = gson.fromJson(response, type(List.class, cls));

                            listener.success(data, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.error(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.error(VolleyErrorHelper.getMessage(error));
                    }
                });

        addRequest(request, requestTag);
    }

    public <T> void getPagedList(String method, String requestTag, Map<String, String> parameter, final Class<T> cls, final ResponseListener<PagedResult<T>> listener) {
        getPagedList(method, -1, 0, requestTag, parameter, cls, listener);
    }

    public <T> void getPagedList(String method, int start, int size, String requestTag, Map<String, String> parameter, final Class<T> cls, final ResponseListener<PagedResult<T>> listener) {

        if (parameter == null)
            parameter = new HashMap<>();



        parameter.put("method", method);
        if (start >= 0) {
            int end = start + size;

            parameter.put("start", String.valueOf(start));
            parameter.put("end", String.valueOf(end));
        }

        String token = getEncryptedToken();

        if (!TextUtils.isEmpty(token)) {
            parameter.put("token", token);
        }


        UispRequest request = new UispRequest(Request.Method.POST,
                ServiceAddressConstant.BASE_ADDRESS, encodeParameters(parameter),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); // new Gson();

                        try {
                            PagedResult<T> data = gson.fromJson(response, type(PagedResult.class, cls));

                            listener.success(data, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.error(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.error(VolleyErrorHelper.getMessage(error));
                    }
                });

        addRequest(request, requestTag);
    }

    public <T> void post(String method, String requestTag, Map<String, String> parameter, Class<T> clazz, final ResponseListener<T> listener) {

        if (parameter == null) {
            parameter = new HashMap<>();
        }

        if (!parameter.containsKey("method")) {
            parameter.put("method", method);
        }

        String token = getEncryptedToken();

        if (!TextUtils.isEmpty(token)) {
            parameter.put("token", token);
        }

        GsonRequest<T> request = new GsonRequest<>(Request.Method.POST,
                ServiceAddressConstant.BASE_ADDRESS, clazz, encodeParameters(parameter),
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {

                        if (listener != null) {
                            listener.success(response, "");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        if (listener != null) {
                            listener.error(VolleyErrorHelper.getMessage(error));
                        }
                    }
                });

        addRequest(request, requestTag);
    }

    public void uploadAvatar(String filePath, final ResponseListener<String> listener) {
        String token = getEncryptedToken();

        uploadAvatar(filePath, token, listener);
    }

    public void uploadAvatar(String filePath, String token, final ResponseListener<String> listener) {

        MultipartRequestParams params = new MultipartRequestParams();
        params.put("method", "updateavatar");
        params.put("avatar", PictureUtil.bitmapToInputStream(filePath, 80), "avatar.png");
        if (!TextUtils.isEmpty(token)) {
            params.put("token", token);
        }

        MultipartRequest request = new MultipartRequest(Request.Method.POST, ServiceAddressConstant.BASE_ADDRESS, params,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();

                        try {
                            UserAvatar data = gson.fromJson(response, UserAvatar.class);

                            if (data.success) {
                                listener.success(data.avatar, "");
                            } else {
                                listener.error(data.error);
                            }
                        } catch (Exception e) {
                            listener.error(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.error(VolleyErrorHelper.getMessage(error));
                    }
                });

        mRequestQueue.add(request);
    }

    public <T> void upload(String requestTag, String method, Map<String, String> postParams, Map<String, File> files,
                           final Class<T> clazz, final ResponseListener<T> listener) {
        MultipartRequestParams params = new MultipartRequestParams();
        params.put("method", method);

        String token = getEncryptedToken();

        if (!TextUtils.isEmpty(token))
            params.put("token", token);

        if (postParams != null) {
            for (String key : postParams.keySet()) {
                params.put(key, postParams.get(key));
            }
        }

        if (files != null) {
            for (String key : files.keySet()) {
                params.put(key, files.get(key));
            }
        }

        MultipartRequest request = new MultipartRequest(Request.Method.POST, ServiceAddressConstant.BASE_ADDRESS, params,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {

                        try {
                            GsonBuilder builder = new GsonBuilder();
                            builder.registerTypeAdapter(String.class, new StringConverter());
                            builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                            Gson gson = builder.create();

                            GsonRequest.ResponseData data = gson.fromJson(response, GsonRequest.ResponseData.class);

                            if (!data.success) {
                                listener.error(data.error);
                            } else {
                                //RequestManager.getInstance().processResponseHeaders(response.headers);

                                //Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

                                T result = null;

                                String command = null;

                                if (data.user != null) {
                                    command = gson.toJson(data.user);
                                } else if (data.result != null) {
                                    command = gson.toJson(data.result);
                                }

                                if (!TextUtils.isEmpty(command)) {
                                    result = gson.fromJson(command, clazz);
                                    listener.success(result, null);
                                } else {
                                    listener.success(null, null);
                                }
                            }
                        } catch (Exception e) {
                            listener.error(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.error(VolleyErrorHelper.getMessage(error));
                    }
                });

        addRequest(request, requestTag);
    }

//    public void demoXiadan() {
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("seq", java.util.UUID.randomUUID().toString());
//        map.put("currName", XmppManager.name);
//        map.put("action", "xiadan");
//
//        StringRequest request = new StringRequest(Request.Method.GET, "http://115.28.171.100:7070/api.jsp?" + encodeParameters(map),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        ToastUtils.show(Application.getInstance(), "下单成功");
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        ToastUtils.show(Application.getInstance(), "下单失败");
//                    }
//                });
//
//        addRequest(request, null);
//    }
//
//    public void demoQiangdan(String seq, String toUser) {
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("seq", seq);
//        map.put("currName", XmppManager.name);
//        map.put("toUser", toUser);
//        map.put("action", "qiangdan");
//
//        StringRequest request = new StringRequest(Request.Method.GET, "http://115.28.171.100:7070/api.jsp?" + encodeParameters(map),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        ToastUtils.show(Application.getInstance(), "抢单成功");
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        ToastUtils.show(Application.getInstance(), "抢单失败");
//                    }
//                });
//        addRequest(request, null);
//    }

    private class UserAvatar {
        public boolean success;

        public String avatar;

        public String error;
    }

    public void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1.0f));
        request.setShouldCache(false);
        mRequestQueue.add(request);
    }

    public void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    private String mCookie = null;
    private String authorization = null;

    public void processRequestHeaders(Map<String, String> headers) {

        if (!TextUtils.isEmpty(mCookie)) {
            headers.put("Cookie", mCookie);
        }

        if (!TextUtils.isEmpty(authorization)) {
            headers.put("Authorization", authorization);
        }
    }

    public void processResponseHeaders(Map<String, String> headers) {
        if (headers.containsKey("Set-Cookie")) {
            mCookie = headers.get("Set-Cookie");
        }
    }
    /**
     * 将請求参数集转化为浏览器对应的参数字符串{@code username=zhangsan&password=666666&age=25}
     * @param params 請求参数集
     * @return 浏览器对应的参数字符串
     */
    private String encodeParameters(Map<String, String> params) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                encodedParams.append('&');
            }

            String parameters = encodedParams.toString();
            if (!TextUtils.isEmpty(parameters)) {
                parameters = parameters.substring(0, parameters.length() - 1);
            }
            return parameters;
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + "UTF-8", uee);
        }
    }

    public void clear() {
        _token = "";
        mCookie = null;
    }

    static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }

    public interface ResponseListener<T> {
        void success(T result, String msg);

        void error(String msg);
    }
}
