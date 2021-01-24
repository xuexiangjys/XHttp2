/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
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
 *
 */

package com.xuexiang.xhttp2.utils;

import androidx.annotation.NonNull;

import com.xuexiang.xhttp2.model.ApiResult;

/**
 * API请求工具类
 *
 * @author xuexiang
 * @since 2021/1/25 12:33 AM
 */
public final class ApiUtils {

    private ApiUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 请求结果处理者，判断是否请求成功
     */
    private static IRequestHandler sRequestHandler = getDefaultRequestHandler();

    /**
     * 请求成功的结果码，默认为0
     */
    private static int SUCCESS_CODE = 0;

    /**
     * 设置请求结果处理者
     *
     * @param sRequestHandler 请求结果处理者
     */
    public static void setIRequestHandler(@NonNull IRequestHandler sRequestHandler) {
        ApiUtils.sRequestHandler = sRequestHandler;
    }

    /**
     * 设置请求成功的code码
     *
     * @param successCode 标识请求成功的结果码
     */
    public static void setSuccessCode(int successCode) {
        SUCCESS_CODE = successCode;
    }

    public static int getSuccessCode() {
        return SUCCESS_CODE;
    }

    /**
     * 请求是否成功
     *
     * @param apiResult 请求结果
     * @return true: 请求成功；false：请求失败
     */
    public static boolean isRequestSuccess(ApiResult apiResult) {
        return sRequestHandler != null && sRequestHandler.isRequestSuccess(apiResult);
    }

    private static IRequestHandler getDefaultRequestHandler() {
        return new IRequestHandler() {
            @Override
            public boolean isRequestSuccess(ApiResult apiResult) {
                return apiResult != null && apiResult.isSuccess();
            }
        };
    }

    /**
     * 请求结果处理者
     *
     * @author xuexiang
     * @since 2021/1/25 12:37 AM
     */
    public interface IRequestHandler {
        /**
         * 请求是否成功
         *
         * @param apiResult 请求结果
         * @return true: 请求成功；false：请求失败
         */
        boolean isRequestSuccess(ApiResult apiResult);
    }
}
