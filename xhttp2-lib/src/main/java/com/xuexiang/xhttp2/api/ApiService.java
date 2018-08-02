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
 * @since 2018/6/20 下午9:43
 */
public interface ApiService {

    //==========================//
    //         POST请求          //
    // =========================//

    @POST
    @FormUrlEncoded
    Observable<ResponseBody> post(@Url String url, @FieldMap Map<String, Object> maps);

    @POST
    Observable<ResponseBody> postBody(@Url String url, @Body Object object);

    @POST
    Observable<ResponseBody> postBody(@Url String url, @Body RequestBody body);

    @POST
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<ResponseBody> postJson(@Url String url, @Body RequestBody jsonBody);

    //==========================//
    //         GET请求           //
    // =========================//

    @GET
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, Object> maps);

    //==========================//
    //         DELETE请求        //
    // =========================//

    @DELETE
    Observable<ResponseBody> delete(@Url String url, @QueryMap Map<String, Object> maps);

    @DELETE
    Observable<ResponseBody> deleteBody(@Url String url, @Body Object object);

    @DELETE
    Observable<ResponseBody> deleteBody(@Url String url, @Body RequestBody body);

    @DELETE
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<ResponseBody> deleteJson(@Url String url, @Body RequestBody jsonBody);


    //==========================//
    //         PUT请求           //
    // =========================//

    @PUT
    Observable<ResponseBody> put(@Url String url, @QueryMap Map<String, Object> maps);

    @PUT
    Observable<ResponseBody> putBody(@Url String url, @Body Object object);

    @PUT
    Observable<ResponseBody> putBody(@Url String url, @Body RequestBody body);

    @PUT
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<ResponseBody> putJson(@Url String url, @Body RequestBody jsonBody);


    //==========================//
    //       文件上传下载         //
    // =========================//

    @Multipart
    @POST
    Observable<ResponseBody> uploadFiles(@Url String url, @PartMap Map<String, RequestBody> maps);

    @Multipart
    @POST
    Observable<ResponseBody> uploadFiles(@Url String url, @Part List<MultipartBody.Part> parts);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);


}
