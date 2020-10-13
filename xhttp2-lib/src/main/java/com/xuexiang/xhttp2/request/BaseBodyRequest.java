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

package com.xuexiang.xhttp2.request;

import com.xuexiang.xhttp2.callback.impl.IProgressResponseCallBack;
import com.xuexiang.xhttp2.model.HttpParams;
import com.xuexiang.xhttp2.request.body.UploadProgressRequestBody;
import com.xuexiang.xhttp2.utils.RequestBodyUtils;
import com.xuexiang.xhttp2.utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;

/**
 * body请求的基类
 *
 * @author xuexiang
 * @since 2018/6/24 下午11:16
 */
@SuppressWarnings(value={"unchecked"})
public abstract class BaseBodyRequest<R extends BaseBodyRequest> extends BaseRequest<R> {
    /**
     * 上传的文本内容
     */
    protected String mString;
    /**
     * 上传文本的类型
     */
    protected MediaType mMediaType;
    /**
     * 上传的Json
     */
    protected String mJson;
    /**
     * 上传的字节数据
     */
    protected byte[] mBytes;
    /**
     * 上传的对象
     */
    protected Object mObject;
    /**
     * 自定义的请求体
     */
    protected RequestBody mRequestBody;
    /**
     * 文件上传方式
     */
    private UploadType mUploadType = UploadType.PART;

    /**
     * 构建请求
     *
     * @param url
     */
    public BaseBodyRequest(String url) {
        super(url);
    }

    public R requestBody(RequestBody requestBody) {
        mRequestBody = requestBody;
        return (R) this;
    }

    /**
     * 上传text文本（注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除）
     *
     * @param string 请求数据
     * @return
     */
    public R upString(String string) {
        mString = string;
        mMediaType = MediaType.parse("text/plain");
        return (R) this;
    }

    /**
     * 上传String类型的数据(注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除)
     *
     * @param string    请求数据
     * @param mediaType 请求String数据的mediaType类型
     * @return
     */
    public R upString(String string, String mediaType) {
        mString = string;
        Utils.checkNotNull(mediaType, "MediaType == null");
        mMediaType = MediaType.parse(mediaType);
        return (R) this;
    }

    public R upObject(@Body Object object) {
        mObject = object;
        return (R) this;
    }

    /**
     * 上传Json格式的数据请求（注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除）
     *
     * @param json json数据
     * @return
     */
    public R upJson(String json) {
        mJson = json;
        return (R) this;
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    public R upBytes(byte[] bs) {
        mBytes = bs;
        return (R) this;
    }

    //===============上传文件====================//

    /**
     * 上传文件
     *
     * @param key              关键字
     * @param file             文件
     * @param responseCallBack 上传进度条回调接口
     */
    public R uploadFile(String key, File file, IProgressResponseCallBack responseCallBack) {
        mParams.put(key, file, responseCallBack);
        return (R) this;
    }

    /**
     * 上传文件
     *
     * @param key              关键字
     * @param file             文件
     * @param fileName         文件名
     * @param responseCallBack 上传进度条回调接口
     */
    public R uploadFile(String key, File file, String fileName, IProgressResponseCallBack responseCallBack) {
        mParams.put(key, file, fileName, responseCallBack);
        return (R) this;
    }

    /**
     * 上传文件流
     *
     * @param key              关键字
     * @param stream           文件流
     * @param fileName         文件名
     * @param responseCallBack 上传进度条回调接口
     */
    public R uploadFile(String key, InputStream stream, String fileName, IProgressResponseCallBack responseCallBack) {
        mParams.put(key, stream, fileName, responseCallBack);
        return (R) this;
    }

    /**
     * 上传文件bytes数组
     *
     * @param key              关键字
     * @param bytes            bytes数组
     * @param fileName         文件名
     * @param responseCallBack 上传进度条回调接口
     */
    public R uploadFile(String key, byte[] bytes, String fileName, IProgressResponseCallBack responseCallBack) {
        mParams.put(key, bytes, fileName, responseCallBack);
        return (R) this;
    }

    /**
     * 上传多个文件
     *
     * @param key              关键字
     * @param files            文件集合
     * @param responseCallBack 上传进度条回调接口
     */
    public R uploadFiles(String key, List<File> files, IProgressResponseCallBack responseCallBack) {
        mParams.putFileParams(key, files, responseCallBack);
        return (R) this;
    }

    /**
     * 上传文件的方式，默认part方式上传
     */
    public <T> R uploadType(UploadType uploadtype) {
        mUploadType = uploadtype;
        return (R) this;
    }

    @Override
    protected Observable<ResponseBody> generateRequest() {
        if (mRequestBody != null) { //自定义的请求体
            return mApiManager.postBody(getUrl(), mRequestBody);
        } else if (mJson != null) {//上传的Json
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), mJson);
            return mApiManager.postJson(getUrl(), body);
        } else if (mObject != null) {//自定义的请求object
            return mApiManager.postBody(getUrl(), mObject);
        } else if (mString != null) {//上传的文本内容
            RequestBody body = RequestBody.create(mMediaType, mString);
            return mApiManager.postBody(getUrl(), body);
        } else if (mBytes != null) {//上传的字节数据
            RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), mBytes);
            return mApiManager.postBody(getUrl(), body);
        }
        if (mParams.fileParamsMap.isEmpty()) {
            return mApiManager.post(getUrl(), mParams.urlParamsMap);
        } else {
            if (mUploadType == UploadType.PART) {//part方式上传
                return uploadFilesWithParts();
            } else {//body方式上传
                return uploadFilesWithBodys();
            }
        }
    }

    public enum UploadType {
        /**
         * MultipartBody.Part方式上传
         */
        PART,
        /**
         * Map RequestBody方式上传
         */
        BODY
    }

    protected Observable<ResponseBody> uploadFilesWithParts() {
        List<MultipartBody.Part> parts = new ArrayList<>();
        //拼接参数键值对
        for (Map.Entry<String, Object> mapEntry : mParams.urlParamsMap.entrySet()) {
            parts.add(MultipartBody.Part.createFormData(mapEntry.getKey(), String.valueOf(mapEntry.getValue())));
        }
        //拼接文件
        for (Map.Entry<String, List<HttpParams.FileWrapper>> entry : mParams.fileParamsMap.entrySet()) {
            List<HttpParams.FileWrapper> fileValues = entry.getValue();
            for (HttpParams.FileWrapper fileWrapper : fileValues) {
                MultipartBody.Part part = addFile(entry.getKey(), fileWrapper);
                parts.add(part);
            }
        }
        return mApiManager.uploadFiles(getUrl(), parts);
    }

    protected Observable<ResponseBody> uploadFilesWithBodys() {
        Map<String, RequestBody> bodyMap = new HashMap<>();
        //拼接参数键值对
        for (Map.Entry<String, Object> mapEntry : mParams.urlParamsMap.entrySet()) {
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(mapEntry.getValue()));
            bodyMap.put(mapEntry.getKey(), body);
        }
        //拼接文件
        for (Map.Entry<String, List<HttpParams.FileWrapper>> entry : mParams.fileParamsMap.entrySet()) {
            List<HttpParams.FileWrapper> fileValues = entry.getValue();
            for (HttpParams.FileWrapper fileWrapper : fileValues) {
                RequestBody requestBody = getRequestBody(fileWrapper);
                UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, fileWrapper.responseCallBack);
                bodyMap.put(entry.getKey(), uploadProgressRequestBody);
            }
        }
        return mApiManager.uploadFiles(getUrl(), bodyMap);
    }

    //文件方式
    private MultipartBody.Part addFile(String key, HttpParams.FileWrapper fileWrapper) {
        RequestBody requestBody = getRequestBody(fileWrapper);
        Utils.checkNotNull(requestBody, "requestBody==null fileWrapper.file must is File/InputStream/byte[]");
        if (fileWrapper.responseCallBack != null) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, fileWrapper.responseCallBack);
            return MultipartBody.Part.createFormData(key, fileWrapper.fileName, uploadProgressRequestBody);
        } else {
            return MultipartBody.Part.createFormData(key, fileWrapper.fileName, requestBody);
        }
    }

    private RequestBody getRequestBody(HttpParams.FileWrapper fileWrapper) {
        RequestBody requestBody = null;
        if (fileWrapper.file instanceof File) {
            requestBody = RequestBody.create(fileWrapper.contentType, (File) fileWrapper.file);
        } else if (fileWrapper.file instanceof InputStream) {
            requestBody = RequestBodyUtils.create(fileWrapper.contentType, (InputStream) fileWrapper.file);
        } else if (fileWrapper.file instanceof byte[]) {
            requestBody = RequestBody.create(fileWrapper.contentType, (byte[]) fileWrapper.file);
        }
        return requestBody;
    }
}
