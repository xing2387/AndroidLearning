package com.example.xing.androidlearning.test;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;

/**
 * Created by xing on 7/29/16.
 */
public class GsonRequest<T> extends JsonRequest<T> {
    /**
     * Default charset for JSON request.
     */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    private Class<T> mClass;
    private TypeToken<T> mTypeToken;
    private Gson mGson;

    public GsonRequest(int method, String url, Class<T> clazz, JsonObject requestBody,
                       Response.Listener<T> successListener, Response.ErrorListener errorListener) {
        this(method, url, clazz, null, requestBody, successListener, errorListener);
    }

    public GsonRequest(int method, String url, TypeToken<T> typeToken, JsonObject requestBody,
                       Response.Listener<T> successListener, Response.ErrorListener errorListener) {
        this(method, url, null, typeToken, requestBody, successListener, errorListener);
    }

    public GsonRequest(String url, TypeToken<T> typeToken,
                       Response.Listener<T> successListener, Response.ErrorListener errorListener) {
        this(Method.GET, url, null, typeToken, null, successListener, errorListener);
    }

    public GsonRequest(String url, Class<T> clazz,
                       Response.Listener<T> successListener, Response.ErrorListener errorListener) {
        this(Method.GET, url, clazz, null, null, successListener, errorListener);
    }

    public GsonRequest(int method, String url, Class<T> clazz, TypeToken<T> typeToken, JsonObject requestBody,
                       Response.Listener<T> successListener, Response.ErrorListener errorListener) {
        super(method, url, requestBody.toString(), successListener, errorListener);
        mClass = clazz;
        mGson = new Gson();
        mTypeToken = typeToken;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if (mTypeToken == null)
                return Response.success(mGson.fromJson(jsonString, mClass),
                        HttpHeaderParser.parseCacheHeaders(response));//用Gson解析返回Java对象
            else
                return (Response<T>) Response.success(mGson.fromJson(jsonString, mTypeToken.getType()),
                        HttpHeaderParser.parseCacheHeaders(response));//通过构造TypeToken让Gson解析成自定义的对象类型

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

}
