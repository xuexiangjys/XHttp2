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

import android.content.Context;
import android.text.TextUtils;

import com.xuexiang.xhttp2.logs.HttpLog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 设置缓存功能 [OkHttp自带缓存]
 *
 * @author xuexiang
 * @since 2018/6/20 上午12:22
 */
public class CacheInterceptor implements Interceptor {

    protected Context context;
    protected String cacheControlValue_Offline;
    protected String cacheControlValue_Online;
    //set cache times is 3 days
    protected static final int maxAgeOffline = 60 * 60 * 24 * 3;
    // read from cache for 60 s
    protected static final int maxAgeOnline = 60;

    public CacheInterceptor(Context context) {
        this(context, String.format("max-age=%d", maxAgeOnline));
    }

    public CacheInterceptor(Context context, String cacheControlValueOnline) {
        this(context, cacheControlValueOnline, String.format("max-age=%d", maxAgeOffline));
    }

    public CacheInterceptor(Context context, String cacheControlValueOnline, String cacheControlValueOffline) {
        this.context = context;
        this.cacheControlValue_Online = cacheControlValueOnline;
        this.cacheControlValue_Offline = cacheControlValueOffline;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String cacheControl = originalResponse.header("Cache-Control");
        HttpLog.i(maxAgeOnline + "s load cache:" + cacheControl);
        if (TextUtils.isEmpty(cacheControl) || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-age") || cacheControl.contains("max-stale")) {
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAgeOnline)
                    .build();

        } else {
            return originalResponse;
        }
    }
}
