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

import com.xuexiang.xhttp2.utils.HttpUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 基础拦截器
 *
 * @author xuexiang
 * @since 2018/5/22 下午4:37
 */
public abstract class BaseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request;
        Request requestTmp = onBeforeRequest(chain.request(), chain);
        if (requestTmp != null) {
            request = requestTmp;
        } else {
            request = chain.request();
        }
        Response response = chain.proceed(request);
        boolean isText = HttpUtils.isText(response.body().contentType());
        if (!isText) {
            return response;
        }

        String bodyString = HttpUtils.getResponseBodyString(response);
        Response tmp = onAfterRequest(response, chain, bodyString);
        if (tmp != null) {
            return tmp;
        }
        return response;
    }

    /**
     * 请求拦截
     *
     * @param request
     * @param chain
     * @return {@code null} : 不进行拦截处理
     */
    protected abstract Request onBeforeRequest(Request request, Chain chain);

    /**
     * 响应拦截
     *
     * @param response
     * @param chain
     * @param bodyString
     * @return {@code null} : 不进行拦截处理
     */
    protected abstract Response onAfterRequest(Response response, Chain chain, String bodyString);
}
