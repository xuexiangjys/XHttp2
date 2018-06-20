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

package com.xuexiang.xhttp2.subsciber;

import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.exception.ApiExceptionHandler;
import com.xuexiang.xhttp2.logs.HttpLog;

import io.reactivex.observers.DisposableObserver;

/**
 * 基础的订阅者
 *
 * @author xuexiang
 * @since 2018/6/20 上午1:48
 */
public abstract class BaseSubscriber<T> extends DisposableObserver<T> {

    public BaseSubscriber() {

    }

    @Override
    protected void onStart() {
        HttpLog.d("--> Subscriber is onStart");
    }

    @Override
    public void onComplete() {
        HttpLog.d("--> Subscriber is Complete");
    }

    @Override
    public void onNext(T t) {
        try {
            onSuccess(t);
        } catch (Throwable e) {
            e.printStackTrace();
            onError(e);
        }
    }

    @Override
    public final void onError(Throwable e) {
        HttpLog.e("--> Subscriber is onError");
        try {
            if (e instanceof ApiException) {
                HttpLog.e("--> e instanceof ApiException, message:" + e.getMessage());
                onError((ApiException) e);
            } else {
                HttpLog.e("--> e !instanceof ApiException, message:" + e.getMessage());
                onError(ApiExceptionHandler.handleException(e));
            }
        } catch (Throwable throwable) {  //防止onError中执行又报错导致rx.exceptions.OnErrorFailedException: Error occurred when trying to propagate error to Observer.onError问题
            e.printStackTrace();
        }
    }

    /**
     * 出错
     *
     * @param e 出错信息
     */
    protected abstract void onError(ApiException e);

    /**
     * 安全版的{@link #onNext},自动做了try-catch
     *
     * @param t
     */
    protected abstract void onSuccess(T t);

}
