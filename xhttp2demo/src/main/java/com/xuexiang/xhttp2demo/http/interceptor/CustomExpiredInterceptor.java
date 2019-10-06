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

package com.xuexiang.xhttp2demo.http.interceptor;

import android.annotation.SuppressLint;

import com.xuexiang.xhttp2.XHttpProxy;
import com.xuexiang.xhttp2.annotation.ThreadType;
import com.xuexiang.xhttp2.interceptor.BaseExpiredInterceptor;
import com.xuexiang.xhttp2.logs.HttpLog;
import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.model.ExpiredInfo;
import com.xuexiang.xhttp2.utils.HttpUtils;
import com.xuexiang.xhttp2demo.entity.LoginInfo;
import com.xuexiang.xhttp2demo.entity.User;
import com.xuexiang.xhttp2demo.http.TestApi;
import com.xuexiang.xhttp2demo.http.subscriber.NoTipRequestSubscriber;
import com.xuexiang.xhttp2demo.manager.TokenManager;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.net.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

import static com.xuexiang.xhttp2demo.http.interceptor.CustomExpiredInterceptor.ExpiredType.KEY_TOKEN_EXPIRED;
import static com.xuexiang.xhttp2demo.http.interceptor.CustomExpiredInterceptor.ExpiredType.KEY_UNREGISTERED_USER;

/**
 * 处理各种失效响应处理：包括token过期、账号异地登录、时间戳过期、签名sign错误等
 *
 * @author xuexiang
 * @since 2018/8/7 上午9:41
 */
public class CustomExpiredInterceptor extends BaseExpiredInterceptor {

    /**
     * Token失效，需要重新获取token的code码
     */
    public static final int TOKEN_INVALID = 100;
    /**
     * 缺少Token
     */
    public static final int TOKEN_MISSING = TOKEN_INVALID + 1;
    /**
     * 认证失败
     */
    public static final int AUTH_ERROR = TOKEN_MISSING + 1;

    @Override
    protected ExpiredInfo isResponseExpired(Response oldResponse, String bodyString) {
        int code = JSONUtils.getInt(bodyString.toLowerCase(), "code", 0);
        ExpiredInfo expiredInfo = new ExpiredInfo(code);
        switch (code) {
            case TOKEN_INVALID:
            case TOKEN_MISSING:
                expiredInfo.setExpiredType(KEY_TOKEN_EXPIRED)
                        .setBodyString(bodyString);
                break;
            case AUTH_ERROR:
                expiredInfo.setExpiredType(KEY_UNREGISTERED_USER)
                        .setBodyString(bodyString);
                break;
            default:
                break;
        }
        return expiredInfo;
    }

    @SuppressLint("CheckResult")
    @Override
    protected Response responseExpired(Response oldResponse, Chain chain, ExpiredInfo expiredInfo) {
        switch(expiredInfo.getExpiredType()) {
            case KEY_TOKEN_EXPIRED:
                User user = TokenManager.getInstance().getLoginUser();
                if (user != null) {
                    final boolean[] isGetNewToken = {false};
                    HttpLog.e("正在重新获取token...");
                    XHttpProxy.proxy(TestApi.IAuthorization.class, ThreadType.IN_THREAD)
                            .login(user.getLoginName(), user.getPassword())
                            .subscribeWith(new NoTipRequestSubscriber<LoginInfo>() {
                                @Override
                                protected void onSuccess(LoginInfo loginInfo) {
                                    TokenManager.getInstance()
                                            .setToken(loginInfo.getToken())
                                            .setLoginUser(loginInfo.getUser());
                                    isGetNewToken[0] = true;
                                    HttpLog.e("重新获取token成功：" + loginInfo.getToken());
                                }
                            });
                    if (isGetNewToken[0]) {
                        try {
                            HttpLog.e("使用新的token重新进行请求...");
                            return chain.proceed(HttpUtils.updateUrlParams(chain.request(), "token", TokenManager.getInstance().getToken()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    XRouter.getInstance().build("/xhttp/login").navigation();
                    return HttpUtils.getErrorResponse(oldResponse, expiredInfo.getCode(), "请先进行登录！");
                }
                break;
            case KEY_UNREGISTERED_USER:
                return HttpUtils.getErrorResponse(oldResponse, expiredInfo.getCode(), "非法用户登录！");
            default:
                break;
        }
        return null;
    }

    /**
     * 失效类型
     */
    static final class ExpiredType {

        /**
         * token失效
         */
        static final int KEY_TOKEN_EXPIRED = 10;

        /**
         * 未注册的用户
         */
        static final int KEY_UNREGISTERED_USER = KEY_TOKEN_EXPIRED + 1;
    }
}
