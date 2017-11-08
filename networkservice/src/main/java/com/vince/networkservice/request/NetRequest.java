package com.vince.networkservice.request;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vince.networkservice.cache.CacheInfo;
import com.vince.networkservice.cache.CacheManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/*
 *  @文件名:   NetRequest
 *  @描述：    用来进行相关的网络请求
 */
public class NetRequest {
    private static final String TAG                 = "NetRequest";
    /*打印信息*/
    private static final String URL_IS_NULL         = "CREATE REQUEST FAILED, URL IS NULL";
    private static final String TAG_IS_NULL         = "CREATE REQUEST FAILED, TAG IS NULL";
    /*公共参数*/
    private static final String PARAMS_DEVICE_MODEL = "device_model";
    private static final String PARAMS_VERSION_NAME = "version_name";
    private static final String PARAMS_VERSION_CODE = "version_code";
    private static final String PARAMS_PACKAGE_NAME = "package_name";
    private static final String TIME_STAMP          = "time_stamp";
    /*优先级标识*/
    public static final  int    PRIORITY_HIGHEST    = 0;
    public static final  int    PRIORITY_HIGH       = 1;
    public static final  int    PRIORITY_NORMAL     = 2;
    public static final  int    PRIORITY_LOW        = 3;
    /*默认超时时长*/
    private static final int    DEFAUL_TIME_OUT     = 60000;

    /*用作单例对象*/
    private static NetRequest   mNetRequest;
    //请求队列
    private        RequestQueue mQueue;
    //用来存储当前的请求,用于去除重复请求
    private Map<String, Request<?>> mCurrentRequests = new HashMap<>();
    //应用上下文
    private Context mApplicaitonContext;
    //用作同步锁
    private static final Object lock    = new Object();
    private static final Object mapLock = new Object();


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
     * 在封装一层,便于外面调用
     */
    public <T> void getjson(String url,
                            Map<String, String> params,
                            final boolean needCache,
                            final NetRequestListener<T> listener,
                            String tag,
                            Class<T> javaBeanClass)
    {

        getjson(url, params, needCache, listener, PRIORITY_NORMAL, tag, javaBeanClass);
    }

    /**
     * 封装Json Get请求
     * @param url 请求url
     * @param params 参数
     * @param listener 回调监听
     * @param priority 优先级
     * @param tag TAG
     * @param javaBeanClass 解析类
     * @param <T> 泛型
     */
    public <T> void getjson(String url,
                            Map<String, String> params,
                            final boolean needCache,
                            final NetRequestListener<T> listener,
                            int priority,
                            String tag,
                            Class<T> javaBeanClass)
    {
        if (null == url) {
            listener.onFailed(URL_IS_NULL);
        }

        if (null == tag) {
            listener.onFailed(TAG_IS_NULL);
        }

        addBasicParams(params);

        UrlWithParams getRequestUrl = new UrlWithParams(url, params);
        final String  urlWithParams = getRequestUrl.parseUrlWithParams();

        Request<?> request = getRequestFromMap(urlWithParams);

        if (null != request) {
            Log.i(TAG, "[getJson]　已有相同的请求：" + urlWithParams);
            return;
        } else {
            Log.d(TAG, "[getJson]　新请求添加成功" + urlWithParams);
        }

        final CacheInfo<T> cacheResponse = loadCache(needCache, urlWithParams, javaBeanClass);
        if (null != cacheResponse && !cacheResponse.isDue()) {
            listener.onSuccess(cacheResponse.getCache());
            return;
        }

        FastJsonRequest<T> tFastJsonRequest = new FastJsonRequest<T>(Request.Method.GET,
                                                                     urlWithParams,
                                                                     null,
                                                                     null,
                                                                     javaBeanClass,
                                                                     new Response.Listener<T>() {
                                                                         @Override
                                                                         public void onResponse(T response) {
                                                                             removeRequestFromMap(
                                                                                     urlWithParams);
                                                                             listener.onSuccess(
                                                                                     response);
                                                                             saveToCache(response,
                                                                                         urlWithParams,
                                                                                         needCache);
                                                                         }
                                                                     },
                                                                     new Response.ErrorListener() {
                                                                         @Override
                                                                         public void onErrorResponse(
                                                                                 VolleyError error)
                                                                         {
                                                                             removeRequestFromMap(
                                                                                     urlWithParams);
                                                                             if (null != error) {
                                                                                 String s = error.getMessage();
                                                                                 if (null != s) {
                                                                                     listener.onFailed(
                                                                                             s);
                                                                                 }
                                                                             }
                                                                         }
                                                                     },
                                                                     getTaskPriority(priority))
        {
            @Override
            public Map<String, String> getHeaders()
                    throws AuthFailureError
            {
                return getBasicHeaders();
            }
        };

        tFastJsonRequest.setTag(tag);
        tFastJsonRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAUL_TIME_OUT,
                                                               DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                               DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToCurrentRequestMap(urlWithParams, tFastJsonRequest);
        mQueue.add(tFastJsonRequest);
    }

    /**
     * 在封装一层,便于外面调用
     */
    public <T> void postjson(String url,
                             Map<String, String> params,
                             final boolean needCache,
                             final NetRequestListener<T> listener,
                             String tag,
                             Class<T> javaBeanClass)
    {

        postjson(url, params, needCache, listener, PRIORITY_NORMAL, tag, javaBeanClass);
    }

    /**
     * 封装Json POST请求
     * @param url 请求url
     * @param params 参数
     * @param listener 回调监听
     * @param priority 优先级
     * @param tag TAG
     * @param javaBeanClass 解析类
     * @param <T> 泛型
     */
    public <T> void postjson(String url,
                             Map<String, String> params,
                             final boolean needCache,
                             final NetRequestListener<T> listener,
                             int priority,
                             String tag,
                             Class<T> javaBeanClass)
    {

        if (null == url) {
            listener.onFailed(URL_IS_NULL);
        }

        if (null == tag) {
            listener.onFailed(TAG_IS_NULL);
        }

        addBasicParams(params);

        UrlWithParams getRequestUrl = new UrlWithParams(url, params);
        final String  urlWithParams = getRequestUrl.parseUrlWithParams();

        Request<?> request = getRequestFromMap(urlWithParams);

        if (null != request) {
            Log.i(TAG, "[postJson]　已有相同的请求：" + urlWithParams);
            return;
        } else {
            Log.d(TAG, "[postJson]　新请求添加成功" + urlWithParams);
        }

        final CacheInfo<T> cacheResponse = loadCache(needCache, urlWithParams, javaBeanClass);
        if (null != cacheResponse && !cacheResponse.isDue()) {
            listener.onSuccess(cacheResponse.getCache());
            return;
        }

        FastJsonRequest<T> tFastJsonRequest = new FastJsonRequest<T>(Request.Method.POST,
                                                                     url,
                                                                     params,
                                                                     null,
                                                                     javaBeanClass,
                                                                     new Response.Listener<T>() {
                                                                         @Override
                                                                         public void onResponse(T response) {

                                                                             removeRequestFromMap(
                                                                                     urlWithParams);

                                                                             listener.onSuccess(
                                                                                     response);

                                                                             saveToCache(response,
                                                                                         urlWithParams,
                                                                                         needCache);
                                                                         }
                                                                     },
                                                                     new Response.ErrorListener() {
                                                                         @Override
                                                                         public void onErrorResponse(
                                                                                 VolleyError error)
                                                                         {
                                                                             removeRequestFromMap(
                                                                                     urlWithParams);

                                                                             if (null != error) {
                                                                                 String s = error.getMessage();
                                                                                 if (null != s) {
                                                                                     listener.onFailed(
                                                                                             s);
                                                                                 }
                                                                             }
                                                                         }
                                                                     },
                                                                     getTaskPriority(priority))
        {
            @Override
            public Map<String, String> getHeaders()
                    throws AuthFailureError
            {
                return getBasicHeaders();
            }
        };

        tFastJsonRequest.setTag(tag);
        tFastJsonRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAUL_TIME_OUT,
                                                               DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                               DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToCurrentRequestMap(urlWithParams, tFastJsonRequest);
        mQueue.add(tFastJsonRequest);
    }

    /**
     *添加请求头信息
     */
    public Map<String, String> getBasicHeaders() {
        Map<String, String> headers = new HashMap<>();

        headers.put(PARAMS_DEVICE_MODEL, DeviceData.getDeviceModel());
        headers.put(PARAMS_VERSION_NAME, DeviceData.getVersionName(mApplicaitonContext));
        headers.put(PARAMS_VERSION_CODE, DeviceData.getVersionCode(mApplicaitonContext));
        headers.put(PARAMS_PACKAGE_NAME, DeviceData.getPackageName(mApplicaitonContext));
        headers.put(TIME_STAMP,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                         Locale.getDefault()).format(new Date()));
        return headers;
    }


    /**
     * 存入正在请求的队列
     */
    private boolean addToCurrentRequestMap(String key, Request<?> mRequest) {

        synchronized (mapLock) {
            if (mCurrentRequests.containsKey(key)) {
                return false;
            } else {
                mCurrentRequests.put(key, mRequest);
                return true;
            }
        }
    }

    /**
     * 根据key获取当前的请求
     */
    public Request<?> getRequestFromMap(String key) {
        synchronized (mapLock) {
            if (mCurrentRequests.containsKey(key)) {
                return mCurrentRequests.get(key);
            }

            return null;
        }
    }

    /**
     *添加公共参数
     */
    public void addBasicParams(Map<String, String> params) {
        params.put(PARAMS_DEVICE_MODEL, android.os.Build.MODEL);
        params.put(PARAMS_VERSION_NAME, DeviceData.getVersionName(mApplicaitonContext));
        params.put(PARAMS_VERSION_CODE, DeviceData.getVersionCode(mApplicaitonContext));
        params.put(PARAMS_PACKAGE_NAME, DeviceData.getPackageName(mApplicaitonContext));
    }

    /**
     * 外面在封装一层,便于调用
     */
    public void getStringRequest(String url,
                                 Map<String, String> params,
                                 final boolean needCache,
                                 final NetRequestListener<String> listener,
                                 String tag)
    {
        getStringRequest(url, params, needCache, listener, PRIORITY_NORMAL, tag);
    }

    /**
     *description:stringRequest GET请求
     */
    public void getStringRequest(String url,
                                 Map<String, String> params,
                                 final boolean needCache,
                                 final NetRequestListener<String> listener,
                                 final int priority,
                                 String tag)
    {

        if (null == url) {
            listener.onFailed(URL_IS_NULL);
        }

        if (null == tag) {
            listener.onFailed(TAG_IS_NULL);
        }

        addBasicParams(params);

        UrlWithParams getRequestUrl = new UrlWithParams(url, params);
        final String  urlWithParams = getRequestUrl.parseUrlWithParams();

        Request<?> request = getRequestFromMap(urlWithParams);

        if (null != request) {
            Log.i(TAG, "[getJson]　已有相同的请求：" + urlWithParams);
            return;
        } else {
            Log.d(TAG, "[getJson]　新请求添加成功" + urlWithParams);
        }

        final CacheInfo<String> cacheResponse = loadCache(needCache, urlWithParams, String.class);
        if (null != cacheResponse && !cacheResponse.isDue()) {
            listener.onSuccess(cacheResponse.getCache());
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                                        urlWithParams,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                removeRequestFromMap(urlWithParams);
                                                                listener.onSuccess(response);
                                                                saveToCache(response,
                                                                            urlWithParams,
                                                                            needCache);
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                removeRequestFromMap(urlWithParams);

                                                                if (null != error) {
                                                                    listener.onFailed(error.getMessage());
                                                                }
                                                            }
                                                        })
        {
            @Override
            public Map<String, String> getHeaders()
                    throws AuthFailureError
            {
                return getBasicHeaders();
            }

            @Override
            public Priority getPriority() {
                return getTaskPriority(priority);
            }
        };

        stringRequest.setTag(tag);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAUL_TIME_OUT,
                                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToCurrentRequestMap(urlWithParams, stringRequest);
        mQueue.add(stringRequest);
    }

    /**
     * 加载缓存
     */
    private <T> CacheInfo<T> loadCache(boolean needCache, String key, Class<T> javaBeanClass) {
        if (!needCache) { return null; }

        CacheInfo<String> jsonCache = CacheManager.getInstance(mApplicaitonContext)
                                                  .loadString(key);

        if (jsonCache == null) {
            return null;
        }

        T            cacheResponse = JSON.parseObject(jsonCache.getCache(), javaBeanClass);
        CacheInfo<T> cacheInfo     = new CacheInfo<>();
        cacheInfo.setCache(cacheResponse);
        cacheInfo.setDue(jsonCache.isDue());

        return cacheInfo;
    }

    /**
     * 保存缓存数据
     */
    private <T> void saveToCache(T response, String urlWithParams, boolean needCache) {

        if (!needCache) {
            return;
        }

        //判断返回值是否有问题,没有问题则机型缓存
        if (response instanceof IDataEmpty) {
            IDataEmpty iDataEmpty = (IDataEmpty) response;

            if (iDataEmpty.isResultDataEmpty()) {
                Log.e(TAG, "[saveDataToCache]　返回数据为空 不缓存 " + urlWithParams);

                return;
            }
        }

        String jsonString = JSON.toJSONString(response);
        CacheManager.getInstance(mApplicaitonContext)
                    .saveString(urlWithParams, jsonString);
    }

    /**
     * 外面在封装一层,便于调用
     */
    public void postStringRequest(String url,
                                  final Map<String, String> params,
                                  final boolean needCache,
                                  final NetRequestListener<String> listener,
                                  String tag)
    {
        postStringRequest(url, params, needCache, listener, PRIORITY_NORMAL, tag);

    }

    /**
     *description:stringRequest POST请求
     */
    public void postStringRequest(String url,
                                  final Map<String, String> params,
                                  final boolean needCache,
                                  final NetRequestListener<String> listener,
                                  final int priority,
                                  String tag)
    {

        if (null == url) {
            listener.onFailed(URL_IS_NULL);
        }

        if (null == tag) {
            listener.onFailed(TAG_IS_NULL);
        }

        addBasicParams(params);

        UrlWithParams getRequestUrl = new UrlWithParams(url, params);
        final String  urlWithParams = getRequestUrl.parseUrlWithParams();

        Request<?> request = getRequestFromMap(urlWithParams);

        if (null != request) {
            Log.i(TAG, "[postJson]　已有相同的请求：" + urlWithParams);
            return;
        } else {
            Log.d(TAG, "[postJson]　新请求添加成功" + urlWithParams);
        }

        final CacheInfo<String> cacheResponse = loadCache(needCache, urlWithParams, String.class);
        if (null != cacheResponse && !cacheResponse.isDue()) {
            listener.onSuccess(cacheResponse.getCache());
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                                        url,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                removeRequestFromMap(urlWithParams);
                                                                listener.onSuccess(response);
                                                                saveToCache(response,
                                                                            urlWithParams,
                                                                            needCache);
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                removeRequestFromMap(urlWithParams);
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

            @Override
            public Map<String, String> getHeaders()
                    throws AuthFailureError
            {
                return getBasicHeaders();
            }

            @Override
            public Priority getPriority() {
                return getTaskPriority(priority);
            }
        };


        stringRequest.setTag(tag);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAUL_TIME_OUT,
                                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToCurrentRequestMap(urlWithParams, stringRequest);
        mQueue.add(stringRequest);
    }

    /**
     *description:取消所有属于TAG请求
     */
    public void cancelRequest(String tag) {
        mQueue.cancelAll(tag);
        removeAllRequestInMapWithTag(tag);
    }

    /**
     * 删除请求队列对应map的请求
     */
    private void removeRequestFromMap(String key) {

        synchronized (mapLock) {
            if (mCurrentRequests.containsKey(key)) {
                mCurrentRequests.remove(key);
            }
        }
    }

    /**
     * 在当前请求队列的map中删掉取消的请求
     * @param tag  需要取消请求的tag
     */
    private void removeAllRequestInMapWithTag(String tag) {

        synchronized (mapLock) {
            Iterator<Map.Entry<String, Request<?>>> iter = mCurrentRequests.entrySet()
                                                                           .iterator();
            //由于要进行删除操作,用这种遍历方式
            while (iter.hasNext()) {
                Map.Entry<String, Request<?>> entry   = iter.next();
                Request<?>                    request = entry.getValue();

                if (null == request) {
                    continue;
                }

                String contentTag = (String) request.getTag();
                if (tag.equals(contentTag)) {
                    iter.remove();
                    request.cancel();
                    request.setRequestQueue(null);
                }
            }
        }
    }

    /**
     * 根据标识转换当前的优先级
     */
    private Request.Priority getTaskPriority(final int priority) {

        if (priority == PRIORITY_HIGHEST) {
            return Request.Priority.IMMEDIATE;
        } else if (priority == PRIORITY_HIGH) {
            return Request.Priority.HIGH;
        } else if (priority == PRIORITY_NORMAL) {
            return Request.Priority.NORMAL;
        } else if (priority == PRIORITY_LOW) {
            return Request.Priority.LOW;
        } else {
            return Request.Priority.NORMAL;
        }
    }
}
