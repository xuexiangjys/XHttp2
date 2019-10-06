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

package com.xuexiang.xhttp2.interceptor;

import com.xuexiang.xhttp2.model.ExpiredInfo;

import okhttp3.Response;

/**
 * 判断响应是否失效的处理拦截器<br>
 * 继承后扩展各种失效响应处理：包括token过期、账号异地登录、时间戳过期、签名sign错误等<br>
 *
 * @author xuexiang
 * @since 2018/6/19 下午11:11
 */
public abstract class BaseExpiredInterceptor extends BaseResponseInterceptor {

    @Override
    protected Response onAfterRequest(Response response, Chain chain, String bodyString) {
        ExpiredInfo expiredInfo = isResponseExpired(response, bodyString);
        if (expiredInfo.isExpired()) {
            Response tmp = responseExpired(response, chain, expiredInfo);
            if (tmp != null) {
                return tmp;
            }
        }
        return response;
    }


    /**
     * 判断是否是失效的响应
     *
     * @param oldResponse
     * @param bodyString
     * @return {@code true} : 失效 <br>  {@code false} : 有效
     */
    protected abstract ExpiredInfo isResponseExpired(Response oldResponse, String bodyString);

    /**
     * 失效响应的处理
     *
     * @return 获取新的有效请求响应
     */
    protected abstract Response responseExpired(Response oldResponse, Chain chain, ExpiredInfo expiredInfo);



}
