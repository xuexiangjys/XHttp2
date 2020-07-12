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

import androidx.annotation.NonNull;

/**
 * 错误信息处理
 *
 * @author xuexiang
 * @since 2018/6/20 上午1:46
 */
public final class ApiExceptionHandler {

    /**
     * 默认的错误信息处理者
     */
    private static IExceptionHandler sIExceptionHandler;

    /**
     * 设置错误信息处理者
     * @param exceptionHandler
     */
    public static void setExceptionHandler(@NonNull IExceptionHandler exceptionHandler) {
        sIExceptionHandler = exceptionHandler;
    }

    /**
     * 处理过滤错误信息
     * @param e
     * @return
     */
    public static ApiException handleException(Throwable e) {
        if (sIExceptionHandler != null) {
            return sIExceptionHandler.handleException(e);
        } else {
            return ApiException.handleException(e);
        }
    }
}
