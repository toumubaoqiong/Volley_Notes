package com.vince.networkservice.request;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

/*
 *  @文件名:   NetRequest
 *  @描述：    用来进行相关的网络请求
 */
public class NetRequest {
    private static final String TAG = "NetRequest";

    private static NetRequest   mNetRequest;
    //请求队列
    private        RequestQueue mQueue;
    //应用上下文
    private        Context      mApplicaitonContext;
    //用作同步锁
    private static Object lock = new Object();


    public NetRequest(Context context) {
        mApplicaitonContext = context.getApplicationContext();
        mQueue = Volley.newRequestQueue(mApplicaitonContext);
    }

    public static NetRequest getInstance(Context context) {

        if (null == mNetRequest) {
            synchronized (lock) {
                if (null == mNetRequest) {
                    mNetRequest = new NetRequest(context);
                }
            }
        }
        return mNetRequest;
    }

    /**
     *description:stringRequest GET请求
     */
    public void getStringRequest(String urlWithParams, final NetRequestListener<String> listener,String tag) {

        StringRequest stringRequest = new StringRequest(urlWithParams,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                listener.onSuccess(response);
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                if (null != error) {
                                                                    listener.onFailed(error.getMessage());
                                                                }
                                                            }
                                                        });

        stringRequest.setTag(tag);
        mQueue.add(stringRequest);
    }

    /**
     *description:stringRequest POST请求
     */
    public void postStringRequest(String url,
                           final Map<String, String> params,
                           final NetRequestListener<String> listener,String tag)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                                        url,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                listener.onSuccess(response);
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                if (null != error) {
                                                                    listener.onFailed(error.getMessage());
                                                                }
                                                            }
                                                        })
        {
            @Override
            protected Map<String, String> getParams()
                    throws AuthFailureError
            {
                return params;
            }
        };


        stringRequest.setTag(tag);
        mQueue.add(stringRequest);
    }

    /**
     *description:取消所有属于TAG请求
     */
    public void cancelAllRequest(String tag) {
        mQueue.cancelAll(tag);
    }
}
