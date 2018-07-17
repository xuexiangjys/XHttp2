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

import com.xuexiang.xhttp2.callback.CallBack;
import com.xuexiang.xhttp2.callback.ProgressLoadingCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

/**
 * 带有callBack的回调【如果作用是不需要用户订阅，只要实现callback回调】
 *
 * @author xuexiang
 * @since 2018/5/23 上午9:58
 */
public class CallBackSubscriber<T> extends BaseSubscriber<T> {

    private CallBack<T> mCallBack;

    public CallBackSubscriber(CallBack<T> callBack) {
        super();
        mCallBack = callBack;
        if (callBack instanceof ProgressLoadingCallBack) {
            ((ProgressLoadingCallBack) callBack).subscription(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mCallBack != null) {
            mCallBack.onStart();
        }
    }

    @Override
    public void onError(ApiException e) {
        if (mCallBack != null) {
            mCallBack.onError(e);
        }
    }

    @Override
    public void onSuccess(T t) {
        try {
            if (mCallBack != null) {
                mCallBack.onSuccess(t);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            onError(e);
        }

    }

    @Override
    public void onComplete() {
        super.onComplete();
        if (mCallBack != null) {
            mCallBack.onCompleted();
        }
    }

}
