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

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xuexiang.xhttp2.annotation.NetMethod;
import com.xuexiang.xhttp2.annotation.ThreadType;
import com.xuexiang.xhttp2.cache.model.CacheMode;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.request.PostRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

/**
 * 网络请求代理
 *
 * @author xuexiang
 * @since 2018/6/25 下午8:44
 */
public class XHttpProxy implements InvocationHandler {

    /**
     * 网络请求代理
     *
     * @param cls 代理的请求接口
     * @param <T>
     * @return
     */
    public static <T> T proxy(Class<T> cls) {
        return new XHttpProxy().create(cls);
    }

    /**
     * 网络请求代理
     *
     * @param cls        代理的请求接口
     * @param isPostJson post请求参数的是否是json形式
     * @param <T>
     * @return
     */
    public static <T> T proxy(Class<T> cls, boolean isPostJson) {
        return new XHttpProxy(isPostJson).create(cls);
    }

    /**
     * 网络请求代理
     *
     * @param cls        代理的请求接口
     * @param threadType 线程调度类型
     * @param isPostJson post请求参数的是否是json形式
     * @param <T>
     * @return
     */
    public static <T> T proxy(Class<T> cls, @ThreadType String threadType, boolean isPostJson) {
        return new XHttpProxy(threadType, isPostJson).create(cls);
    }


    /**
     * 网络请求代理
     *
     * @param cls        代理的请求接口
     * @param threadType 线程调度类型
     * @param <T>
     * @return
     */
    public static <T> T proxy(Class<T> cls, @ThreadType String threadType) {
        return new XHttpProxy(threadType).create(cls);
    }

    /**
     * 线程调度类型
     */
    private String mThreadType;

    /**
     * post请求参数的形式【默认为json】
     */
    private boolean mIsPostJson;

    /**
     * 构造方法
     */
    public XHttpProxy() {
        this(ThreadType.TO_MAIN, true);
    }

    /**
     * 构造方法
     *
     * @param isPostJson 是否post json
     */
    public XHttpProxy(boolean isPostJson) {
        this(ThreadType.TO_MAIN, isPostJson);
    }

    /**
     * 构造方法
     *
     * @param threadType 线程调度类型
     */
    public XHttpProxy(@ThreadType String threadType) {
        this(threadType, true);
    }

    /**
     * 构造方法
     *
     * @param threadType 线程调度类型
     * @param isPostJson 是否post json
     */
    public XHttpProxy(@ThreadType String threadType, boolean isPostJson) {
        mThreadType = threadType;
        mIsPostJson = isPostJson;
    }

    public <T> T create(Class<T> cls) {
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        NetMethod netMethod = method.getAnnotation(NetMethod.class);
        if (netMethod == null) {
            throw new ApiException(method.getName() + "方法无NetMethod注释", ApiException.ERROR.NET_METHOD_ANNOTATION_ERROR);
        } else if (netMethod.ParameterNames().length != method.getGenericParameterTypes().length) {
            throw new ApiException(method.getName() + "方法NetMethod注释与实际参数个数不对应", ApiException.ERROR.NET_METHOD_ANNOTATION_ERROR);
        }
        Map<String, Object> params = getParamsMap(method, args, netMethod);
        Type type = getReturnType(method);
        PostRequest postRequest = getPostRequest(netMethod);
        if (mIsPostJson) {
            postRequest.upJson(new Gson().toJson(params));
        } else {
            postRequest.params(params);
        }
        return postRequest.execute(type);
    }

    /**
     * 获取网络请求接口返回值的类型
     *
     * @param method
     * @return
     * @throws ApiException
     */
    private Type getReturnType(Method method) throws ApiException {
        Type type = method.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            throw new ApiException("接口方法:" + method.getName() + "的返回值类型不是泛型, 必须返回Observable<T>类型", ApiException.ERROR.NET_METHOD_ANNOTATION_ERROR);
        }
        return type;
    }

    /**
     * 获取post请求
     *
     * @param apiMethod
     * @return
     */
    private PostRequest getPostRequest(NetMethod apiMethod) {
        String baseUrl = apiMethod.BaseUrl();
        String url = apiMethod.Url();
        long timeout = apiMethod.Timeout();
        boolean accessToken = apiMethod.AccessToken();
        CacheMode cacheMode = apiMethod.CacheMode();

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
                .threadType(mThreadType)
                .accessToken(accessToken)
                .timeOut(timeout);
    }

    /**
     * 获取请求参数集合
     *
     * @param method
     * @param args
     * @param apiMethod
     * @return
     */
    @NonNull
    private Map<String, Object> getParamsMap(Method method, Object[] args, NetMethod apiMethod) {
        Map<String, Object> params = new TreeMap<>();
        Type[] parameters = method.getGenericParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            params.put(apiMethod.ParameterNames()[i], args[i]);
        }
        return params;
    }

    /**
     * 更新线程调度状态
     *
     * @param postRequest
     * @return
     */
    private PostRequest updateThreadType(PostRequest postRequest) {
        postRequest.threadType(mThreadType);
        return postRequest;
    }
}
