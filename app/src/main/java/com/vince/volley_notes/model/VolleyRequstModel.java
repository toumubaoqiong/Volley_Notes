package com.vince.volley_notes.model;

import com.vince.networkservice.request.NetRequestListener;

import java.util.Map;

/*
 *  @描述：    volly request 请求 model接口类
 */
public interface VolleyRequstModel {
    void getRequest(String urlWithParams, final NetRequestListener<String> listener);
    void postRequest(String url,
                     final Map<String, String> params,
                     final NetRequestListener<String> listener);

    void cancelRequest();
}
