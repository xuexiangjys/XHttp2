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

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xuexiang.xhttp2.annotation.NetMethod;
import com.xuexiang.xhttp2.annotation.ThreadType;
import com.xuexiang.xhttp2.cache.model.CacheMode;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.request.BaseBodyRequest;
import com.xuexiang.xhttp2.request.BaseRequest;
import com.xuexiang.xhttp2.request.PostRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

import static com.xuexiang.xhttp2.annotation.NetMethod.GET;
import static com.xuexiang.xhttp2.annotation.NetMethod.POST;
import static com.xuexiang.xhttp2.annotation.NetMethod.PUT;

/**
 * 网络请求代理[这里只是一种演示，可以模仿着自定义]
 * <p>
 * 请结合@NetMethod注解注释的接口使用
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
     * 构造方法
     */
    public XHttpProxy() {
        this(ThreadType.TO_MAIN);
    }

    /**
     * 构造方法
     *
     * @param threadType 线程调度类型
     */
    public XHttpProxy(@ThreadType String threadType) {
        mThreadType = threadType;
    }


    public <T> T create(Class<T> cls) {
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        NetMethod netMethod = method.getAnnotation(NetMethod.class);
        if (netMethod == null) {
            throw new ApiException(method.getName() + "方法无NetMethod注释", ApiException.ERROR.NET_METHOD_ANNOTATION_ERROR);
        } else if (netMethod.parameterNames().length != method.getGenericParameterTypes().length) {
            throw new ApiException(method.getName() + "方法NetMethod注释与实际参数个数不对应", ApiException.ERROR.NET_METHOD_ANNOTATION_ERROR);
        }

        Map<String, Object> params = getParamsMap(method, args, netMethod);
        Type type = getReturnType(method);
        BaseRequest request = getHttpRequest(netMethod);
        if (request instanceof BaseBodyRequest) {
            if (netMethod.paramType() == NetMethod.JSON) {
                ((BaseBodyRequest) request).upJson(new Gson().toJson(params));
            } else {
                request.params(params);
            }
        } else {
            if (netMethod.paramType() == NetMethod.URL_GET) {
                if (args.length > 0) {
                    request.url(netMethod.url() + "/" + args[0]);
                    request.params(getParamsMap(method, args, netMethod, 1));
                }
            } else {
                request.params(params);
            }
        }
        return request.execute(type);
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
    private BaseRequest getHttpRequest(NetMethod apiMethod) {
        String action = apiMethod.action();
        String baseUrl = apiMethod.baseUrl();
        String url = apiMethod.url();
        long timeout = apiMethod.timeout();
        boolean accessToken = apiMethod.accessToken();
        CacheMode cacheMode = apiMethod.cacheMode();

        BaseRequest request;
        switch (action) {
            case POST:
                request = XHttp.post(url);
                break;
            case GET:
                request = XHttp.get(url);
                break;
            case PUT:
                request = XHttp.put(url);
                break;
            default:
                request = XHttp.delete(url);
                break;
        }
        if (!TextUtils.isEmpty(baseUrl)) {
            request.baseUrl(baseUrl);
        }
        if (!CacheMode.NO_CACHE.equals(cacheMode)) {
            request.cacheMode(cacheMode).cacheKey(url);
        }
        if (timeout <= 0) {   //如果超时时间小于等于0，使用默认的超时时间
            timeout = XHttp.DEFAULT_TIMEOUT_MILLISECONDS;
        }
        return request
                .threadType(mThreadType)
                .keepJson(apiMethod.keepJson())
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
            params.put(apiMethod.parameterNames()[i], args[i]);
        }
        return params;
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
    private Map<String, Object> getParamsMap(Method method, Object[] args, NetMethod apiMethod, int index) {
        Map<String, Object> params = new TreeMap<>();
        Type[] parameters = method.getGenericParameterTypes();
        for (int i = index; i < parameters.length; i++) {
            params.put(apiMethod.parameterNames()[i], args[i]);
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
