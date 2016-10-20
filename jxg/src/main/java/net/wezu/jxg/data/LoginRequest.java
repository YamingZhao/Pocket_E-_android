package net.wezu.jxg.data;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.wezu.jxg.model.LoginResult;
import net.wezu.jxg.model.UserAddress;
import net.wezu.jxg.model.UserModel;
import net.wezu.jxg.service.ServiceAddressConstant;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author snox@live.com
 * @date 2015/9/24.
 */
public class LoginRequest extends Request<LoginResult> {

    private static final String TAG = "LoginRequest";

    private static Gson mGson = null;
    private final Response.Listener<LoginResult> mListener;
    private final String mRequestBody;

    private int stateCode = 0;

    public LoginRequest(int method, String url, Response.Listener<LoginResult> listener,
                        Response.ErrorListener errorListener) {
        this(method, url, null, listener, errorListener);
    }

    public LoginRequest(int method, String url, String requestBody,
                        Response.Listener<LoginResult> listener,
                        Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mRequestBody = requestBody;
        this.mListener = listener;

        setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1.0f));
    }

    public int getStateCode() {
        return stateCode;
    }

    public class LoginResponseData {

        public boolean success;

        public UserModel user;

        public UserAddress companyaddress;

        public String error;
    }


    @Override
    protected Response<LoginResult> parseNetworkResponse(NetworkResponse response) {
        stateCode = response.statusCode;

        if (mGson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(String.class, new StringConverter());
            builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
            mGson = builder.create();
        }

        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            VolleyLog.v("parseNetworkResponse: " + json);

            LoginResponseData data = mGson.fromJson(json, LoginResponseData.class);

            if (!data.success) {
                return Response.error(new VolleyError(data.error));
            } else {
                RequestManager.getInstance().processResponseHeaders(response.headers);

                Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

                return Response.success(new LoginResult(data.user, data.companyaddress), entry);
            }

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> headers = super.getHeaders();

        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        RequestManager.getInstance().processRequestHeaders(headers);

        return headers;
    }

    @Override
    protected void deliverResponse(LoginResult response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        Log.e(TAG, "deliverError: ", error);
        super.deliverError(error);
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? super.getBody() : mRequestBody
                    .getBytes(ServiceAddressConstant.PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, ServiceAddressConstant.PROTOCOL_CHARSET);
        } catch (AuthFailureError e) {
            VolleyLog.wtf("AuthFailureError while trying to get the bytes of %s using %s",
                    mRequestBody, ServiceAddressConstant.PROTOCOL_CHARSET);
        }
        return null;
    }
}
