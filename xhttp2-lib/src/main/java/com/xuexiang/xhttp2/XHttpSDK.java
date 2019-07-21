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

package com.xuexiang.xhttp2;

import android.app.Application;
import android.text.TextUtils;

import com.xuexiang.xhttp2.annotation.RequestParams;
import com.xuexiang.xhttp2.annotation.ThreadType;
import com.xuexiang.xhttp2.cache.converter.GsonDiskConverter;
import com.xuexiang.xhttp2.cache.model.CacheMode;
import com.xuexiang.xhttp2.interceptor.HttpLoggingInterceptor;
import com.xuexiang.xhttp2.logs.HttpLog;
import com.xuexiang.xhttp2.model.XHttpRequest;
import com.xuexiang.xhttp2.request.PostRequest;
import com.xuexiang.xhttp2.subsciber.BaseSubscriber;
import com.xuexiang.xhttp2.utils.HttpUtils;
import com.xuexiang.xhttp2.utils.TypeUtils;
import com.xuexiang.xhttp2.utils.Utils;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;

import static com.xuexiang.xhttp2.XHttp.DEFAULT_RETRY_COUNT;
import static com.xuexiang.xhttp2.XHttp.DEFAULT_RETRY_DELAY;
import static com.xuexiang.xhttp2.XHttp.DEFAULT_TIMEOUT_MILLISECONDS;

/**
 * 网络请求工具
 *
 * @author xuexiang
 * @since 2018/5/23 上午11:24
 */
public final class XHttpSDK {

    private XHttpSDK() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    //==============================================全局参数设置===============================================//

    /**
     * 初始化RxHttp
     *
     * @param application
     */
    public static void init(Application application) {
        XHttp.init(application);
        XHttp.getInstance()
                .setTimeout(DEFAULT_TIMEOUT_MILLISECONDS)
                .setRetryCount(DEFAULT_RETRY_COUNT)
                .setRetryDelay(DEFAULT_RETRY_DELAY)
                .setCacheMode(CacheMode.NO_CACHE)
                .setCacheDiskConverter(new GsonDiskConverter())//默认缓存使用序列化转化
                .setCacheMaxSize(50 * 1024 * 1024);//设置缓存大小为50M
    }

    /**
     * 增加全局拦截器
     *
     * @param interceptor
     */
    public static void addInterceptor(Interceptor interceptor) {
        XHttp.getInstance().addInterceptor(interceptor);
    }

    /**
     * 全局设置baseUrl
     *
     * @param baseUrl
     * @return true: 设置baseUrl成功
     */
    public static boolean setBaseUrl(String baseUrl) {
        if (verifyBaseUrl(baseUrl)) {
            XHttp.getInstance().setBaseUrl(baseUrl);
            return true;
        }
        return false;
    }

    /**
     * 验证BaseUrl是否合法
     *
     * @param baseUrl
     * @return true: 设置baseUrl成功
     */
    public static boolean verifyBaseUrl(String baseUrl) {
        if (!TextUtils.isEmpty(baseUrl)) {
            HttpUrl httpUrl = HttpUrl.parse(baseUrl);
            if (httpUrl != null) {
                List<String> pathSegments = httpUrl.pathSegments();
                return "".equals(pathSegments.get(pathSegments.size() - 1));
            }
        }
        return false;
    }

    /**
     * 全局设置SubUrl
     *
     * @param subUrl
     */
    public static void setSubUrl(String subUrl) {
        XHttp.getInstance().setSubUrl(subUrl);
    }

    /**
     * 设置debug模式
     */
    public static void debug() {
        XHttp.getInstance().debug(HttpLog.DEFAULT_LOG_TAG);
    }

    /**
     * 设置debug模式
     *
     * @param tag logFlag
     */
    public static void debug(String tag) {
        XHttp.getInstance().debug(tag);
    }

    /**
     * 设置debug模式
     *
     * @param loggingInterceptor 日志拦截器
     */
    public static void debug(HttpLoggingInterceptor loggingInterceptor) {
        XHttp.getInstance().debug(loggingInterceptor);
    }
    //==============================================通用Post请求===============================================//

    /**
     * 获取PostRequest请求
     *
     * @param url        请求地址（子地址）
     * @param json       请求json参数
     * @param threadType 线程调度类型
     * @return
     */
    public static PostRequest post(String url, String json, @ThreadType String threadType) {
        return post(url, true, json, threadType);
    }

    /**
     * 获取PostRequest请求
     *
     * @param url           请求地址（子地址）
     * @param isAccessToken 是否验证Token
     * @param json          请求json参数
     * @param threadType    线程调度类型
     * @return
     */
    public static PostRequest post(String url, boolean isAccessToken, String json, @ThreadType String threadType) {
        return XHttp.post(url)
                .accessToken(isAccessToken)
                .upJson(json)
                .threadType(threadType);
    }

    /**
     * 获取PostRequest请求
     *
     * @param url           请求地址（子地址）
     * @param isAccessToken 是否验证Token
     * @param json          请求json参数
     * @param isSyncRequest 是否是同步请求
     * @param toMainThread  是否回到主线程
     * @return
     */
    public static PostRequest post(String url, boolean isAccessToken, String json, boolean isSyncRequest, boolean toMainThread) {
        return XHttp.post(url)
                .accessToken(isAccessToken)
                .upJson(json)
                .syncRequest(isSyncRequest)
                .onMainThread(toMainThread);
    }

    /**
     * 执行post请求，返回对象
     *
     * @param url           请求地址（子地址）
     * @param isAccessToken 是否验证Token
     * @param json          请求json参数
     * @param isSyncRequest 是否是同步请求
     * @param toMainThread  是否回到主线程
     * @param clazz         请求返回的类型
     * @return
     */
    public static <T> Observable<T> execute(String url, boolean isAccessToken, String json, boolean isSyncRequest, boolean toMainThread, Class<T> clazz) {
        return post(url, isAccessToken, json, isSyncRequest, toMainThread).execute(clazz);
    }

    /**
     * 执行post请求，返回对象
     *
     * @param url           请求地址（子地址）
     * @param isAccessToken 是否验证Token
     * @param json          请求json参数
     * @param isSyncRequest 是否是同步请求
     * @param toMainThread  是否回到主线程
     * @param type          请求返回的类型
     * @return
     */
    public static <T> Observable<T> execute(String url, boolean isAccessToken, String json, boolean isSyncRequest, boolean toMainThread, Type type) {
        return post(url, isAccessToken, json, isSyncRequest, toMainThread).execute(type);
    }

    //==========================================执行post请求，返回对象======================================================//

    /**
     * 执行post请求，返回对象
     *
     * @param postRequest post请求
     * @param type        请求返回的类型
     * @return
     */
    public static <T> Observable<T> execute(PostRequest postRequest, Type type) {
        return postRequest.execute(type);
    }

    /**
     * 执行post请求，返回对象
     *
     * @param postRequest post请求
     * @param clazz       请求返回的类型
     * @return
     */
    public static <T> Observable<T> execute(PostRequest postRequest, Class<T> clazz) {
        return postRequest.execute(clazz);
    }

    /**
     * 执行post请求，返回订阅信息
     *
     * @param postRequest post请求
     * @param clazz       请求返回的类型
     * @param subscriber  订阅者
     * @return
     */
    public static <T> Disposable execute(PostRequest postRequest, Class<T> clazz, BaseSubscriber<T> subscriber) {
        return addRequest(postRequest.getUrl(), postRequest.execute(clazz).subscribeWith(subscriber));
    }

    //========================================特定线程Post请求====================================//

    //==============主线程->主线程==================//

    /**
     * 获取PostRequest请求（主线程->主线程， 需要验证token）
     *
     * @param url  请求地址（子地址）
     * @param json 请求json参数
     * @return
     */
    public static PostRequest postToMain(String url, String json) {
        return post(url, true, json, false, true);
    }

    /**
     * 执行PostRequest请求，返回对象（主线程->主线程，需要验证token）
     *
     * @param url   请求地址（子地址）
     * @param json  请求json参数
     * @param clazz 请求返回的类型
     * @return
     */
    public static <T> Observable<T> postToMain(String url, String json, Class<T> clazz) {
        return execute(postToMain(url, json), clazz);
    }

    /**
     * 获取PostRequest请求（主线程->主线程）
     *
     * @param xHttpRequest 请求实体
     * @return
     */
    public static PostRequest postToMain(XHttpRequest xHttpRequest) {
        return post(xHttpRequest, false, true);
    }

    /**
     * 执行post请求，返回对象（主线程->主线程）
     *
     * @param xHttpRequest 请求实体
     * @return
     */
    public static Observable executeToMain(XHttpRequest xHttpRequest) {
        return execute(xHttpRequest, false, true);
    }

    /**
     * 执行post请求并订阅，返回订阅信息（主线程->主线程）
     *
     * @param xHttpRequest 请求实体
     * @param subscriber   订阅者
     * @return
     */
    public static <T> Disposable executeToMain(XHttpRequest xHttpRequest, BaseSubscriber<T> subscriber) {
        return addRequest(xHttpRequest.getUrl(), (Disposable) executeToMain(xHttpRequest).subscribeWith(subscriber));
    }

    /**
     * 执行post请求并订阅，返回订阅信息（主线程->主线程）
     *
     * @param xHttpRequest 请求实体
     * @param subscriber   订阅者
     * @param tagName      请求标志
     * @return
     */
    public static <T> Disposable executeToMain(XHttpRequest xHttpRequest, BaseSubscriber<T> subscriber, Object tagName) {
        return addRequest(tagName, (Disposable) executeToMain(xHttpRequest).subscribeWith(subscriber));
    }

    /**
     * 执行PostRequest请求，返回对象（主线程->主线程）
     *
     * @param xHttpRequest 请求实体
     * @param clazz        请求返回的类型
     * @return
     */
    public static <T> Observable<T> postToMain(XHttpRequest xHttpRequest, Class<T> clazz) {
        return execute(postToMain(xHttpRequest), clazz);

    }

    /**
     * 执行PostRequest请求并订阅，返回订阅信息（主线程->主线程）
     *
     * @param xHttpRequest 请求实体
     * @param clazz        请求返回的类型
     * @param subscriber   订阅者
     * @return
     */
    public static <T> Disposable postToMain(XHttpRequest xHttpRequest, Class<T> clazz, BaseSubscriber<T> subscriber) {
        return execute(postToMain(xHttpRequest), clazz, subscriber);
    }

    /**
     * 获取PostRequest请求（主线程->主线程）
     *
     * @param json  请求json参数
     * @param clazz 网络请求实体类【使用了注解配置】
     * @return
     */
    public static PostRequest postToMain(String json, Class<?> clazz) {
        return post(json, clazz, false, true);
    }

    /**
     * 获取PostRequest请求（主线程->主线程）
     *
     * @param xHttpRequest 请求实体【使用了注解配置请求key】
     * @return
     */
    public static PostRequest postJsonToMain(XHttpRequest xHttpRequest) {
        return postJson(xHttpRequest, false, true);
    }

    //==============主线程->子线程==================//

    /**
     * 获取PostRequest请求（主线程->子线程，需要验证token）
     *
     * @param url  请求地址（子地址）
     * @param json 请求json参数
     * @return
     */
    public static PostRequest postToIO(String url, String json) {
        return post(url, true, json, false, false);
    }

    /**
     * 执行PostRequest请求，返回对象（主线程->子线程，需要验证token）
     *
     * @param url   请求地址（子地址）
     * @param json  请求json参数
     * @param clazz 请求返回的类型
     * @return
     */
    public static <T> Observable<T> postToIO(String url, String json, Class<T> clazz) {
        return execute(postToIO(url, json), clazz);
    }

    /**
     * 获取PostRequest请求（主线程->子线程）
     *
     * @param xHttpRequest 请求实体
     * @return
     */
    public static PostRequest postToIO(XHttpRequest xHttpRequest) {
        return post(xHttpRequest, false, false);
    }

    /**
     * 执行post请求，返回对象（主线程->子线程）
     *
     * @param xHttpRequest 请求实体
     * @return
     */
    public static Observable executeToIO(XHttpRequest xHttpRequest) {
        return execute(xHttpRequest, false, false);
    }

    /**
     * 执行post请求，返回订阅信息（主线程->子线程）
     *
     * @param xHttpRequest 请求实体
     * @param subscriber   订阅者
     * @return
     */
    public static <T> Disposable executeToIO(XHttpRequest xHttpRequest, BaseSubscriber<T> subscriber) {
        return addRequest(xHttpRequest.getUrl(), (Disposable) executeToIO(xHttpRequest).subscribeWith(subscriber));
    }

    /**
     * 执行post请求，返回订阅信息（主线程->子线程）
     *
     * @param xHttpRequest 请求实体
     * @param subscriber   订阅者
     * @param tagName      请求标志
     * @return
     */
    public static <T> Disposable executeToIO(XHttpRequest xHttpRequest, BaseSubscriber<T> subscriber, Object tagName) {
        return addRequest(tagName, (Disposable) executeToIO(xHttpRequest).subscribeWith(subscriber));
    }

    /**
     * 执行PostRequest请求，返回对象（主线程->子线程）
     *
     * @param xHttpRequest 请求实体
     * @param clazz        请求返回的类型
     * @return
     */
    public static <T> Observable<T> postToIO(XHttpRequest xHttpRequest, Class<T> clazz) {
        return execute(postToIO(xHttpRequest), clazz);
    }

    //==============子线程->子线程==================//

    /**
     * 获取PostRequest请求（子线程->子线程，需要验证token）
     *
     * @param url  请求地址（子地址）
     * @param json 请求json参数
     * @return
     */
    public static PostRequest postInThread(String url, String json) {
        return post(url, true, json, true, false);
    }

    /**
     * 执行PostRequest请求，返回对象（子线程->子线程，需要验证token）
     *
     * @param url   请求地址（子地址）
     * @param json  请求json参数
     * @param clazz 请求返回的类型
     * @return
     */
    public static <T> Observable<T> postInThread(String url, String json, Class<T> clazz) {
        return execute(postInThread(url, json), clazz);
    }

    /**
     * 获取PostRequest请求（子线程->子线程）
     *
     * @param xHttpRequest 请求实体
     * @return
     */
    public static PostRequest postInThread(XHttpRequest xHttpRequest) {
        return post(xHttpRequest, true, false);
    }

    /**
     * 执行post请求，返回对象（子线程->子线程）
     *
     * @param xHttpRequest 请求实体
     * @return
     */
    public static Observable executeInThread(XHttpRequest xHttpRequest) {
        return execute(xHttpRequest, true, false);
    }

    /**
     * 执行post请求，返回订阅信息（子线程->子线程）
     *
     * @param xHttpRequest 请求实体
     * @param subscriber   订阅者
     * @return
     */
    public static <T> Disposable executeInThread(XHttpRequest xHttpRequest, BaseSubscriber<T> subscriber) {
        return addRequest(xHttpRequest.getUrl(), (Disposable) executeInThread(xHttpRequest).subscribeWith(subscriber));
    }

    /**
     * 执行post请求，返回订阅信息（子线程->子线程）
     *
     * @param xHttpRequest 请求实体
     * @param subscriber   订阅者
     * @return
     */
    public static <T> Disposable executeInThread(XHttpRequest xHttpRequest, BaseSubscriber<T> subscriber, Object tagName) {
        return addRequest(tagName, (Disposable) executeInThread(xHttpRequest).subscribeWith(subscriber));
    }

    /**
     * 执行PostRequest请求，返回对象（子线程->子线程）
     *
     * @param xHttpRequest 请求实体
     * @param clazz        请求返回的类型
     * @return
     */
    public static <T> Observable<T> postInThread(XHttpRequest xHttpRequest, Class<T> clazz) {
        return execute(postInThread(xHttpRequest), clazz);
    }

    //======================================注解请求=======================================//

    /**
     * 【推荐使用】
     * 获取PostRequest请求（使用实体参数名作为请求Key）
     *
     * @param xHttpRequest  请求实体
     * @param isSyncRequest 是否是同步请求
     * @param toMainThread  是否回到主线程
     * @return
     */
    public static PostRequest post(XHttpRequest xHttpRequest, boolean isSyncRequest, boolean toMainThread) {
        return post(xHttpRequest.toString(), xHttpRequest.getClass(), isSyncRequest, toMainThread);
    }

    /**
     * 【推荐使用】
     * 执行post请求，返回对象（使用实体参数名作为请求Key）
     *
     * @param xHttpRequest  请求实体
     * @param isSyncRequest 是否是同步请求
     * @param toMainThread  是否回到主线程
     * @return
     */
    public static Observable execute(XHttpRequest xHttpRequest, boolean isSyncRequest, boolean toMainThread) {
        return execute(post(xHttpRequest, isSyncRequest, toMainThread), xHttpRequest.parseReturnType());
    }

    /**
     * 执行post请求，返回对象（使用实体参数名作为请求Key）
     *
     * @param postRequest  请求实体
     * @param xHttpRequest 请求实体
     * @return
     */
    public static Observable execute(PostRequest postRequest, XHttpRequest xHttpRequest) {
        return execute(postRequest, xHttpRequest.parseReturnType());
    }

    /**
     * 执行post请求，返回对象（使用实体参数名作为请求Key）
     *
     * @param postRequest 请求实体
     * @param type        请求返回的类型
     * @return
     */
    public static Observable executePost(PostRequest postRequest, Type type) {
        return execute(postRequest, type);
    }

    /**
     * 执行post请求，返回订阅信息
     *
     * @param postRequest  请求对象
     * @param xHttpRequest 请求体
     * @param subscriber   订阅信息
     * @param tagName      请求标志
     * @param <T>
     * @return
     */
    public static <T> Disposable execute(PostRequest postRequest, XHttpRequest xHttpRequest, BaseSubscriber<T> subscriber, Object tagName) {
        return addRequest(tagName, (Disposable) execute(postRequest, xHttpRequest).subscribeWith(subscriber));
    }

    /**
     * 执行post请求，返回订阅信息
     *
     * @param postRequest 请求对象
     * @param type        请求返回的类型
     * @param subscriber  订阅信息
     * @param tagName     请求标志
     * @param <T>
     * @return
     */
    public static <T> Disposable execute(PostRequest postRequest, Type type, BaseSubscriber<T> subscriber, Object tagName) {
        return addRequest(tagName, (Disposable) executePost(postRequest, type).subscribeWith(subscriber));
    }

    /**
     * 获取PostRequest请求（使用注解作为请求Key）
     *
     * @param xHttpRequest  请求实体【使用了注解配置请求key】
     * @param isSyncRequest 是否是同步请求
     * @param toMainThread  是否回到主线程
     * @return
     */
    public static PostRequest postJson(XHttpRequest xHttpRequest, boolean isSyncRequest, boolean toMainThread) {
        String jsonString = xHttpRequest.toString();
        try {
            jsonString = HttpUtils.getAnnotationParamString(xHttpRequest);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return post(jsonString, xHttpRequest.getClass(), isSyncRequest, toMainThread);
    }

    /**
     * 获取PostRequest请求（使用实体参数名作为请求Key）
     *
     * @param json          请求json参数
     * @param clazz         网络请求实体类【使用了注解配置】
     * @param isSyncRequest 是否是同步请求
     * @param toMainThread  是否回到主线程
     * @return
     */
    public static PostRequest post(String json, Class<?> clazz, boolean isSyncRequest, boolean toMainThread) {
        RequestParams requestParams = clazz.getAnnotation(RequestParams.class);
        Utils.checkNotNull(requestParams, "requestParams == null");

        String baseUrl = requestParams.baseUrl();
        String url = requestParams.url();
        long timeout = requestParams.timeout();
        boolean accessToken = requestParams.accessToken();
        CacheMode cacheMode = requestParams.cacheMode();

        PostRequest postRequest = XHttp.post(url);
        if (!TextUtils.isEmpty(baseUrl)) {
            postRequest.baseUrl(baseUrl);
        }
        if (!CacheMode.NO_CACHE.equals(cacheMode)) {
            postRequest.cacheMode(cacheMode).cacheKey(url);
        }
        if (timeout <= 0) {   //如果超时时间小于等于0，使用默认的超时时间
            timeout = XHttp.DEFAULT_TIMEOUT_MILLISECONDS;
        }
        return postRequest
                .accessToken(accessToken)
                .timeOut(timeout)
                .upJson(json)
                .syncRequest(isSyncRequest)
                .onMainThread(toMainThread);
    }


    //================================网络请求取消=================================================//

    /**
     * 注册网络请求的订阅
     *
     * @param tagName
     * @param disposable
     */
    public static Disposable addRequest(Object tagName, Disposable disposable) {
        return XHttpRequestPool.get().add(tagName, disposable);
    }

    /**
     * 注册网络请求的订阅
     *
     * @param tagName
     * @param disposable
     */
    public static Disposable addRequest(Disposable disposable, Object tagName) {
        return XHttpRequestPool.get().add(disposable, tagName);
    }

    /**
     * 取消注册网络请求的订阅
     *
     * @param tagName
     */
    public static void cancelRequest(Object tagName) {
        XHttpRequestPool.get().remove(tagName);
    }

    /**
     * 取消所有的网络请求订阅
     */
    public static void cancelAll() {
        XHttpRequestPool.get().removeAll();
    }

}
