package com.vince.networkservice.request;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;

/**
 *description:自定义的Request类,此处应用了fastjson解析包进行json数据解析
 */
public class FastJsonRequest<T> extends Request<T> {
	private static final String TAG = "FastJsonRequest";
	
    private Response.Listener<T> mListener;
    private final Map<String, String> mParams;
    private Map<String,String> mHeaders;
    private Request.Priority mPriority;
    private Class<T> mClass;
    private String url;
    
    public FastJsonRequest(int method, String url, Map<String, String> params,
						   Map<String, String> headers, Class<T> pclass,
						   Response.Listener<T> listener, Response.ErrorListener errorListener,
						   Priority priority) {
    	
        super(method, url, errorListener);
        this.url = url;
        mListener = listener;
        mParams = params;
        mHeaders = headers;
        mPriority = priority;
        mClass = pclass;
    }

	/**
	 * 进一步封装
     */
    public FastJsonRequest(String url, Map<String, String> params,
						   Map<String, String> headers, Class<T> pclass,
						   Response.Listener<T> listener, Response.ErrorListener errorListener,
						   Priority priority) {
        this(null == params ? Method.GET : Method.POST, url,
			 params, headers,
			 pclass, listener,
			 errorListener, priority);
    }

	/**
	 * Post请求传参
     */
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (null == mHeaders) {
            mHeaders = Collections.emptyMap();
        }
        return mHeaders;
    }
    
    @Override
    public Request.Priority getPriority() {
    	return (mPriority == null) ? Priority.NORMAL : mPriority;
    }

	@Override
    protected void deliverResponse(T response) {
    	if(mListener != null)
    		mListener.onResponse(response);
    }
    
    @Override
    public void cancel() {
    	super.cancel();
    	mListener = null;
    }
 
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

			//若加密了，先解密
			/*if(EncryptUtils.checkIsEncrypt(response.headers)){
				jsonString = EncryptUtils.decryptString(jsonString);
			}*/
			Log.i(TAG, "[Server Return Data] " + jsonString);
			return Response.success(JSON.parseObject(jsonString, mClass), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			
			Log.e(TAG, "[Error while parse NetworkResponse] " + e.getMessage());
			return Response.error(new ParseError(e));
		} catch (NullPointerException e){
			
			Log.e(TAG, "[Error while parse NetworkResponse] " + e.getMessage());
			return Response.error(new ParseError(e));
		} catch (Exception e){
			
			Log.e(TAG, "[Error while parse NetworkResponse] " + e.getMessage());
			return Response.error(new ParseError(e));
		}
	}
}