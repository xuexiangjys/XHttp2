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

package com.xuexiang.xhttp2.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 线程调度类型
 *
 * @author xuexiang
 * @since 2018/5/22 下午3:24
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
@StringDef({ThreadType.TO_MAIN, ThreadType.TO_IO, ThreadType.IN_THREAD})
public @interface ThreadType {

    /**
     * -> 网络请求前 -> 网络请求中 -> 网络请求响应<br>
     * -> main -> io -> main<br>
     * 【注意】请确保网络请求在主线程中【实质是异步请求(切换到io线程)，且响应的线程又切换至主线程】
     */
    String TO_MAIN = "executeToMain";
    /**
     * -> 网络请求前 -> 网络请求中 -> 网络请求响应<br>
     * -> main -> io -> io<br>
     * 【注意】请确保网络请求在主线程中【实质是异步请求(切换到io线程)，不过响应的线程不变，还是之前请求的那个io线程】
     */
    String TO_IO = "executeToIO";
    /**
     * -> 网络请求前 -> 网络请求中 -> 网络请求响应<br>
     * -> io -> io -> io<br>
     * 【注意】请确保网络请求在子线程中才可以使用该类型【实质是不做任何线程调度】
     */
    String IN_THREAD = "executeInThread";
}
