package net.wezu.jxg.data;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.wezu.jxg.service.ServiceAddressConstant;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author snox@live.com
 * @date 2015/9/24.
 */
public class GsonRequest<T> extends Request<T> {

    private static final String TAG = "GsonRequest";

    private static Gson mGson = null;
    private final Class<T> mClass;
    private final Response.Listener<T> mListener;
    private final String mRequestBody;

    private int stateCode = 0;

    public GsonRequest(int method, String url, Class<T> cls, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        this(method, url, cls, null, listener, errorListener);
    }

    public GsonRequest(int method, String url, Class<T> cls, String requestBody,
                       Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mRequestBody = requestBody;
        this.mClass = cls;
        this.mListener = listener;
    }

    public int getStateCode() {
        return stateCode;
    }

    public class ResponseData {

        public boolean success;

        public Object user;
        public Object result;

        public String error;
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        stateCode = response.statusCode;

        if (mGson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(String.class, new StringConverter());
            builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
            mGson = builder.create();
        }

        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            ResponseData data = mGson.fromJson(json, ResponseData.class);

            if (!data.success) {
                return Response.error(new VolleyError(data.error));
            } else {
                RequestManager.getInstance().processResponseHeaders(response.headers);

                Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

                T result = null;

                String command = null;

                if (data.user != null) {
                    command = mGson.toJson(data.user);
                } else if (data.result != null) {
                    command = mGson.toJson(data.result);
                }


                if (!TextUtils.isEmpty(command)) {
                    result = mGson.fromJson(command, mClass);
                    return Response.success(result, entry);
                } else {
                    return Response.success(null, entry);
                }
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
    protected void deliverResponse(T response) {
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

//    @Override
//    public String getCacheKey() {
//        Uri uri = Uri.parse(getUrl());
//        Uri.Builder builder = Uri.parse(
//                uri.getScheme() + "://" + uri.getAuthority() + uri.getPath()).buildUpon();
//        Set<String> names = uri.getQueryParameterNames();
//        if (names != null) {
//            for (String param : names) {
//                if ("latitude".equals(param) || "longitude".equals(param)) {
//                    continue;
//                }
//                builder.appendQueryParameter(param, uri.getQueryParameter(param));
//            }
//        }
//
//        return builder.build().toString();
//    }
}
