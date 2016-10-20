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

public class UispRequest extends Request<String> {

    private final Response.Listener<String> mListener;
    private final String mRequestBody;

    private int stateCode = 0;

    public class ResponseData {
        public boolean success;
        public Object user;
        public Object command;
        public Object result;
        public String msg;
    }

    public UispRequest(int method, String url, String requestBody, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        mRequestBody = requestBody;
        mListener = listener;
    }

    public int getStateCode() {
        return stateCode;
    }

    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        stateCode = response.statusCode;

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(String.class, new StringConverter());
        Gson gson = builder.create();

        try {
            String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            Log.d("DATA", responseString);

            ResponseData responseData = gson.fromJson(responseString, ResponseData.class);// mClass);

            Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);

            RequestManager.getInstance().processResponseHeaders(response.headers);

            if (responseData.success) {
                String payload= "";
                if (responseData.user != null) {
                    payload = gson.toJson(responseData.user);
                } else if (responseData.command != null) {
                    payload = gson.toJson(responseData.command);
                } else if (responseData.result != null) {
                    payload = gson.toJson(responseData.result);
                }

                if (!TextUtils.isEmpty(payload)) {
                    return Response.success(payload, entry);
                } else {
                    return Response.error(new VolleyError("没有对应的数据"));
                }
            } else {
                return Response.error(new VolleyError(responseData.msg));
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
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
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
    public byte[] getBody() {
        try {
            return mRequestBody == null ? super.getBody() : mRequestBody
                    .getBytes(ServiceAddressConstant.PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, ServiceAddressConstant.PROTOCOL_CHARSET);
        } catch (AuthFailureError e) {
            VolleyLog.wtf("AuthFailureError while trying to get the bytes of %s",
                    ServiceAddressConstant.PROTOCOL_CHARSET);
        }
        return null;
    }
}