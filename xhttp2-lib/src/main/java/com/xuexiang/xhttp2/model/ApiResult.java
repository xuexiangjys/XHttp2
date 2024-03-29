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

import com.google.gson.annotations.SerializedName;
import com.xuexiang.xhttp2.utils.ApiUtils;

/**
 * 提供的默认的标注返回api
 *
 * @author xuexiang
 * @since 2018/5/22 下午4:22
 */
public class ApiResult<T> implements IApiResult<T> {
    public final static String CODE = "Code";
    public final static String MSG = "Msg";
    public final static String DATA = "Data";

    @SerializedName(value = CODE, alternate = {"code"})
    private int Code;
    @SerializedName(value = MSG, alternate = {"msg"})
    private String Msg;
    @SerializedName(value = DATA, alternate = {"data"})
    private T Data;

    @Override
    public int getCode() {
        return Code;
    }

    public ApiResult setCode(int code) {
        Code = code;
        return this;
    }

    @Override
    public String getMsg() {
        return Msg;
    }

    public ApiResult setMsg(String msg) {
        Msg = msg;
        return this;
    }

    /**
     * 设置请求响应的数据，非严格模式下需要使用【很关键】
     *
     * @param data 请求响应的数据
     */
    @Override
    public void setData(T data) {
        Data = data;
    }

    /**
     * 获取请求响应的数据，自定义api的时候需要重写【很关键】
     *
     * @return 请求响应的数据
     */
    @Override
    public T getData() {
        return Data;
    }

    /**
     * 是否请求成功,自定义api的时候需要重写【很关键】
     *
     * @return true: 请求成功；false：请求失败
     */
    @Override
    public boolean isSuccess() {
        return getCode() == ApiUtils.getSuccessCode();
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "Code='" + Code + '\'' +
                ", Msg='" + Msg + '\'' +
                ", Data=" + Data +
                '}';
    }
}
