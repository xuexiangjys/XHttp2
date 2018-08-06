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

package com.xuexiang.xhttp2demo.http;

import com.xuexiang.xhttp2.annotation.NetMethod;
import com.xuexiang.xhttp2.annotation.RequestParams;
import com.xuexiang.xhttp2.model.XHttpRequest;
import com.xuexiang.xhttp2demo.entity.LoginInfo;
import com.xuexiang.xhttp2demo.entity.User;

import io.reactivex.Observable;

/**
 * 测试api协议
 *
 * @author xuexiang
 * @since 2018/7/16 下午6:48
 */
public class TestApi {

    @RequestParams(url = "/user/addUser", accessToken = false)
    public static class UserService_AddUser extends XHttpRequest {

        /**
         *
         */
        public User request;

        @Override
        protected Boolean getResponseEntityType() {
            return null;
        }
    }


    /**
     * 订单
     */
    public interface IOrder {
        /**
         * 购买书
         *
         * @param bookId 图书ID
         * @param userId 用户ID
         * @param number 购买数量
         */
        @NetMethod(ParameterNames = {"bookId", "userId", "number"}, Url = "/order/addOrder/", AccessToken = false)
        Observable<Boolean> buyBook(int bookId, int userId, int number);
    }


    /**
     * 身份验证
     */
    public interface IAuthorization {
        /**
         * 登录获取token
         *
         * @param loginName 用户名
         * @param password 密码
         */
        @NetMethod(ParameterNames = {"loginName", "password"}, Url = "/authorization/login/", AccessToken = false)
        Observable<LoginInfo> login(String loginName, String password);

    }

}
