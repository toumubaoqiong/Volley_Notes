package com.vince.volley_notes.model;

import android.content.Context;

import com.vince.networkservice.request.NetRequest;
import com.vince.networkservice.request.NetRequestListener;
import com.vince.volley_notes.beans.Beans;

import java.util.Map;

/*
 *  @描述：    olly request 请求 model接口实现类
 */
public class VolleyRequstModelImpl
        implements VolleyRequstModel
{
    private static final String TAG = "VolleyRequstModelImpl";
    private Context mContext;

    public VolleyRequstModelImpl(Context context) {
        mContext = context;
    }

    @Override
    public void getRequest(String url,
                           Map<String, String> params,
                           NetRequestListener<String> listener)
    {
        NetRequest.getInstance(mContext)
                  .getStringRequest(url,params, listener, TAG);
    }

    @Override
    public void postRequest(String url,
                            Map<String, String> params,
                            NetRequestListener<String> listener)
    {
        NetRequest.getInstance(mContext)
                  .postStringRequest(url, params, listener, TAG);
    }

    @Override
    public void getJsonRequest(String url,
                               final Map<String, String> params,
                               final NetRequestListener<Beans> listener)
    {
        NetRequest.getInstance(mContext)
                  .getjson(url, params, listener, TAG, Beans.class);
    }

    @Override
    public void postJsonRequest(String url,
                                final Map<String, String> params,
                                final NetRequestListener<Beans> listener)
    {
        NetRequest.getInstance(mContext)
                  .postjson(url, params, listener, TAG, Beans.class);
    }

    @Override
    public void cancelRequest() {
        NetRequest.getInstance(mContext)
                  .cancelRequest(TAG);
    }
}
