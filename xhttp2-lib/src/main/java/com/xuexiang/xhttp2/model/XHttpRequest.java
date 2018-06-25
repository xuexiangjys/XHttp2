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

package com.xuexiang.xhttp2.model;

import com.google.gson.Gson;
import com.xuexiang.xhttp2.annotation.RequestParams;
import com.xuexiang.xhttp2.cache.model.CacheMode;
import com.xuexiang.xhttp2.utils.Utils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 基础网络请求实体
 *
 * @author xuexiang
 * @since 2018/5/22 下午3:24
 */
@RequestParams
public abstract class XHttpRequest {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /**
     * 获取网络请求配置参数
     *
     * @param clazz
     * @return
     */
    public static RequestParams getHttpRequestParams(Class<? extends XHttpRequest> clazz) {
        return Utils.checkNotNull(clazz.getAnnotation(RequestParams.class), "requestParams == null");
    }

    public RequestParams getRequestParams() {
        return getHttpRequestParams(getClass());
    }

    /**
     * 获取请求的url地址【作为网络请求订阅的key和缓存的key】
     *
     * @param clazz
     * @return
     */
    public static String getUrl(Class<? extends XHttpRequest> clazz) {
        return getHttpRequestParams(clazz).url();
    }

    /**
     * @return 获取请求地址
     */
    public String getUrl() {
        return getRequestParams().url();
    }

    /**
     * 请求超时时间
     *
     * @return 超时时间
     */
    public long getTimeout() {
        return getRequestParams().timeout();
    }

    /**
     * 请求是否需要验证token
     *
     * @return 是否需要携带token
     */
    public boolean isAccessToken() {
        return getRequestParams().accessToken();
    }

    /**
     * @return 获取基础url
     */
    public String getBaseUrl() {
        return getRequestParams().baseUrl();
    }

    /**
     * @return 获得缓存模式
     */
    public CacheMode getCacheMode() {
        return getRequestParams().cacheMode();
    }

    /**
     * 用于通过反射获取请求返回类型的方法【不要调用】
     *
     * @return
     */
    protected abstract <T> T getResponseEntityType();

    /**
     * 解析获取请求的返回类型【默认类型：String】
     *
     * @return
     */
    public Type parseReturnType() {
        Type type = String.class;
        try {
            Method m = getClass().getDeclaredMethod("getResponseEntityType");
            m.setAccessible(true);
            type = m.getGenericReturnType();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return type;
    }
}
