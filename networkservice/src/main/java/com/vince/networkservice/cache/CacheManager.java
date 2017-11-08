package com.vince.networkservice.cache;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.vince.networkservice.request.UrlWithParams;

import java.util.Map;

/**
 *description:管理cache类
 */
public class CacheManager {

    public static final int CACHE_VALID_TIME = 6 * ACache.TIME_HOUR;
    private static CacheManager manager;
    private static Object objLock = new Object();
    private ACache cache;

    private CacheManager(Context ctx) {
        initCache(ctx);
    }

    public static CacheManager getInstance(Context ctx) {
        if (null == manager) {
            synchronized (objLock) {
                if (null == manager) {
                    manager = new CacheManager(ctx.getApplicationContext());
                }
            }
        }
        return manager;
    }

    public void resetCache(Context ctx) {
        if (null != cache) {
            cache.resetCache();
            cache = null;
        }
        manager.initCache(ctx.getApplicationContext());
    }

    private void initCache(Context ctx) {
        if (cache == null) {
            cache = ACache.get(ctx.getApplicationContext());
        }
    }

    public void saveString(String key, String value) {
        cache.put(key, value, CACHE_VALID_TIME);
    }

    public void saveString(String key, String value, int time) {
        cache.put(key, value, time);
    }

    public CacheInfo<String> loadString(String key) {
        CacheInfo<String> cacheInfo = cache.getAsStringConsiderDue(key);
        return cacheInfo;
    }

    public boolean isCacheExist(String url, final Map<String, String> params) {
        UrlWithParams     getRequestParams = new UrlWithParams(url, params);
        final String      urlWithParams    = getRequestParams.toString();
        CacheInfo<String> jsonCache        = loadString(urlWithParams);

        return jsonCache != null;
    }

    public void saveObject(String key, Object value) {
        String data = JSON.toJSONString(value);
        cache.put(key, data, CACHE_VALID_TIME);
    }

    public <T> T getObject(String key, Class<T> type) {
        CacheInfo<String> cacheInfo = loadString(key);
        if (null == cacheInfo) {
            return null;
        }
        if (cacheInfo.getCache() == null) { return null; }

        return JSON.parseObject(cacheInfo.getCache(), type);
    }

    public void removeCache(String key) {
        cache.remove(key);
    }

    public boolean removeCache(String url, final Map<String, String> params) {
        UrlWithParams getRequestParams = new UrlWithParams(url, params);
        final String  urlWithParams    = getRequestParams.toString();
        return cache.remove(urlWithParams);
    }
}
