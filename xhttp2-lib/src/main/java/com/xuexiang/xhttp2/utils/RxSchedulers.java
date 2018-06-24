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

package com.xuexiang.xhttp2.utils;

import com.xuexiang.xhttp2.model.SchedulerType;
import com.xuexiang.xhttp2.transform.HttpSchedulersTransformer;

import io.reactivex.ObservableTransformer;

/**
 * <p>描述：线程调度工具</p>
 *
 * @author xuexiang
 * @since 2018/6/21 下午2:10
 */
public class RxSchedulers {

    private RxSchedulers() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 订阅发生在主线程 （  ->  -> main)
     * 使用compose操作符
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> _main() {
        return new HttpSchedulersTransformer<>(SchedulerType._main);
    }

    /**
     * 订阅发生在io线程 （  ->  -> io)
     * 使用compose操作符
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> _io() {
        return new HttpSchedulersTransformer<>(SchedulerType._io);
    }


    /**
     * 处理在io线程，订阅发生在主线程（ -> io -> main)
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> _io_main() {
        return new HttpSchedulersTransformer<>(SchedulerType._io_main);
    }


    /**
     * 处理在io线程，订阅也发生在io线程（ -> io -> io)
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> _io_io() {
        return new HttpSchedulersTransformer<>(SchedulerType._io_io);
    }

}
