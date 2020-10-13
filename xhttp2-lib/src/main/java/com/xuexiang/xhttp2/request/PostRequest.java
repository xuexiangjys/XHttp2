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

package com.xuexiang.xhttp2.request;


import android.text.TextUtils;

import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.cache.model.CacheMode;
import com.xuexiang.xhttp2.model.XHttpRequest;

/**
 * post请求
 *
 * @author xuexiang
 * @since 2018/6/25 下午8:48
 */
public class PostRequest extends BaseBodyRequest<PostRequest> {

    public PostRequest(String url) {
        super(url);
    }

    /**
     * 使用xHttpRequest来构建post请求
     * @param xHttpRequest 统一封装的请求实体对象
     */
    public PostRequest(XHttpRequest xHttpRequest) {
        super(xHttpRequest.getUrl());
        initRequest(xHttpRequest);
    }

    /**
     * 初始化请求
     *
     * @param xHttpRequest
     */
    private void initRequest(XHttpRequest xHttpRequest) {
        String baseUrl = xHttpRequest.getBaseUrl();
        String url = xHttpRequest.getUrl();
        long timeout = xHttpRequest.getTimeout();
        boolean accessToken = xHttpRequest.isAccessToken();
        CacheMode cacheMode = xHttpRequest.getCacheMode();

        if (!TextUtils.isEmpty(baseUrl)) {
            baseUrl(baseUrl);
        }
        if (!CacheMode.NO_CACHE.equals(cacheMode)) {
            cacheMode(cacheMode).cacheKey(url);
        }
        //如果超时时间小于等于0，使用默认的超时时间
        if (timeout <= 0) {
            timeout = XHttp.DEFAULT_TIMEOUT_MILLISECONDS;
        }
        accessToken(accessToken).timeOut(timeout).upJson(xHttpRequest.toString());
    }

}
