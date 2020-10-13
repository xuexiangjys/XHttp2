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

package com.xuexiang.xhttp2.interceptor;

import com.xuexiang.xhttp2.logs.HttpLog;
import com.xuexiang.xhttp2.utils.HttpUtils;
import com.xuexiang.xhttp2.utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * <p>描述：动态拦截器</p>
 * 主要功能是针对参数：<br>
 * 1.可以获取到全局公共参数和局部参数，统一进行签名sign<br>
 * 2.可以自定义动态添加参数，类似时间戳timestamp是动态变化的，token（登录了才有），参数签名等<br>
 * 3.参数值是经过UTF-8编码的<br>
 * 4.默认提供询问是否动态签名（签名需要自定义），动态添加时间戳等<br>
 *
 * @author xuexiang
 * @since 2018/6/20 上午1:35
 */
public abstract class BaseDynamicInterceptor<R extends BaseDynamicInterceptor> implements Interceptor {
    private HttpUrl mHttpUrl;

    /**
     * 是否需要签名
     */
    private boolean mIsSign = false;
    /**
     * 是否需要追加时间戳
     */
    private boolean mTimeStamp = false;
    /**
     * 是否需要添加token
     */
    private boolean mAccessToken = false;

    public BaseDynamicInterceptor() {
    }

    public boolean isSign() {
        return mIsSign;
    }

    public R sign(boolean sign) {
        mIsSign = sign;
        return (R) this;
    }

    public boolean isTimeStamp() {
        return mTimeStamp;
    }

    public R timeStamp(boolean timeStamp) {
        mTimeStamp = timeStamp;
        return (R) this;
    }

    public R accessToken(boolean accessToken) {
        mAccessToken = accessToken;
        return (R) this;
    }

    public boolean isAccessToken() {
        return mAccessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if ("GET".equals(request.method())) {
            mHttpUrl = HttpUrl.parse(HttpUtils.parseUrl(request.url().toString()));
            request = addGetParamsSign(request);
        } else if ("POST".equals(request.method())) {
            mHttpUrl = request.url();
            request = addPostParamsSign(request);
        }
        return chain.proceed(request);
    }

    public HttpUrl getHttpUrl() {
        return mHttpUrl;
    }

    /**
     * 为get请求 添加签名和公共动态参数
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    private Request addGetParamsSign(Request request) throws UnsupportedEncodingException {
        HttpUrl httpUrl = request.url();
        HttpUrl.Builder newBuilder = httpUrl.newBuilder();

        //获取原有的参数
        Set<String> nameSet = httpUrl.queryParameterNames();
        ArrayList<String> nameList = new ArrayList<>();
        nameList.addAll(nameSet);
        TreeMap<String, Object> oldParams = new TreeMap<>();
        for (int i = 0; i < nameList.size(); i++) {
            String value = httpUrl.queryParameterValues(nameList.get(i)) != null && httpUrl.queryParameterValues(nameList.get(i)).size() > 0 ? httpUrl.queryParameterValues(nameList.get(i)).get(0) : "";
            oldParams.put(nameList.get(i), value);
        }
        String nameKeys = Arrays.asList(nameList).toString();
        //拼装新的参数
        TreeMap<String, Object> newParams = updateDynamicParams(oldParams);
        Utils.checkNotNull(newParams, "newParams==null");
        for (Map.Entry<String, Object> entry : newParams.entrySet()) {
            String urlValue = URLEncoder.encode(String.valueOf(entry.getValue()), HttpUtils.UTF8.name());
            //原来的URl: https://xxx.xxx.xxx/app/chairdressing/skinAnalyzePower/skinTestResult?appId=10101
            if (!nameKeys.contains(entry.getKey())) {//避免重复添加
                newBuilder.addQueryParameter(entry.getKey(), urlValue);
            }
        }

        httpUrl = newBuilder.build();
        request = request.newBuilder().url(httpUrl).build();
        return request;
    }

    /**
     * 为post请求 添加签名和公共动态参数
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    private Request addPostParamsSign(Request request) throws UnsupportedEncodingException {
        if (request.body() instanceof FormBody) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            FormBody formBody = (FormBody) request.body();

            //原有的参数
            TreeMap<String, Object> oldParams = new TreeMap<>();
            for (int i = 0; i < formBody.size(); i++) {
                oldParams.put(formBody.encodedName(i), formBody.encodedValue(i));
            }

            //拼装新的参数
            TreeMap<String, Object> newParams = updateDynamicParams(oldParams);
            Utils.checkNotNull(newParams, "newParams == null");
            for (Map.Entry<String, Object> entry : newParams.entrySet()) {
                String value = URLDecoder.decode(String.valueOf(entry.getValue()), HttpUtils.UTF8.name());
                bodyBuilder.addEncoded(entry.getKey(), value);
            }
            String url = HttpUtils.createUrlFromParams(HttpUtils.parseUrl(mHttpUrl.url().toString()), newParams);
            HttpLog.i(url);
            formBody = bodyBuilder.build();
            request = request.newBuilder().post(formBody).build();
        } else if (request.body() instanceof MultipartBody) {
            MultipartBody multipartBody = (MultipartBody) request.body();
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            List<MultipartBody.Part> oldParts = multipartBody.parts();

            //拼装新的参数
            List<MultipartBody.Part> newParts = new ArrayList<>();
            newParts.addAll(oldParts);
            TreeMap<String, Object> oldParams = new TreeMap<>();
            TreeMap<String, Object> newParams = updateDynamicParams(oldParams);
            for (Map.Entry<String, Object> paramEntry : newParams.entrySet()) {
                MultipartBody.Part part = MultipartBody.Part.createFormData(paramEntry.getKey(), String.valueOf(paramEntry.getValue()));
                newParts.add(part);
            }
            for (MultipartBody.Part part : newParts) {
                bodyBuilder.addPart(part);
            }
            multipartBody = bodyBuilder.build();
            request = request.newBuilder().post(multipartBody).build();
        } else if (request.body() instanceof RequestBody) {
            TreeMap<String, Object> params = updateDynamicParams(new TreeMap<String, Object>());
            String url = HttpUtils.createUrlFromParams(HttpUtils.parseUrl(mHttpUrl.url().toString()), params);
            request = request.newBuilder().url(url).build();
        }
        return request;
    }

    /**
     * 更新请求的动态参数
     *
     * @param dynamicMap
     * @return 返回新的参数集合
     */
    protected abstract TreeMap<String, Object> updateDynamicParams(TreeMap<String, Object> dynamicMap);
}
