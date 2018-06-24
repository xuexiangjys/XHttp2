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

package com.xuexiang.xhttp2.transform.func;

import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.exception.ServerException;
import com.xuexiang.xhttp2.model.ApiResult;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * ApiResult<T>转换T
 *
 * @author xuexiang
 * @since 2018/6/21 下午8:33
 */
public class HttpResultFuc<T> implements Function<ApiResult<T>, T> {

    @Override
    public T apply(@NonNull ApiResult<T> response) throws Exception {
        if (ApiException.isSuccess(response)) {
            return response.getData();
        } else {
            throw new ServerException(response.getCode(), response.getMsg());
        }
    }
}
