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


import com.google.gson.reflect.TypeToken;
import com.xuexiang.xhttp2.cache.model.CacheResult;
import com.xuexiang.xhttp2.callback.CallBack;
import com.xuexiang.xhttp2.callback.CallBackProxy;
import com.xuexiang.xhttp2.callback.CallClazzProxy;
import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.subsciber.CallBackSubscriber;
import com.xuexiang.xhttp2.transform.HttpResultTransformer;
import com.xuexiang.xhttp2.transform.HttpSchedulersTransformer;
import com.xuexiang.xhttp2.transform.func.ApiResultFunc;
import com.xuexiang.xhttp2.transform.func.CacheResultFunc;
import com.xuexiang.xhttp2.transform.func.RetryExceptionFunc;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * <p>描述：get请求</p>
 *
 * @author xuexiang
 * @since 2018/6/25 上午12:46
 */
@SuppressWarnings(value={"unchecked", "deprecation"})
public class GetRequest extends BaseRequest<GetRequest> {

    public GetRequest(String url) {
        super(url);
    }

    public <T> Observable<T> execute(Class<T> clazz) {
        return execute(new CallClazzProxy<ApiResult<T>, T>(clazz) {
        });
    }

    public <T> Observable<T> execute(Type type) {
        return execute(new CallClazzProxy<ApiResult<T>, T>(type) {
        });
    }

    public <T> Observable<T> execute(CallClazzProxy<? extends ApiResult<T>, T> proxy) {
        return build().generateRequest()
                .map(new ApiResultFunc(proxy.getType(), mKeepJson))
                .compose(new HttpResultTransformer())
                .compose(new HttpSchedulersTransformer(mIsSyncRequest, mIsOnMainThread))
                .compose(mRxCache.transformer(mCacheMode, proxy.getCallType()))
                .retryWhen(new RetryExceptionFunc(mRetryCount, mRetryDelay, mRetryIncreaseDelay))
                .compose(new ObservableTransformer() {
                    @Override
                    public ObservableSource apply(@NonNull Observable upstream) {
                        return upstream.map(new CacheResultFunc<T>());
                    }
                });
    }

    public <T> Disposable execute(CallBack<T> callBack) {
        return execute(new CallBackProxy<ApiResult<T>, T>(callBack) {
        });
    }

    public <T> Disposable execute(CallBackProxy<? extends ApiResult<T>, T> proxy) {
        Observable<CacheResult<T>> observable = build().toObservable(mApiManager.get(getUrl(), mParams.urlParamsMap), proxy);
        if (CacheResult.class != proxy.getCallBack().getRawType()) {
            return observable.compose(new ObservableTransformer<CacheResult<T>, T>() {
                @Override
                public ObservableSource<T> apply(@NonNull Observable<CacheResult<T>> upstream) {
                    return upstream.map(new CacheResultFunc<T>());
                }
            }).subscribeWith(new CallBackSubscriber<T>(proxy.getCallBack()));
        } else {
            return observable.subscribeWith(new CallBackSubscriber<CacheResult<T>>(proxy.getCallBack()));
        }
    }

    private <T> Observable<CacheResult<T>> toObservable(Observable observable, CallBackProxy<? extends ApiResult<T>, T> proxy) {
        return observable.map(new ApiResultFunc(proxy != null ? proxy.getType() : new TypeToken<ResponseBody>() {
        }.getType(), mKeepJson))
                .compose(new HttpResultTransformer())
                .compose(new HttpSchedulersTransformer(mIsSyncRequest, mIsOnMainThread))
                .compose(mRxCache.transformer(mCacheMode, proxy.getCallBack().getType()))
                .retryWhen(new RetryExceptionFunc(mRetryCount, mRetryDelay, mRetryIncreaseDelay));
    }

    @Override
    protected Observable<ResponseBody> generateRequest() {
        return mApiManager.get(getUrl(), mParams.urlParamsMap);
    }
}
