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

import com.xuexiang.xhttp2.model.SchedulerType;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 网络请求线程切换调度
 *
 * @author xuexiang
 * @since 2018/6/25 上午12:16
 */
public class HttpSchedulersTransformer<T> implements ObservableTransformer<T, T> {
    /**
     * 线程类型
     */
    private SchedulerType mSchedulerType;

    /**
     * 构造方法
     *
     * @param isSyncRequest  是否是同步请求
     * @param isOnMainThread 是否回到主线程
     */
    public HttpSchedulersTransformer(boolean isSyncRequest, boolean isOnMainThread) {
        mSchedulerType = getSchedulerType(isSyncRequest, isOnMainThread);
    }

    public HttpSchedulersTransformer(SchedulerType schedulerType) {
        mSchedulerType = schedulerType;
    }

    /**
     * 获取线程的类型
     *
     * @param isSyncRequest  是否是同步请求
     * @param isOnMainThread 是否回到主线程
     * @return
     */
    private SchedulerType getSchedulerType(boolean isSyncRequest, boolean isOnMainThread) {
        if (isSyncRequest) {
            // 同步请求
            if (isOnMainThread) {
                return SchedulerType._main;
            } else {
                return SchedulerType._io;
            }
        } else {
            // 异步请求,开启io线程
            if (isOnMainThread) {
                return SchedulerType._io_main;
            } else {
                return SchedulerType._io_io;
            }
        }
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        switch (mSchedulerType) {
            case _main:
                return upstream.observeOn(AndroidSchedulers.mainThread());
            case _io:
                return upstream;
            case _io_main:
                return upstream
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            case _io_io:
                return upstream
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io());
            default:
                break;
        }
        return upstream;
    }
}
