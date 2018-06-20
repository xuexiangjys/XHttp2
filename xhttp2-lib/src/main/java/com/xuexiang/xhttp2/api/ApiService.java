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

package com.xuexiang.xhttp2.api;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 通用的的api接口
 *
 * @author xuexiang
 * @since 2018/6/20 下午4:43
 */
public interface ApiService {

    /**
     * POST表单请求
     *
     * @param url  请求地址
     * @param maps
     * @return
     */
    @POST
    @FormUrlEncoded
    Observable<ResponseBody> post(@Url String url, @FieldMap Map<String, Object> maps);

    /**
     * POST Object请求
     *
     * @param url    请求地址
     * @param object
     * @return
     */
    @POST
    Observable<ResponseBody> postBody(@Url String url, @Body Object object);

    /**
     * POST Json请求
     *
     * @param url      请求地址
     * @param jsonBody
     * @return
     */
    @POST
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<ResponseBody> postJson(@Url String url, @Body RequestBody jsonBody);

    /**
     * POST RequestBody 请求
     *
     * @param url  请求地址
     * @param body
     * @return
     */
    @POST
    Observable<ResponseBody> postBody(@Url String url, @Body RequestBody body);

    /**
     * GET请求
     *
     * @param url  请求地址
     * @param maps
     * @return
     */
    @GET
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, Object> maps);

    /**
     * DELETE请求
     *
     * @param url  请求地址
     * @param maps
     * @return
     */
    @DELETE
    Observable<ResponseBody> delete(@Url String url, @QueryMap Map<String, Object> maps);

    @DELETE
    Observable<ResponseBody> deleteBody(@Url String url, @Body Object object);

    @DELETE
    Observable<ResponseBody> deleteBody(@Url String url, @Body RequestBody body);

    @DELETE
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<ResponseBody> deleteJson(@Url String url, @Body RequestBody jsonBody);

    /**
     * PUT请求
     *
     * @param url  请求地址
     * @param maps
     * @return
     */
    @PUT
    Observable<ResponseBody> put(@Url String url, @QueryMap Map<String, Object> maps);

    @POST
    Observable<ResponseBody> putBody(@Url String url, @Body Object object);

    /**
     * 上传文件
     *
     * @param url
     * @param maps
     * @return
     */
    @Multipart
    @POST
    Observable<ResponseBody> uploadFiles(@Url String url, @PartMap Map<String, RequestBody> maps);

    /**
     * POST 上传文件【目前使用】
     *
     * @param path  上传文件路径
     * @param parts 文件
     * @return
     */
    @Multipart
    @POST
    Observable<ResponseBody> uploadFiles(@Url String path, @Part List<MultipartBody.Part> parts);

    /**
     * GET 下载文件
     *
     * @param fileUrl
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);


}
