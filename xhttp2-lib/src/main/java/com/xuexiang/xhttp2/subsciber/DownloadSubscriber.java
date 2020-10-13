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

package com.xuexiang.xhttp2.subsciber;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import android.text.TextUtils;

import com.xuexiang.xhttp2.callback.CallBack;
import com.xuexiang.xhttp2.callback.DownloadProgressCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.logs.HttpLog;
import com.xuexiang.xhttp2.utils.PathUtils;
import com.xuexiang.xhttp2.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;

import static com.xuexiang.xhttp2.exception.ApiException.ERROR.DOWNLOAD_ERROR;

/**
 * 下载的订阅者
 *
 * @author xuexiang
 * @since 2018/6/25 上午12:54
 */
public class DownloadSubscriber<ResponseBody extends okhttp3.ResponseBody> extends BaseSubscriber<ResponseBody> {
    private String path;
    private String name;
    private CallBack mCallBack;
    private static String APK_CONTENT_TYPE = "application/vnd.android.package-archive";
    private static String PNG_CONTENT_TYPE = "image/png";
    private static String JPG_CONTENT_TYPE = "image/jpg";
    private static String MP4_CONTENT_TYPE = "video/mp4";
    private long lastRefreshUiTime;

    public DownloadSubscriber(String path, String name, CallBack callBack) {
        super();
        this.path = path;
        this.name = name;
        this.mCallBack = callBack;
        this.lastRefreshUiTime = System.currentTimeMillis();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mCallBack != null) {
            mCallBack.onStart();
        }
    }

    @Override
    public final void onComplete() {

    }

    @Override
    public void onError(final ApiException e) {
        finalOnError(e);
    }

    @Override
    protected void onSuccess(ResponseBody responseBody) {
        if (Utils.isScopedStorageMode() && Utils.isPublicPath(path)) {
            writeResponseBodyToDisk29Api(path, name, responseBody);
        } else {
            writeResponseBodyToDisk(path, name, responseBody);
        }
    }


    /**
     * 将响应的请求直接写入到磁盘中
     *
     * @param path 保存的文件目录
     * @param name 保存的文件名
     * @param body 文件响应
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void writeResponseBodyToDisk29Api(String path, String name, okhttp3.ResponseBody body) {
        name = checkFileName(name, body);
        HttpLog.i("path:-->" + path + ", name:" + name);
        InputStream inputStream = null;
        Uri fileUri;
        OutputStream outputStream = null;
        try {
            byte[] bytes = new byte[1024 * 128];
            final long fileSize = body.contentLength();
            long downloadSize = 0;
            HttpLog.i("file length: " + fileSize);
            inputStream = body.byteStream();
            fileUri = Utils.getFileUri(path, name, body.contentType());
            if (fileUri == null) {
                throw new FileNotFoundException("fileUri is null!");
            }
            outputStream = Utils.openOutputStream(fileUri);
            final CallBack callBack = mCallBack;
            int read;
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
                downloadSize += read;
                //下载进度
                updateDownLoadProgress(fileSize, downloadSize, callBack);
            }
            outputStream.flush();
            HttpLog.i("file downloaded: " + downloadSize + " of " + fileSize);

            handleDownLoadFinished(PathUtils.getFilePathByUri(fileUri), callBack);
        } catch (Throwable e) {
            onError(e);
        } finally {
            Utils.closeIO(outputStream, inputStream);
        }
    }


    /**
     * 将响应的请求直接写入到磁盘中
     *
     * @param path 保存的文件目录
     * @param name 保存的文件名
     * @param body 文件响应
     */
    private void writeResponseBodyToDisk(String path, String name, okhttp3.ResponseBody body) {
        name = checkFileName(name, body);
        path = checkFilePath(path, name);

        HttpLog.i("path:-->" + path);
        File downLoadFile = new File(path);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            byte[] bytes = new byte[1024 * 128];
            final long fileSize = body.contentLength();
            long downloadSize = 0;
            HttpLog.i("file length: " + fileSize);
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(downLoadFile);
            final CallBack callBack = mCallBack;
            int read;
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
                downloadSize += read;
                //下载进度
                updateDownLoadProgress(fileSize, downloadSize, callBack);
            }
            outputStream.flush();
            HttpLog.i("file downloaded: " + downloadSize + " of " + fileSize);

            handleDownLoadFinished(path, callBack);
        } catch (Throwable e) {
            onError(e);
        } finally {
            Utils.closeIO(outputStream, inputStream);
        }
    }

    /**
     * 更新下载进度
     *
     * @param fileSize     文件的总大小
     * @param downloadSize 文件已下载的大小
     * @param callBack
     */
    @SuppressLint("CheckResult")
    private void updateDownLoadProgress(final long fileSize, long downloadSize, final CallBack callBack) {
        HttpLog.d("file download: " + downloadSize + " of " + fileSize);
        float progress = downloadSize * 1.0f / fileSize;
        long curTime = System.currentTimeMillis();
        //每200毫秒刷新一次数据,防止频繁更新进度
        if (curTime - lastRefreshUiTime >= 200 || progress == 1.0f) {
            if (callBack != null) {
                final long finalFileSizeDownloaded = downloadSize;
                Observable.just(finalFileSizeDownloaded)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                                if (callBack instanceof DownloadProgressCallBack) {
                                    ((DownloadProgressCallBack) callBack).update(finalFileSizeDownloaded, fileSize, finalFileSizeDownloaded == fileSize);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {

                            }
                        });
            }
            lastRefreshUiTime = System.currentTimeMillis();
        }
    }

    @SuppressLint("CheckResult")
    private void handleDownLoadFinished(String path, final CallBack callBack) {
        if (callBack != null) {
            final String finalPath = path;
            Observable.just(finalPath)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(@NonNull String s) throws Exception {
                            if (callBack instanceof DownloadProgressCallBack) {
                                ((DownloadProgressCallBack) callBack).onComplete(finalPath);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {

                        }
                    });
            HttpLog.i("file downloaded: is success");
        }
    }

    /**
     * 检查文件名，补全后缀
     *
     * @param name
     * @param body
     * @return
     */
    private String checkFileName(String name, okhttp3.ResponseBody body) {
        HttpLog.d("contentType:>>>>" + body.contentType());
        if (!TextUtils.isEmpty(name)) {//text/html; charset=utf-8
            if (!name.contains(".")) {
                name = name + getFileSuffix(body);
            }
        } else {
            name = System.currentTimeMillis() + getFileSuffix(body);
        }
        return name;
    }

    /**
     * 检查文件的保存路径
     *
     * @param path 保存目录路径
     * @param name 保存的文件名
     * @return 文件的保存路径
     */
    private String checkFilePath(String path, String name) {
        if (path == null) {
            path = Utils.getDiskFilesDir(name);
        } else {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            path = path + File.separator + name;
            path = path.replaceAll("//", "/");
        }
        return path;
    }

    /**
     * 获取下载文件的后缀名
     *
     * @param body
     * @return
     */
    private String getFileSuffix(okhttp3.ResponseBody body) {
        MediaType contentType = body.contentType();
        if (contentType == null) {
            return ".txt";
        }
        String fileSuffix;
        String type = contentType.toString();
        if (type.equals(APK_CONTENT_TYPE)) {
            fileSuffix = ".apk";
        } else if (type.equals(PNG_CONTENT_TYPE)) {
            fileSuffix = ".png";
        } else if (type.equals(JPG_CONTENT_TYPE)) {
            fileSuffix = ".jpg";
        } else if (type.equals(MP4_CONTENT_TYPE)) {
            fileSuffix = ".mp4";
        } else {
            fileSuffix = "." + contentType.subtype();
        }
        return fileSuffix;
    }

    @SuppressLint("CheckResult")
    private void finalOnError(final Throwable e) {
        if (mCallBack == null) {
            return;
        }
        Observable.just(new ApiException(e, DOWNLOAD_ERROR))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ApiException>() {
                    @Override
                    public void accept(@NonNull ApiException e) throws Exception {
                        if (mCallBack != null) {
                            mCallBack.onError(e);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                });
    }
}
