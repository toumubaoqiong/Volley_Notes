package com.vince.networkservice.request;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/*
 *  @描述：    转化GET请求的url
 */
public class UrlWithParams {
    private static final String TAG = "UrlWithParams";

    Map<String,String> mParams;
    String mUrl;

    public UrlWithParams(String url, Map<String, String> params) {
        mParams = params;
        mUrl = url;
    }

    /**
     * 转化为GET请求的url
     * @return GET请求的url
     */
    public String parseUrlWithParams() {

        if(null == mParams)  return mUrl;

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : mParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            try {
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8" ));
            } catch ( UnsupportedEncodingException e ) {
                e.printStackTrace();
            }
        }

        String urlWithParams = mUrl;
        String paramStr = result.toString();

        if( !TextUtils.isEmpty(paramStr ) ) {
            urlWithParams += "?" + paramStr;
        }

        return urlWithParams;
    }
}
