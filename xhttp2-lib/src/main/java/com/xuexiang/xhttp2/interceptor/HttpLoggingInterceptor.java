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
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;

/**
 * <p>描述：设置日志拦截器</p>
 * 提供了详细、易懂的日志打印<br>
 *
 * @author xuexiang
 * @since 2018/6/20 上午12:58
 */
public class HttpLoggingInterceptor implements Interceptor {

    private volatile Level level = Level.NONE;
    protected Logger logger;
    protected String tag;
    private boolean isPrintStack = true;

    public enum Level {
        NONE,       //不打印log
        BASIC,      //只打印 请求首行 和 响应首行
        HEADERS,    //打印请求和响应的所有 Header
        BODY,        //所有数据全部打印
        PARAM      //只打印请求和响应参数
    }

    public void log(String message) {
        logger.log(java.util.logging.Level.INFO, message);
    }

    public HttpLoggingInterceptor(String tag) {
        setTag(tag);
    }

    public HttpLoggingInterceptor(String tag, boolean isPrintStack) {
        setTag(tag);
        this.isPrintStack = isPrintStack;
    }

    public HttpLoggingInterceptor setLevel(Level level) {
        if (level == null) {
            throw new NullPointerException("level == null. Use Level.NONE instead.");
        }
        this.level = level;
        return this;
    }

    public HttpLoggingInterceptor setTag(String tag) {
        this.tag = tag;
        logger = Logger.getLogger(tag);
        return this;
    }

    public String getTag() {
        return tag;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (Level.NONE.equals(level)) { //不打任何日志
            return chain.proceed(request);
        }

        //请求日志拦截
        logForRequest(request, chain.connection());

        //执行请求，计算请求时间
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            e.printStackTrace();
            log("<-- HTTP FAILED: " + e.getMessage());
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        //响应日志拦截
        return logForResponse(response, tookMs);
    }

    /**
     * 记录请求日志
     *
     * @param request
     * @param connection
     * @throws IOException
     */
    protected void logForRequest(Request request, Connection connection) throws IOException {
        if (level != Level.PARAM) {
            log("-------------------------------request-------------------------------");
        }
        boolean logBody = (level == Level.BODY || level == Level.PARAM);
        boolean logHeaders = (level == Level.BODY || level == Level.HEADERS);
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        try {
            String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
            log(requestStartMessage);

            if (logHeaders) {
                Headers headers = request.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    log("\t" + headers.name(i) + ": " + headers.value(i));
                }
            }

            if (logBody && hasRequestBody) {
                if (HttpUtils.isPlaintext(requestBody.contentType())) {
                    log("\tbody:" + bodyToString(request));
                } else {
                    log("\tbody: maybe [file part] , too large too print , ignored!");
                }
            }
        } catch (Exception e) {
            onError(e);
        } finally {
            if (level != Level.PARAM) {
                log("--> END " + request.method());
            }
        }
    }

    /**
     * 记录响应日志
     *
     * @param response
     * @param tookMs   请求花费的时间
     * @return
     */
    protected Response logForResponse(Response response, long tookMs) {
        if (level != Level.PARAM) {
            log("-------------------------------response-------------------------------");
        }
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();
        boolean logBody = (level == Level.BODY || level == Level.PARAM);
        boolean logHeaders = (level == Level.BODY || level == Level.HEADERS);

        try {
            log("<-- " + clone.code() + ' ' + clone.message() + ' ' + clone.request().url() + " (" + tookMs + "ms）");
            if (logHeaders) {
                log(" ");
                Headers headers = clone.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    log("\t" + headers.name(i) + ": " + headers.value(i));
                }
                log(" ");
            }

            if (logBody && HttpHeaders.hasBody(clone)) {
                if (HttpUtils.isPlaintext(responseBody.contentType())) {
                    String body = responseBody.string();
                    log("\tbody:" + body);
                    responseBody = ResponseBody.create(responseBody.contentType(), body);
                    return response.newBuilder().body(responseBody).build();
                } else {
                    log("\tbody: maybe [file part] , too large too print , ignored!");
                }
                if (level != Level.PARAM) {
                    log(" ");
                }
            }
        } catch (Exception e) {
            onError(e);
        } finally {
            if (level != Level.PARAM) {
                log("<-- END HTTP");
            }
        }
        return response;
    }


    protected String bodyToString(Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            Charset charset = HttpUtils.UTF8;
            MediaType contentType = copy.body().contentType();
            if (contentType != null) {
                charset = contentType.charset(HttpUtils.UTF8);
            }
            return URLDecoder.decode(buffer.readString(charset), HttpUtils.UTF8.name());
        } catch (Exception e) {
            onError(e);
        }
        return "";
    }

    protected void onError(Throwable t) {
        if (isPrintStack) {
            t.printStackTrace();
        }
    }
}