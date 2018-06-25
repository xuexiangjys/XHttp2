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

import android.support.annotation.StringDef;

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
     * -> main -> io -> main
     */
    String TO_MAIN = "executeToMain";
    /**
     * -> 网络请求前 -> 网络请求中 -> 网络请求响应<br>
     * -> main -> io -> io
     */
    String TO_IO = "executeToIO";
    /**
     * -> 网络请求前 -> 网络请求中 -> 网络请求响应<br>
     * -> io -> io -> io
     */
    String IN_THREAD = "executeInThread";
}
