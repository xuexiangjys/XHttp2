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
 * 请求失效信息
 *
 * @author xuexiang
 * @since 2018/8/7 上午9:47
 */
public class ExpiredInfo {

    /**
     * 请求是否失效
     */
    private boolean mIsExpired;

    /**
     * 失效的类型
     */
    private int mExpiredType;

    /**
     * 服务器返回的错误码（失效码）
     */
    private int mCode;

    /**
     * 请求的body内容
     */
    private String mBodyString;

    public ExpiredInfo(int code) {
        mCode = code;
    }

    public boolean isExpired() {
        return mIsExpired;
    }

    public ExpiredInfo setExpired(boolean expired) {
        mIsExpired = expired;
        return this;
    }

    public int getExpiredType() {
        return mExpiredType;
    }

    public ExpiredInfo setExpiredType(int expiredType) {
        mExpiredType = expiredType;
        mIsExpired = true;
        return this;
    }

    public int getCode() {
        return mCode;
    }

    public ExpiredInfo setCode(int code) {
        mCode = code;
        return this;
    }

    public String getBodyString() {
        return mBodyString;
    }

    public ExpiredInfo setBodyString(String bodyString) {
        mBodyString = bodyString;
        return this;
    }

}
