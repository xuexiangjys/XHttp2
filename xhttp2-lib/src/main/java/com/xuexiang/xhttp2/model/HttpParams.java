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

import com.xuexiang.xhttp2.callback.impl.IProgressResponseCallBack;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;

/**
 * <p>描述：普通参数</p>
 *
 * @author xuexiang
 * @since 2018/5/22 下午4:21
 */
public class HttpParams implements Serializable {
    /**
     * 普通的键值对参数
     */
    public LinkedHashMap<String, Object> urlParamsMap;
    /**
     * 文件的键值对参数
     */
    public LinkedHashMap<String, List<FileWrapper>> fileParamsMap;

    public HttpParams() {
        init();
    }

    public HttpParams(String key, String value) {
        init();
        put(key, value);
    }

    public HttpParams(Map<String, Object> params) {
        init();
        put(params);
    }

    private void init() {
        urlParamsMap = new LinkedHashMap<>();
        fileParamsMap = new LinkedHashMap<>();
    }

    public HttpParams put(HttpParams params) {
        if (params != null) {
            if (params.urlParamsMap != null && !params.urlParamsMap.isEmpty()) {
                urlParamsMap.putAll(params.urlParamsMap);
            }

            if (params.fileParamsMap != null && !params.fileParamsMap.isEmpty()) {
                fileParamsMap.putAll(params.fileParamsMap);
            }
        }
        return this;
    }

    //===============存放普通键值对参数=====================//

    /**
     * 存放普通键值对参数
     *
     * @param params
     */
    public HttpParams put(Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            urlParamsMap.putAll(params);
        }
        return this;
    }

    /**
     * 存放普通键值对参数
     *
     * @param key
     * @param value
     */
    public HttpParams put(String key, Object value) {
        urlParamsMap.put(key, value);
        return this;
    }

    //===============存放文件键值对参数=====================//


    /**
     * 存放文件键值对参数
     *
     * @param key              关键字
     * @param file             文件
     * @param responseCallBack 上传进度条回调接口
     * @param <T>
     */
    public <T extends File> void put(String key, T file, IProgressResponseCallBack responseCallBack) {
        put(key, file, file.getName(), responseCallBack);
    }

    /**
     * 存放文件键值对参数
     *
     * @param key              关键字
     * @param file             文件
     * @param fileName         文件名
     * @param responseCallBack 上传进度条回调接口
     * @param <T>
     */
    public <T extends File> void put(String key, T file, String fileName, IProgressResponseCallBack responseCallBack) {
        put(key, file, fileName, guessMimeType(fileName), responseCallBack);
    }

    /**
     * 存放文件键值对参数
     *
     * @param key              关键字
     * @param file             文件流
     * @param fileName         文件名
     * @param responseCallBack 上传进度条回调接口
     * @param <T>
     */
    public <T extends InputStream> void put(String key, T file, String fileName, IProgressResponseCallBack responseCallBack) {
        put(key, file, fileName, guessMimeType(fileName), responseCallBack);
    }

    /**
     * 存放文件键值对参数
     *
     * @param key              关键字
     * @param bytes            bytes数组
     * @param fileName         文件名
     * @param responseCallBack 上传进度条回调接口
     */
    public void put(String key, byte[] bytes, String fileName, IProgressResponseCallBack responseCallBack) {
        put(key, bytes, fileName, guessMimeType(fileName), responseCallBack);
    }

    /**
     * 存放多个文件的键值对参数
     *
     * @param key              关键字
     * @param files            文件集合
     * @param responseCallBack 上传进度条回调接口
     * @param <T>
     */
    public <T extends File> void putFileParams(String key, List<T> files, IProgressResponseCallBack responseCallBack) {
        if (key != null && files != null && !files.isEmpty()) {
            for (File file : files) {
                put(key, file, responseCallBack);
            }
        }
    }

    //=======================//
    @SuppressWarnings("unchecked")
    private <T> void put(String key, T content, String fileName, MediaType contentType, IProgressResponseCallBack responseCallBack) {
        if (fileParamsMap != null && key != null) {
            List<FileWrapper> fileWrappers = fileParamsMap.get(key);
            if (fileWrappers == null) {
                fileWrappers = new ArrayList<>();
                fileParamsMap.put(key, fileWrappers);
            }
            fileWrappers.add(new FileWrapper<>(content, fileName, contentType, responseCallBack));
        }
    }

    public void removeUrl(String key) {
        if (urlParamsMap != null) {
            urlParamsMap.remove(key);
        }
    }

    public void removeFile(String key) {
        if (fileParamsMap != null) {
            fileParamsMap.remove(key);
        }
    }

    public void remove(String key) {
        removeUrl(key);
        removeFile(key);
    }

    public void clear() {
        if (urlParamsMap != null) {
            urlParamsMap.clear();
        }
        if (fileParamsMap != null) {
            fileParamsMap.clear();
        }
    }

    /**
     * 解析文件的媒体类型
     *
     * @param path
     * @return
     */
    private MediaType guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        path = path.replace("#", "");   //解决文件名中含有#号异常的问题
        String contentType = fileNameMap.getContentTypeFor(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return MediaType.parse(contentType);
    }

    /**
     * 文件类型的包装类
     */
    public static class FileWrapper<T> {
        public T file;//可以是
        public String fileName;
        public MediaType contentType;
        public long fileSize;
        public IProgressResponseCallBack responseCallBack;

        public FileWrapper(T file, String fileName, MediaType contentType, IProgressResponseCallBack responseCallBack) {
            this.file = file;
            this.fileName = fileName;
            this.contentType = contentType;
            if (file instanceof File) {
                this.fileSize = ((File) file).length();
            } else if (file instanceof byte[]) {
                this.fileSize = ((byte[]) file).length;
            }
            this.responseCallBack = responseCallBack;
        }

        @Override
        public String toString() {
            return "FileWrapper{" + "content=" + file + ", fileName='" + fileName + ", contentType=" + contentType + ", fileSize=" + fileSize + '}';
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, Object> entry : urlParamsMap.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        for (ConcurrentHashMap.Entry<String, List<FileWrapper>> entry : fileParamsMap.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return result.toString();
    }
}