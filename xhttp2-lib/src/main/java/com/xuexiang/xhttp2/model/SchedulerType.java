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

package com.xuexiang.xhttp2.model;

/**
 * 线程类型
 *
 * @author xuexiang
 * @since 2018/6/25 上午12:02
 */
public enum SchedulerType {

    /**
     * 订阅发生在主线程 （  ->  -> main)
     */
    _main,
    /**
     * 订阅发生在io线程 （  ->  -> io)
     */
    _io,
    /**
     * 处理在io线程，订阅发生在主线程（ -> io -> main)
     */
    _io_main,
    /**
     * 处理在io线程，订阅也发生在io线程（ -> io -> io)
     */
    _io_io,
}
