/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xhttp2.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.xuexiang.xhttp2.annotation.ParamKey;
import com.xuexiang.xhttp2.logs.HttpLog;
import com.xuexiang.xhttp2.model.XHttpRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * http工具类
 *
 * @author xuexiang
 * @since 2018/6/19 下午10:59
 */
public final class HttpUtils {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    private HttpUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取响应body的byte流
     * @param response
     * @return
     */
    @Nullable
    public static byte[] getResponseBody(@NonNull Response response){
        ResponseBody responseBody = response.body();
        byte[] source = null ;
        try {
            source = responseBody.bytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return source;
    }

    /**
     * 获取响应body的String
     * @param response
     * @return
     * @throws IOException
     */
    @NonNull
    public static String getResponseBodyString(Response response) throws IOException {
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }
        return buffer.clone().readString(charset);
    }

    /**
     * 判断MediaType是否是text类型
     * @param mediaType
     * @return
     */
    public static boolean isText(MediaType mediaType) {
        if (mediaType == null)
            return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        return mediaType.subtype() != null && mediaType.subtype().equals("json");
    }

    /**
     * 解析前：https://xxx.xxx.xxx/app/chairdressing/skinAnalyzePower/skinTestResult?appId=10101
     * 解析后：https://xxx.xxx.xxx/app/chairdressing/skinAnalyzePower/skinTestResult
     * @param url
     * @return
     */
    public static String parseUrl(String url) {
        if (!"".equals(url) && url.contains("?")) {// 如果URL不是空字符串
            url = url.substring(0, url.indexOf('?'));
        }
        return url;
    }

    /**
     * 将参数拼接到url中
     * @param url 请求的url
     * @param params 参数
     * @return
     */
    public static String createUrlFromParams(String url, Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0) sb.append("&");
            else sb.append("?");
            for (Map.Entry<String, String> urlParams : params.entrySet()) {
                String urlValues = urlParams.getValue();
                //对参数进行 utf-8 编码,防止头信息传中文
                String urlValue = URLEncoder.encode(urlValues, UTF8.name());
                sb.append(urlParams.getKey()).append("=").append(urlValue).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            HttpLog.e(e);
        }
        return url;
    }

    /**
     * 获取注解设置请求key的请求Json
     * @param xHttpRequest
     * @return
     * @throws IllegalAccessException
     */
    public static String getAnnotationParamString(XHttpRequest xHttpRequest) throws IllegalAccessException {
        TreeMap<String, Object> params = new TreeMap<>();
        Field[] fields = xHttpRequest.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            ParamKey paramKey = field.getAnnotation(ParamKey.class);
            if (paramKey != null) {
                params.put(paramKey.key(), field.get(xHttpRequest));
            } else {
                params.put(field.getName(), field.get(xHttpRequest));
            }
        }
        return new Gson().toJson(params);
    }
}
