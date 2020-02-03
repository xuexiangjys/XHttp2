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

import com.xuexiang.xhttp2.callback.CallBack;
import com.xuexiang.xhttp2.subsciber.DownloadSubscriber;
import com.xuexiang.xhttp2.transform.HandleErrTransformer;
import com.xuexiang.xhttp2.transform.func.RetryExceptionFunc;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * <p>描述：下载请求</p>
 *
 * @author xuexiang
 * @since 2018/6/25 上午12:53
 */
@SuppressWarnings(value = {"unchecked"})
public class DownloadRequest extends BaseRequest<DownloadRequest> {

    public DownloadRequest(String url) {
        super(url);
    }

    /**
     * 下载文件的路径
     */
    private String mSavePath;
    /**
     * 下载文件的名称
     */
    private String mSaveName;

    /**
     * 是否使用baseUrl
     */
    private boolean mIsUseBaseUrl;

    /**
     * 设置下载文件路径<br>
     * SD卡不存在: /data/data/com.xxx.xxx/files;<br>
     * 存在: /storage/emulated/0/Android/data/com.xxx.xxx/files;
     */
    public DownloadRequest savePath(String savePath) {
        mSavePath = savePath;
        return this;
    }

    /**
     * 设置下载文件名称<br>
     * 默认名字是时间戳生成的<br>
     */
    public DownloadRequest saveName(String saveName) {
        mSaveName = saveName;
        return this;
    }

    public DownloadRequest isUseBaseUrl(boolean isUseBaseUrl) {
        mIsUseBaseUrl = isUseBaseUrl;
        return this;
    }

    @Override
    public <T> Disposable execute(CallBack<T> callBack) {
        return (Disposable) build().generateRequest().compose(new ObservableTransformer<ResponseBody, ResponseBody>() {
            @Override
            public ObservableSource<ResponseBody> apply(@NonNull Observable<ResponseBody> upstream) {
                if (mIsSyncRequest) {
                    return upstream;//.observeOn(AndroidSchedulers.mainThread());
                } else {
                    return upstream.subscribeOn(Schedulers.io())
                            .unsubscribeOn(Schedulers.io())
                            .observeOn(Schedulers.computation());
                }
            }
        }).compose(new HandleErrTransformer())
                .retryWhen(new RetryExceptionFunc(mRetryCount, mRetryDelay, mRetryIncreaseDelay))
                .subscribeWith(new DownloadSubscriber(mSavePath, mSaveName, callBack));
    }

    @Override
    protected Observable<ResponseBody> generateRequest() {
        if (mIsUseBaseUrl) {
            return mApiManager.downloadFile(getBaseUrl() + getUrl());
        } else {
            return mApiManager.downloadFile(mUrl);
        }
    }
}
