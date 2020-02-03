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

import com.xuexiang.xhttp2.cache.model.CacheResult;
import com.xuexiang.xhttp2.callback.CallBack;
import com.xuexiang.xhttp2.callback.CallBackProxy;
import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.subsciber.CallBackSubscriber;
import com.xuexiang.xhttp2.transform.HandleErrTransformer;
import com.xuexiang.xhttp2.transform.HttpResultTransformer;
import com.xuexiang.xhttp2.transform.HttpSchedulersTransformer;
import com.xuexiang.xhttp2.transform.func.CacheResultFunc;
import com.xuexiang.xhttp2.transform.func.RetryExceptionFunc;
import com.xuexiang.xhttp2.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 *  <p>描述：自定义请求，例如你有自己的ApiService</p>
 *
 * @author xuexiang
 * @since 2018/6/25 下午8:16
 */
@SuppressWarnings(value={"unchecked"})
public class CustomRequest extends BaseRequest<CustomRequest> {

    public CustomRequest() {
        super("");
    }

    @Override
    public CustomRequest build() {
        return super.build();
    }

    /**
     * 创建api服务  可以支持自定义的api，默认使用BaseApiService,上层不用关心
     *
     * @param service 自定义的ApiService class
     */
    public <T> T create(final Class<T> service) {
        checkValidate();
        return mRetrofit.create(service);
    }

    private void checkValidate() {
        Utils.checkNotNull(mRetrofit, "请先在调用build()才能使用");
    }


    //=================apiCall====================//

    /**
     * 针对retrofit定义的接口，返回的是Observable<ApiResult<T>>的情况<br>
     *
     * 对ApiResult进行拆包，直接获取数据
     *
     * @param observable retrofit定义接口返回的类型
     */
    public <T> Observable<T> apiCall(Observable<? extends ApiResult<T>> observable) {
        checkValidate();
        return observable
                .compose(new HttpResultTransformer())
                .compose(new HttpSchedulersTransformer(mIsSyncRequest, mIsOnMainThread))
                .retryWhen(new RetryExceptionFunc(mRetryCount, mRetryDelay, mRetryIncreaseDelay));
    }

    /**
     * 针对retrofit定义的接口，返回的是Observable<ApiResult<T>>的情况<br>
     *
     * 对ApiResult进行拆包，直接获取数据
     *
     * @param observable retrofit定义接口返回的类型
     */
    public <T> Disposable apiCall(Observable observable, CallBack<T> callBack) {
        return call(observable, new CallBackProxy<ApiResult<T>, T>(callBack){});
    }

    //=================call====================//
    /**
     * 针对retrofit定义的接口，返回的是Observable<T>的情况<br>
     *
     * 不对ApiResult进行拆包，返回服务端响应的ApiResult
     *
     * @param observable retrofit定义接口返回的类型
     */
    public <T> Observable<T> call(Observable<T> observable) {
        checkValidate();
        return observable
                .compose(new HandleErrTransformer())
                .compose(new HttpSchedulersTransformer(mIsSyncRequest, mIsOnMainThread))
                .retryWhen(new RetryExceptionFunc(mRetryCount, mRetryDelay, mRetryIncreaseDelay));
    }

    /**
     * 针对retrofit定义的接口，返回的是Observable<T>的情况<br>
     *
     * 不对ApiResult进行拆包，返回服务端响应的ApiResult
     *
     * @param observable retrofit定义接口返回的类型
     */
    public <T> void call(Observable<T> observable, CallBack<T> callBack) {
        call(observable, new CallBackSubscriber(callBack));
    }

    public <R> void call(Observable observable, Observer<R> subscriber) {
        call(observable).subscribe(subscriber);
    }

    /**
     * @param observable
     * @param proxy
     * @param <T>
     * @return
     */
    public <T> Disposable call(Observable<T> observable, CallBackProxy<? extends ApiResult<T>, T> proxy) {
        Observable<CacheResult<T>> cacheObservable = build().toObservable(observable, proxy);
        if (CacheResult.class != proxy.getRawType()) {
            return cacheObservable.compose(new ObservableTransformer<CacheResult<T>, T>() {
                @Override
                public ObservableSource<T> apply(@NonNull Observable<CacheResult<T>> upstream) {
                    return upstream.map(new CacheResultFunc<T>());
                }
            }).subscribeWith(new CallBackSubscriber<T>(proxy.getCallBack()));
        } else {
            return cacheObservable.subscribeWith(new CallBackSubscriber<CacheResult<T>>(proxy.getCallBack()));
        }
    }

    @Override
    protected <T> Observable<CacheResult<T>> toObservable(Observable observable, CallBackProxy<? extends ApiResult<T>, T> proxy) {
        checkValidate();
        return observable
                .compose(new HttpResultTransformer())
                .compose(new HttpSchedulersTransformer(mIsSyncRequest, mIsOnMainThread))
                .compose(mRxCache.transformer(mCacheMode, proxy.getCallBack().getType()))
                .retryWhen(new RetryExceptionFunc(mRetryCount, mRetryDelay, mRetryIncreaseDelay));
    }

    @Override
    protected Observable<ResponseBody> generateRequest() {
        return null;
    }
}
