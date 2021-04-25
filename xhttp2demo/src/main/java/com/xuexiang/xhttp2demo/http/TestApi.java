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
import com.xuexiang.xhttp2.cache.model.CacheMode;
import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.model.XHttpRequest;
import com.xuexiang.xhttp2demo.entity.Book;
import com.xuexiang.xhttp2demo.entity.LoginInfo;
import com.xuexiang.xhttp2demo.entity.User;
import com.xuexiang.xhttp2demo.http.request.CustomApiResult;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.GET;

import static com.xuexiang.xhttp2.annotation.NetMethod.FORM_BODY;
import static com.xuexiang.xhttp2.annotation.NetMethod.GET;
import static com.xuexiang.xhttp2.annotation.NetMethod.JSON_OBJECT;

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

    @RequestParams(url = "/user/findUsers", accessToken = false)
    public static class UserService_findUsers extends XHttpRequest {
        /**
         * 第几页数
         */
        public int pageNum;
        /**
         * 每页的数量
         */
        public int pageSize;

        @Override
        protected List<User> getResponseEntityType() {
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
        @NetMethod(parameterNames = {"bookId", "userId", "number"}, url = "/order/addOrder/", accessToken = false)
        Observable<Boolean> buyBook(int bookId, int userId, int number);
    }

    /**
     * 图书管理
     */
    public interface IBook {
        /**
         * 获取图书
         *
         * @param pageNum  第几页数
         * @param pageSize 每页的数量
         */
        @NetMethod(parameterNames = {"pageNum", "pageSize"}, paramType = FORM_BODY, url = "/book/findBooks/", cacheMode = CacheMode.FIRST_CACHE, cacheTime = 120, accessToken = false)
        Observable<List<Book>> getBooks(int pageNum, int pageSize);

        /**
         * 获取所有图书
         */
        @NetMethod(action = GET, url = "/book/getAllBook", cacheMode = CacheMode.FIRST_CACHE, accessToken = false)
        Observable<List<Book>> getAllBooks();
    }


    /**
     * 身份验证
     */
    public interface IAuthorization {
        /**
         * 登录获取token
         *
         * @param loginName 用户名
         * @param password  密码
         */
        @NetMethod(parameterNames = {"loginName", "password"}, url = "/authorization/login/", accessToken = false)
        Observable<LoginInfo> login(String loginName, String password);

    }


    /**
     * 使用的是retrofit的接口定义
     */
    public interface LoginService {
        @POST("/authorization/login/")
        @Headers({"Content-Type: application/json", "Accept: application/json"})
        Observable<ApiResult<LoginInfo>> login(@Body RequestBody jsonBody);


        @POST("/authorization/login/")
        @Headers({"Content-Type: application/json", "Accept: application/json"})
        Observable<ApiResult<LoginInfo>> login(@Body User user);
    }


    /**
     * 使用的是retrofit的接口定义
     */
    public interface UserService {
        @POST("/user/registerUser/")
        @Headers({"Content-Type: application/json", "Accept: application/json"})
        Observable<ApiResult<Boolean>> registerUser(@Body RequestBody jsonBody);


        @POST("/user/registerUser/")
        @Headers({"Content-Type: application/json", "Accept: application/json"})
        Observable<ApiResult> register(@Body User user);

        @GET("/test/testCustomResult")
        Observable<CustomApiResult<Boolean>> testCustomResult();
    }


    /**
     * 测试接口
     */
    public interface ITestService {
        /**
         * 测试JsonObject
         */
        @NetMethod(url = "/test/testJsonObject/", paramType = JSON_OBJECT, cacheMode = CacheMode.FIRST_CACHE, accessToken = false)
        Observable<User> testJsonObject(User user);

        /**
         * 测试JsonObject数组
         */
        @NetMethod(url = "/test/testJsonObjectArray/", paramType = JSON_OBJECT, cacheMode = CacheMode.FIRST_CACHE, accessToken = false)
        Observable<List<User>> testJsonObjectArray(List<User> users);
    }

}
