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

package com.xuexiang.xhttp2.exception;

import android.net.ParseException;
import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.xuexiang.xhttp2.model.ApiResult;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.InterruptedIOException;
import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * 统一处理了API异常错误
 *
 * @author xuexiang
 * @since 2018/6/20 上午1:41
 */
public class ApiException extends Exception {

    /**
     * 错误的code码
     */
    private int mCode;

    public ApiException(String message, int code) {
        super(message);
        mCode = code;
    }

    public ApiException(Throwable e, int code) {
        super(e);
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }

    /**
     * 获取展示的错误信息
     *
     * @return
     */
    public String getDisplayMessage() {
        return getMessage();
    }

    /**
     * 获取错误的详细信息
     *
     * @return
     */
    public String getDetailMessage() {
        return "Code:" + mCode + ", Message:" + getMessage();
    }

    public static boolean isSuccess(ApiResult apiResult) {
        return apiResult != null && apiResult.isSuccess();
    }

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            if (!TextUtils.isEmpty(httpException.message())) {
                ex = new ApiException(httpException.message(), httpException.code());
            } else {
                ex = new ApiException(httpException.getMessage(), httpException.code());
            }
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ApiException(resultException.getMessage(), resultException.getErrCode());
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSyntaxException
                || e instanceof JsonSerializer
                || e instanceof NotSerializableException
                || e instanceof ParseException) {
            ex = new ApiException("解析错误", ERROR.PARSE_ERROR);
            return ex;
        } else if (e instanceof ClassCastException) {
            ex = new ApiException("类型转换错误", ERROR.CAST_ERROR);
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException("网络连接异常，请稍后再试", ERROR.NET_WORD_ERROR);
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException("证书验证失败", ERROR.SSL_ERROR);
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ApiException("请求服务器超时，请稍后再试", ERROR.TIMEOUT_ERROR);
            return ex;
        } else if (e instanceof SocketTimeoutException) {
            ex = new ApiException("网络连接超时，请稍后再试", ERROR.TIMEOUT_ERROR);
            return ex;
        } else if (e instanceof UnknownHostException) { //无法解析该域名
            ex = new ApiException("网络不给力，请检查网络设置", ERROR.UN_KNOWN_HOST_ERROR);
            return ex;
        } else if (e instanceof NullPointerException) {
            ex = new ApiException("空指针错误", ERROR.NULL_POINTER_ERROR);
            return ex;
        } else if (e instanceof OutOfMemoryError) {
            ex = new ApiException("内存不足，请清理后台应用", ERROR.OUT_OF_MEMORY_ERROR);
            return ex;
        } else if (e instanceof InterruptedIOException) {
            ex = new ApiException("请求被取消", ERROR.REQUEST_CANCEL);
            return ex;
        } else {
            ex = new ApiException(e, ERROR.UNKNOWN);
            return ex;
        }
    }

    /**
     * 约定异常
     */
    public static class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 5000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = UNKNOWN + 1;
        /**
         * 网络错误
         */
        public static final int NET_WORD_ERROR = PARSE_ERROR + 1;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = NET_WORD_ERROR + 1;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = HTTP_ERROR + 1;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = SSL_ERROR + 1;

        /**
         * 调用错误
         */
        public static final int INVOKE_ERROR = TIMEOUT_ERROR + 1;
        /**
         * 类转换错误
         */
        public static final int CAST_ERROR = INVOKE_ERROR + 1;
        /**
         * 请求取消
         */
        public static final int REQUEST_CANCEL = CAST_ERROR + 1;
        /**
         * 未知主机错误
         */
        public static final int UN_KNOWN_HOST_ERROR = REQUEST_CANCEL + 1;
        /**
         * 空指针错误
         */
        public static final int NULL_POINTER_ERROR = UN_KNOWN_HOST_ERROR + 1;
        /**
         * OOM错误
         */
        public static final int OUT_OF_MEMORY_ERROR = NULL_POINTER_ERROR + 1;
        /**
         * 下载错误
         */
        public static final int DOWNLOAD_ERROR = OUT_OF_MEMORY_ERROR + 1;
        /**
         * NetMethod注解错误
         */
        public static final int NET_METHOD_ANNOTATION_ERROR = OUT_OF_MEMORY_ERROR + 1;


    }
}
