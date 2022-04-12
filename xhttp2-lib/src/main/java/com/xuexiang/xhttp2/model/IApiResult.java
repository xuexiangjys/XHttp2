/*
 * Copyright (C) 2022 xuexiangjys(xuexiangjys@163.com)
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
 *
 */

package com.xuexiang.xhttp2.model;

/**
 * 请求返回结果的标准格式，自定义api时需重写以下接口
 *
 * @author xuexiang
 * @since 4/12/22 10:40 PM
 */
public interface IApiResult<T> {

    /**
     * 设置请求响应的数据，非严格模式下需要使用【很关键】
     *
     * @param data 请求响应的数据
     */
    void setData(T data);

    /**
     * 获取请求响应的数据，自定义api的时候需要重写【很关键】
     *
     * @return 请求响应的数据
     */
    T getData();

    /**
     * 是否请求成功,自定义api的时候需要重写【很关键】
     *
     * @return true: 请求成功；false：请求失败
     */
    boolean isSuccess();

    /**
     * 获取请求结果码【一般请求出错时用到】
     *
     * @return 请求结果码
     */
    int getCode();

    /**
     * 获取请求错误信息【一般请求出错时用到】
     *
     * @return 请求错误信息
     */
    String getMsg();

}
