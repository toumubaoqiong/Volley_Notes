package com.vince.volley_notes.presenter;

import android.content.Context;
import android.util.Log;

import com.vince.networkservice.request.NetRequestListener;
import com.vince.volley_notes.beans.Beans;
import com.vince.volley_notes.model.VolleyRequstModel;
import com.vince.volley_notes.model.VolleyRequstModelImpl;

import java.util.Map;

/*
 *  @描述：    volly request 请求presenter接口实现类
 */
public class VollyRequestPresenterImpl implements VollyRequestPresenter{
    private static final String TAG = "VRequestPresenterImpl";

    private VolleyRequstModel mVolleyRequstModel;
    private Context mContext;

    public VollyRequestPresenterImpl(Context context){
        mContext = context;
        mVolleyRequstModel = new VolleyRequstModelImpl(mContext);
    }

    @Override
    public void getRequest(String url, final Map<String, String> params) {
        if(null != mVolleyRequstModel){
            mVolleyRequstModel.getRequest(url,params,listener);
        }
    }

    @Override
    public void postRequest(String url, Map<String, String> params) {
        if(null != mVolleyRequstModel){
            mVolleyRequstModel.postRequest(url,params,listener);
        }
    }

    @Override
    public void getJsonRequest(String url,
                        final Map<String, String> params){
        if(null != mVolleyRequstModel){
            mVolleyRequstModel.getJsonRequest(url,params,listenerJson);
        }
    }

    @Override
    public void postJsonRequest(String url,
                         final Map<String, String> params){

        if(null != mVolleyRequstModel){
            mVolleyRequstModel.postJsonRequest(url,params,listenerJson);
        }
    }

    NetRequestListener<String> listener = new NetRequestListener<String>(){
        @Override
        public void onSuccess(String respose) {
            Log.i(TAG,"respose--->onSuccess:" + respose);
        }

        @Override
        public void onFailed(String respose) {
            Log.i(TAG,"respose--->onFailed:" + respose);
        }
    };

    NetRequestListener<Beans> listenerJson = new NetRequestListener<Beans>(){
        @Override
        public void onSuccess(Beans respose) {
            Log.i(TAG,"respose--->onSuccess:" + respose.toString());
        }

        @Override
        public void onFailed(String respose) {
            Log.i(TAG,"respose--->onFailed:" + respose);
        }
    };

    /**
     *description:取消请求
     */
    public void cancelRequest(){
        if(null != mVolleyRequstModel){
            mVolleyRequstModel.cancelRequest();
        }
    }
}
