package com.vince.networkservice.request;

/*
 *  @描述：    网络请求监听回调
 */
public interface NetRequestListener<T> {
    void onSuccess(T respose);

    void onFailed(String respose);
}
