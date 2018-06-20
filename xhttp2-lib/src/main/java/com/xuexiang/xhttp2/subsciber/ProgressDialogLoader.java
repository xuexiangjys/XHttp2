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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;
import com.xuexiang.xhttp2.subsciber.impl.OnProgressCancelListener;

/**
 * 默认进度加载
 *
 * @author xuexiang
 * @since 2018/6/10 下午9:27
 */
public class ProgressDialogLoader implements IProgressLoader {
    /**
     * 进度loading弹窗
     */
    private ProgressDialog mDialog;
    /**
     * 进度框取消监听
     */
    private OnProgressCancelListener mOnProgressCancelListener;

    public ProgressDialogLoader(Context context) {
        this(context, "请稍候...");
    }

    public ProgressDialogLoader(Context context, String msg) {
        mDialog = new ProgressDialog(context);
        updateMessage(msg);
    }

    @Override
    public boolean isLoading() {
        return mDialog != null && mDialog.isShowing();
    }

    @Override
    public void updateMessage(String msg) {
        if (mDialog != null) {
            mDialog.setMessage(msg);
        }
    }

    @Override
    public void showLoading() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void setCancelable(boolean flag) {
        mDialog.setCancelable(flag);
        if (flag) {
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if (mOnProgressCancelListener != null) {
                        mOnProgressCancelListener.onCancelProgress();
                    }
                }
            });
        }
    }

    @Override
    public void setOnProgressCancelListener(OnProgressCancelListener listener) {
        mOnProgressCancelListener = listener;
    }
}
