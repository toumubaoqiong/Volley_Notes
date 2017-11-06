package com.vince.volley_notes.model;

import android.content.Context;

import com.vince.networkservice.request.NetRequest;
import com.vince.networkservice.request.NetRequestListener;

import java.util.Map;

/*
 *  @描述：    olly request 请求 model接口实现类
 */
public class VolleyRequstModelImpl implements VolleyRequstModel{
    private static final String TAG = "VolleyRequstModelImpl";
    private Context mContext;

    public VolleyRequstModelImpl(Context context) {
        mContext = context;
    }

    @Override
    public void getRequest(String urlWithParams, NetRequestListener<String> listener) {
        NetRequest.getInstance(mContext).getStringRequest(urlWithParams,listener,TAG);
    }

    @Override
    public void postRequest(String url,
                            Map<String, String> params,
                            NetRequestListener<String> listener)
    {
        NetRequest.getInstance(mContext).postStringRequest(url,params,listener,TAG);
    }

    @Override
    public void cancelRequest(){
        NetRequest.getInstance(mContext).cancelAllRequest(TAG);
    }
}
