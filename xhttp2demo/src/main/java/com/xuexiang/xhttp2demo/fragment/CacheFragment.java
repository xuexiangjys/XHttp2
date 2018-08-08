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

package com.xuexiang.xhttp2demo.fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.xuexiang.xaop.annotation.MainThread;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.cache.converter.GsonDiskConverter;
import com.xuexiang.xhttp2.cache.converter.SerializableDiskConverter;
import com.xuexiang.xhttp2.cache.model.CacheMode;
import com.xuexiang.xhttp2.cache.model.CacheResult;
import com.xuexiang.xhttp2.callback.ProgressLoadingCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.subsciber.ProgressDialogLoader;
import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;
import com.xuexiang.xhttp2.utils.TypeUtils;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.entity.Book;
import com.xuexiang.xhttp2demo.http.callback.TipRequestCallBack;
import com.xuexiang.xhttp2demo.http.subscriber.TipRequestSubscriber;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.net.JsonUtil;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

/**
 * @author xuexiang
 * @since 2018/8/8 下午3:47
 */
@Page(name = "高阶使用 -- 缓存策略")
public class CacheFragment extends XPageFragment {

    private static final String CACHE_KEY = "/book/getAllBook";

    @BindView(R.id.tv_result_info)
    TextView mTvResultInfo;

    private CacheMode mCacheMode;

    private IProgressLoader mIProgressLoader;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cache;
    }

    @Override
    protected void initViews() {
        mTvResultInfo.setMovementMethod(ScrollingMovementMethod.getInstance());

        mIProgressLoader = new ProgressDialogLoader(getContext(), "正在加载中...");
    }

    @Override
    protected void initListeners() {

    }

    @OnClick({R.id.default_cache, R.id.first_remote, R.id.first_cache, R.id.only_remote, R.id.only_cache, R.id.cache_remote, R.id.cache_remote_distinct})
    public void onViewClicked(View view) {
        clearLog();
        switch (view.getId()) {
            case R.id.default_cache:
                //默认缓存，走的是OKHttp cache
                mCacheMode = CacheMode.DEFAULT;
                break;
            case R.id.first_remote:
                //先请求网络，请求网络失败后再加载缓存 （自定义缓存RxCache）
                mCacheMode = CacheMode.FIRST_REMOTE;
                break;
            case R.id.first_cache:
                //先加载缓存，缓存没有再去请求网络 （自定义缓存RxCache）
                mCacheMode = CacheMode.FIRST_CACHE;
                break;
            case R.id.only_remote:
                //仅加载网络，但数据依然会被缓存 （自定义缓存RxCache）
                mCacheMode = CacheMode.ONLY_REMOTE;
                break;
            case R.id.only_cache:
                //只读取缓存 （自定义缓存RxCache）
                mCacheMode = CacheMode.ONLY_CACHE;
                break;
            case R.id.cache_remote:
                //先使用缓存，不管是否存在，仍然请求网络，会回调两次 （自定义缓存RxCache）
                mCacheMode = CacheMode.CACHE_REMOTE;
                break;
            case R.id.cache_remote_distinct:
                //先使用缓存，不管是否存在，仍然请求网络，有缓存先显示缓存，等网络请求数据回来后发现和缓存是一样的就不会再返回，否则数据不一样会继续返回。
                // 目的是为了防止数据是一致的也会刷新两次界面） （自定义缓存RxCache）
                mCacheMode = CacheMode.CACHE_REMOTE_DISTINCT;
                break;
        }

        requestCache();
    }

    private void requestCache() {
        XHttp.get("/book/getAllBook")
                .timeOut(10 * 1000)//测试局部超时10s
                .cacheMode(mCacheMode)
                .cacheKey(CACHE_KEY)//缓存key
                .retryCount(5)//重试次数
                .cacheTime(5 * 60)//缓存时间300s，默认-1永久缓存  okhttp和自定义缓存都起作用
                .cacheDiskConverter(new GsonDiskConverter())//默认使用的是 new SerializableDiskConverter();
                .timeStamp(true)
                .execute(new ProgressLoadingCallBack<CacheResult<List<Book>>>(mIProgressLoader) {
                    @Override
                    public void onSuccess(CacheResult<List<Book>> cacheResult) {
                        ToastUtils.toast("请求成功!");
                        String from;
                        if (cacheResult.isFromCache) {
                            from = "我来自缓存";
                        } else {
                            from = "我来自远程网络";
                        }
                        showResult(from + "\n" + JsonUtil.toJson(cacheResult.data));
                    }

                    @Override
                    public void onError(ApiException e) {
                        super.onError(e);
                        ToastUtils.toast(e.getDisplayMessage());
                    }
                });
    }

    @OnClick({R.id.btn_clear, R.id.load_cache, R.id.remove_cache, R.id.clear_cache})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clear:
                clearLog();
                break;
            case R.id.load_cache:
                loadCache(CACHE_KEY);
                break;
            case R.id.remove_cache:
                XHttp.removeCache(CACHE_KEY);
                break;
            case R.id.clear_cache:
                XHttp.clearCache();
                break;
            default:
                break;
        }

    }

    /**
     * 根据key获取缓存
     */
    public void loadCache(String key) {
        Observable<List<Book>> observable = XHttp.getRxCacheBuilder()
                //获取缓存需要指定下转换器，默认是SerializableDiskConverter
                //就是你网络请求用哪个转换器存储的缓存，那么读取时也要采用对应的转换器读取
                .diskConverter(new GsonDiskConverter()).build()
                //这个表示读取缓存根据时间,读取指定时间内的缓存，例如读取:5*60s之内的缓存
//                .load(TypeUtils.getListType(Book.class), key, 5 * 60)
                //这个表示读取缓存不根据时间只要有缓存就读取
                .load(TypeUtils.getListType(Book.class), key);
        observable.subscribe(new TipRequestSubscriber<List<Book>>() {
            @Override
            protected void onSuccess(List<Book> books) {
                showResult("获取缓存成功! \n" + JsonUtil.toJson(books));
            }

            @Override
            public void onError(ApiException e) {
                showResult("获取缓存失败：未找到缓存！");
            }
        });
    }

    @MainThread
    private void showResult(String result) {
        mTvResultInfo.setText("请求结果:\n\r" + result);
    }

    private void clearLog() {
        mTvResultInfo.setText("");
    }
}
