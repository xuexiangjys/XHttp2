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

package com.xuexiang.xhttp2.callback;

/**
 * 下载进度回调（主线程，可以直接操作UI）
 *
 * @author xuexiang
 * @since 2018/5/23 上午10:45
 */
public abstract class DownloadProgressCallBack<T> extends CallBack<T> {

    public DownloadProgressCallBack() {
    }

    @Override
    public void onSuccess(T response) {

    }

    /**
     * 更新进度条
     * @param downLoadSize 已经下载的大小
     * @param totalSize 下载文件的总大小
     * @param done
     */
    public abstract void update(long downLoadSize, long totalSize, boolean done);

    public abstract void onComplete(String path);

    @Override
    public void onCompleted() {

    }
}
