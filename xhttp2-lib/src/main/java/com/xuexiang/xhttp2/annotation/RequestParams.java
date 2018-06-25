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

import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.cache.model.CacheMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 网络请求实体的网络请求配置的注解
 *
 * @author xuexiang
 * @since 2018/5/22 下午3:23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestParams {
    /**
     * @return 基础请求地址
     */
    String baseUrl() default "";

    /**
     * @return 请求网络接口地址
     */
    String url() default "";

    /**
     * @return 请求超时时间
     */
    long timeout() default XHttp.DEFAULT_TIMEOUT_MILLISECONDS;

    /**
     * @return 请求是否需要验证Token
     */
    boolean accessToken() default true;

    /**
     * @return 缓存模式
     */
    CacheMode cacheMode() default CacheMode.NO_CACHE;

}
