package com.vince.volley_notes.model;

import com.vince.networkservice.request.NetRequestListener;
import com.vince.volley_notes.beans.Beans;

import java.util.Map;

/*
 *  @描述：    volly request 请求 model接口类
 */
public interface VolleyRequstModel {
    void getRequest(String url, Map<String, String> params, NetRequestListener<String> listener);

    void postRequest(String url,
                     final Map<String, String> params,
                     final NetRequestListener<String> listener);

    void getJsonRequest(String url,
                        final Map<String, String> params,
                        final NetRequestListener<Beans> listener);

    void postJsonRequest(String url,
                         final Map<String, String> params,
                         final NetRequestListener<Beans> listener);

    void cancelRequest();
}
