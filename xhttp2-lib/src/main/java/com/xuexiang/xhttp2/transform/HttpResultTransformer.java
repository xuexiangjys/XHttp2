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

package com.xuexiang.xhttp2.transform;

import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.transform.func.HttpResponseThrowableFunc;
import com.xuexiang.xhttp2.transform.func.HttpResultFuc;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

/**
 * 将解析到的ApiResult转化为T, 并将错误抛出
 *
 * @author xuexiang
 * @since 2018/6/25 上午12:09
 */
public class HttpResultTransformer<T> implements ObservableTransformer<ApiResult<T>, T> {

    @Override
    public ObservableSource<T> apply(Observable<ApiResult<T>> upstream) {
        return upstream.map(new HttpResultFuc<T>())
                .onErrorResumeNext(new HttpResponseThrowableFunc<T>());
    }
}
