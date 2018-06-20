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

import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.logs.HttpLog;
import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;
import com.xuexiang.xhttp2.subsciber.impl.OnProgressCancelListener;

/**
 * 实现带有进度加载的订阅【进度条加载者实现接口】
 *
 * @author xuexiang
 * @since 2018/6/20 上午9:20
 */
public abstract class ProgressLoadingSubscriber<T> extends BaseSubscriber<T> implements OnProgressCancelListener {
    /**
     * 进度加载提示
     */
    private IProgressLoader mIProgressLoader;
    /**
     * 是否显示进度弹窗
     */
    private boolean mIsShowProgress = true;

    /**
     * 默认不显示弹出框，不可以取消
     */
    public ProgressLoadingSubscriber() {
        super();
        init(false);
    }

    /**
     * 自定义加载进度框
     *
     * @param iProgressLoader 自定义加载
     */
    public ProgressLoadingSubscriber(IProgressLoader iProgressLoader) {
        super();
        mIProgressLoader = iProgressLoader;
        init(false);
    }

    /**
     * 自定义加载进度框,可以设置是否显示弹出框，是否可以取消
     *
     * @param iProgressLoader
     * @param isShowProgress
     * @param isCancel        是否可取消
     */
    public ProgressLoadingSubscriber(IProgressLoader iProgressLoader, boolean isShowProgress, boolean isCancel) {
        super();
        mIProgressLoader = iProgressLoader;
        mIsShowProgress = isShowProgress;
        init(isCancel);
    }

    /**
     * 初始化
     *
     * @param isCancel
     */
    private void init(boolean isCancel) {
        if (mIProgressLoader == null) {
            return;
        }
        mIProgressLoader.setCancelable(isCancel);
        if (isCancel) {
            mIProgressLoader.setOnProgressCancelListener(this);
        }
    }

    /**
     * 展示进度框
     */
    private void showProgress() {
        if (!mIsShowProgress) {
            return;
        }
        if (mIProgressLoader != null) {
            if (!mIProgressLoader.isLoading()) {
                mIProgressLoader.showLoading();
            }
        }
    }

    /**
     * 取消进度框
     */
    private void dismissProgress() {
        if (!mIsShowProgress) {
            return;
        }
        if (mIProgressLoader != null) {
            if (mIProgressLoader.isLoading()) {
                mIProgressLoader.dismissLoading();
            }
        }
    }

    @Override
    public void onStart() {
        showProgress();
    }

    @Override
    public void onComplete() {
        dismissProgress();
    }

    @Override
    public void onError(ApiException e) {
        HttpLog.e(e);
        dismissProgress();
    }

    @Override
    public void onCancelProgress() {
        if (!isDisposed()) {
            dispose();
        }
    }
}
