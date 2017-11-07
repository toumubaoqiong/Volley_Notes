package com.vince.volley_notes.presenter;

import java.util.Map;

/*
 *  @描述：    volly request 请求presenter接口类
 */
public interface VollyRequestPresenter {

    void getRequest(String url, final Map<String, String> params);

    void postRequest(String url, final Map<String, String> params);

    void getJsonRequest(String url, final Map<String, String> params);

    void postJsonRequest(String url, final Map<String, String> params);

    void cancelRequest();
}
