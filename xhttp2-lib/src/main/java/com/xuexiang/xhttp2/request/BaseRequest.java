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

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.annotation.ThreadType;
import com.xuexiang.xhttp2.api.ApiService;
import com.xuexiang.xhttp2.cache.RxCache;
import com.xuexiang.xhttp2.cache.converter.IDiskConverter;
import com.xuexiang.xhttp2.cache.model.CacheMode;
import com.xuexiang.xhttp2.cache.model.CacheResult;
import com.xuexiang.xhttp2.callback.CallBack;
import com.xuexiang.xhttp2.callback.CallBackProxy;
import com.xuexiang.xhttp2.callback.CallClazzProxy;
import com.xuexiang.xhttp2.https.HttpsUtils;
import com.xuexiang.xhttp2.interceptor.BaseDynamicInterceptor;
import com.xuexiang.xhttp2.interceptor.CacheInterceptor;
import com.xuexiang.xhttp2.interceptor.CacheInterceptorOffline;
import com.xuexiang.xhttp2.interceptor.HeadersInterceptor;
import com.xuexiang.xhttp2.interceptor.NoCacheInterceptor;
import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.model.HttpHeaders;
import com.xuexiang.xhttp2.model.HttpParams;
import com.xuexiang.xhttp2.subsciber.CallBackSubscriber;
import com.xuexiang.xhttp2.transform.HttpResultTransformer;
import com.xuexiang.xhttp2.transform.HttpSchedulersTransformer;
import com.xuexiang.xhttp2.transform.func.ApiResultFunc;
import com.xuexiang.xhttp2.transform.func.CacheResultFunc;
import com.xuexiang.xhttp2.transform.func.RetryExceptionFunc;
import com.xuexiang.xhttp2.utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.Cache;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import static com.xuexiang.xhttp2.XHttp.DEFAULT_CACHE_NEVER_EXPIRE;

/**
 * 所有请求的基类
 *
 * @author xuexiang
 * @since 2018/5/23 上午10:03
 */
@SuppressWarnings(value = {"unchecked"})
public abstract class BaseRequest<R extends BaseRequest> {

    protected Context mContext;
    //====请求地址=====//
    protected HttpUrl mHttpUrl;
    /**
     * baseUrl, 基础服务器地址
     */
    protected String mBaseUrl;
    /**
     * SubUrl,介于BaseUrl和请求url之间
     */
    private String mSubUrl;
    /**
     * 请求url
     */
    protected String mUrl;
    //====请求行为=====//
    /**
     * 是否是同步请求
     */
    protected boolean mIsSyncRequest = false;
    /**
     * 响应是否回到主线程
     */
    protected boolean mIsOnMainThread = true;
    /**
     * 是否返回原始的json格式
     */
    protected boolean mKeepJson = false;
    //====请求校验=====//
    /**
     * 是否需要签名
     */
    private boolean mSign = false;
    /**
     * 是否需要追加时间戳
     */
    private boolean mTimeStamp = false;
    /**
     * 是否需要追加token
     */
    private boolean mAccessToken = false;
    //====请求超时重试=====//
    /**
     * 读超时
     */
    protected long mReadTimeOut;
    /**
     * 写超时
     */
    protected long mWriteTimeOut;
    /**
     * 链接超时
     */
    protected long mConnectTimeout;
    /**
     * 重试次数默认3次
     */
    protected int mRetryCount;
    /**
     * 延迟xxms重试
     */
    protected int mRetryDelay;
    /**
     * 叠加延迟
     */
    protected int mRetryIncreaseDelay;
    //====请求头，公共参数的设置=====//
    /**
     * 添加的header
     */
    protected HttpHeaders mHeaders = new HttpHeaders();
    /**
     * 添加的param
     */
    protected HttpParams mParams = new HttpParams();
    //====请求缓存=====//
    /**
     * rxCache缓存
     */
    protected RxCache mRxCache;
    protected Cache mCache;
    /**
     * 缓存模式, 默认无缓存
     */
    protected CacheMode mCacheMode;
    /**
     * 缓存时间
     */
    protected long mCacheTime;
    /**
     * 缓存Key
     */
    protected String mCacheKey;
    /**
     * 设置RxCache磁盘转换器
     */
    protected IDiskConverter mDiskConverter;
    //====OkHttpClient的拦截器、代理等=====//
    protected OkHttpClient mOkHttpClient;
    protected Proxy mProxy;
    protected final List<Interceptor> mNetworkInterceptors = new ArrayList<>();
    protected final List<Interceptor> mInterceptors = new ArrayList<>();
    //====Retrofit的Api、Factory=====//
    protected Retrofit mRetrofit;
    /**
     * 通用的的api接口
     */
    protected ApiService mApiManager;
    protected List<Converter.Factory> mConverterFactories = new ArrayList<>();
    protected List<CallAdapter.Factory> mAdapterFactories = new ArrayList<>();
    //====Https设置=====//
    protected HttpsUtils.SSLParams mSSLParams;
    protected HostnameVerifier mHostnameVerifier;
    //====Cookie设置=====//
    /**
     * 用户手动添加的Cookie
     */
    protected List<Cookie> mCookies = new ArrayList<>();

    /**
     * 构建基础请求
     *
     * @param url 请求的url
     */
    public BaseRequest(String url) {
        mContext = XHttp.getContext();
        mUrl = url;
        mBaseUrl = XHttp.getBaseUrl();
        mSubUrl = XHttp.getSubUrl();
        if (!TextUtils.isEmpty(mBaseUrl)) {
            mHttpUrl = HttpUrl.parse(mBaseUrl);
        }
        // 添加缓存模式
        mCacheMode = XHttp.getCacheMode();
        // 缓存时间
        mCacheTime = XHttp.getCacheTime();
        // 超时重试次数
        mRetryCount = XHttp.getRetryCount();
        // 超时重试延时
        mRetryDelay = XHttp.getRetryDelay();
        // 超时重试叠加延时
        mRetryIncreaseDelay = XHttp.getRetryIncreaseDelay();
        // OKHttp  mCache
        mCache = XHttp.getHttpCache();
        // 默认添加 Accept-Language
        String acceptLanguage = HttpHeaders.getAcceptLanguage();
        if (!TextUtils.isEmpty(acceptLanguage)) {
            headers(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);
        }
        // 默认添加 User-Agent
        String userAgent = HttpHeaders.getUserAgent();
        if (!TextUtils.isEmpty(userAgent)) {
            headers(HttpHeaders.HEAD_KEY_USER_AGENT, userAgent);
        }
        // 添加公共请求参数
        if (XHttp.getCommonParams() != null) {
            mParams.put(XHttp.getCommonParams());
        }
        if (XHttp.getCommonHeaders() != null) {
            mHeaders.put(XHttp.getCommonHeaders());
        }
    }

    //===========================================//
    //               请求url设置                  //
    //===========================================//

    /**
     * 设置url路径
     *
     * @param url
     * @return
     */
    public R url(String url) {
        mUrl = Utils.checkNotNull(url, "mUrl == null");
        return (R) this;
    }

    /**
     * 设置基础url路径
     *
     * @param baseUrl
     * @return
     */
    public R baseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
        if (!TextUtils.isEmpty(mBaseUrl)) {
            mHttpUrl = HttpUrl.parse(baseUrl);
        }
        return (R) this;
    }

    /**
     * 设置基础subUrl路径
     *
     * @param subUrl
     * @return
     */
    public R subUrl(String subUrl) {
        mSubUrl = Utils.checkNotNull(subUrl, "mSubUrl == null");
        return (R) this;
    }

    /**
     * 获取请求的地址
     *
     * @return
     */
    public String getUrl() {
        return mSubUrl + mUrl;
    }

    /**
     * @return 获取基础地址
     */
    public String getBaseUrl() {
        return mBaseUrl;
    }

    //===========================================//
    //               请求校验                     //
    //===========================================//

    /**
     * 保存json的形式（不返回对象，保持Json的String形式，不过传入的必须是String.class）
     *
     * @param keepJson
     * @return
     */
    public R keepJson(boolean keepJson) {
        mKeepJson = keepJson;
        return (R) this;
    }

    /**
     * 是否是同步请求（不开子线程，默认是false）
     *
     * @param syncRequest
     * @return
     */
    public R syncRequest(boolean syncRequest) {
        mIsSyncRequest = syncRequest;
        return (R) this;
    }

    /**
     * 请求完成后是否回到主线程（默认是true）
     *
     * @param onMainThread
     * @return
     */
    public R onMainThread(boolean onMainThread) {
        mIsOnMainThread = onMainThread;
        return (R) this;
    }

    /**
     * 设置请求的线程调度类型
     *
     * @param threadType
     * @return
     */
    public R threadType(@ThreadType String threadType) {
        if (ThreadType.TO_MAIN.equals(threadType)) { // -> main -> io -> main
            syncRequest(false).onMainThread(true);
        } else if (ThreadType.TO_IO.equals(threadType)) { // -> main -> io -> io
            syncRequest(false).onMainThread(false);
        } else if (ThreadType.IN_THREAD.equals(threadType)) { // -> io -> io -> io
            syncRequest(true).onMainThread(false);
        }
        return (R) this;
    }

    //===========================================//
    //             请求超时、重试设置                //
    //===========================================//

    /**
     * 是否添加数字签名（默认false）
     *
     * @param sign
     * @return
     */
    public R sign(boolean sign) {
        mSign = sign;
        return (R) this;
    }

    /**
     * 是否添加时间戳（默认false）
     *
     * @param timeStamp
     * @return
     */
    public R timeStamp(boolean timeStamp) {
        mTimeStamp = timeStamp;
        return (R) this;
    }

    /**
     * 是否需要验证token（默认false)
     *
     * @param accessToken
     * @return
     */
    public R accessToken(boolean accessToken) {
        mAccessToken = accessToken;
        return (R) this;
    }

    //===========================================//
    //             请求超时、重试设置                //
    //===========================================//

    /**
     * 设置读的超时时间
     *
     * @param readTimeOut
     * @return
     */
    public R readTimeOut(long readTimeOut) {
        mReadTimeOut = readTimeOut;
        return (R) this;
    }

    /**
     * 设置写的超时时间
     *
     * @param writeTimeOut
     * @return
     */
    public R writeTimeOut(long writeTimeOut) {
        mWriteTimeOut = writeTimeOut;
        return (R) this;
    }

    /**
     * 设置服务器连接的超时时间
     *
     * @param connectTimeout
     * @return
     */
    public R connectTimeout(long connectTimeout) {
        mConnectTimeout = connectTimeout;
        return (R) this;
    }

    /**
     * 设置超时时间（读、写、服务器连接的超时时间）
     *
     * @param timeOut
     * @return
     */
    public R timeOut(long timeOut) {
        mReadTimeOut = timeOut;
        mWriteTimeOut = timeOut;
        mConnectTimeout = timeOut;
        return (R) this;
    }

    /**
     * 设置超时重试的次数
     *
     * @param retryCount
     * @return
     */
    public R retryCount(int retryCount) {
        if (retryCount < 0) {
            throw new IllegalArgumentException("mRetryCount must > 0");
        }
        mRetryCount = retryCount;
        return (R) this;
    }

    /**
     * 设置超时重试的延迟时间
     *
     * @param retryDelay
     * @return
     */
    public R retryDelay(int retryDelay) {
        if (retryDelay < 0) {
            throw new IllegalArgumentException("mRetryDelay must > 0");
        }
        mRetryDelay = retryDelay;
        return (R) this;
    }

    /**
     * 设置超时重试叠加延时
     *
     * @param retryIncreaseDelay
     * @return
     */
    public R retryIncreaseDelay(int retryIncreaseDelay) {
        if (retryIncreaseDelay < 0) {
            throw new IllegalArgumentException("mRetryIncreaseDelay must > 0");
        }
        mRetryIncreaseDelay = retryIncreaseDelay;
        return (R) this;
    }

    //===========================================//
    //            请求头，公共参数的设置             //
    //===========================================//

    /**
     * 添加头信息
     */
    public R headers(HttpHeaders headers) {
        mHeaders.put(headers);
        return (R) this;
    }

    /**
     * 添加头信息
     */
    public R headers(String key, String value) {
        mHeaders.put(key, value);
        return (R) this;
    }

    /**
     * 移除头信息
     */
    public R removeHeader(String key) {
        mHeaders.remove(key);
        return (R) this;
    }

    /**
     * 移除所有头信息
     */
    public R removeAllHeaders() {
        mHeaders.clear();
        return (R) this;
    }

    /**
     * 设置参数
     */
    public R params(HttpParams params) {
        mParams.put(params);
        return (R) this;
    }

    /**
     * 设置参数
     */
    public R params(Map<String, Object> params) {
        mParams.put(params);
        return (R) this;
    }

    /**
     * 设置参数
     */
    public R params(String key, Object value) {
        mParams.put(key, value);
        return (R) this;
    }

    /**
     * 去除参数
     */
    public R removeParam(String key) {
        mParams.remove(key);
        return (R) this;
    }

    /**
     * 去除所有参数
     */
    public R removeAllParams() {
        mParams.clear();
        return (R) this;
    }

    public HttpParams getParams() {
        return mParams;
    }

    //===========================================//
    //               请求缓存设置                  //
    //===========================================//

    /**
     * 设置缓存（默认无缓存）
     *
     * @param cache
     * @return
     */
    public R okCache(Cache cache) {
        mCache = cache;
        return (R) this;
    }

    /**
     * 设置缓存的模式（默认无缓存）
     *
     * @param cacheMode
     * @return
     */
    public R cacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return (R) this;
    }

    /**
     * 设置缓存的key
     *
     * @param cacheKey
     * @return
     */
    public R cacheKey(String cacheKey) {
        mCacheKey = cacheKey;
        return (R) this;
    }

    /**
     * 设置缓存保存的时间（缓存过期时间，默认永久缓存）
     *
     * @param cacheTime
     * @return
     */
    public R cacheTime(long cacheTime) {
        if (cacheTime <= -1) {
            cacheTime = DEFAULT_CACHE_NEVER_EXPIRE;
        }
        mCacheTime = cacheTime;
        return (R) this;
    }

    /**
     * 设置缓存的转换器
     */
    public R cacheDiskConverter(IDiskConverter converter) {
        mDiskConverter = Utils.checkNotNull(converter, "converter == null");
        return (R) this;
    }

    //===========================================//
    //     OkHttpClient的拦截器、代理等设置          //
    //===========================================//

    /**
     * 设置代理
     */
    public R okproxy(Proxy proxy) {
        mProxy = proxy;
        return (R) this;
    }

    /**
     * 增加应用拦截器
     *
     * @param interceptor
     * @return
     */
    public R addInterceptor(Interceptor interceptor) {
        mInterceptors.add(Utils.checkNotNull(interceptor, "interceptor == null"));
        return (R) this;
    }

    /**
     * 增加网络拦截器
     *
     * @param interceptor
     * @return
     */
    public R addNetworkInterceptor(Interceptor interceptor) {
        mNetworkInterceptors.add(Utils.checkNotNull(interceptor, "interceptor == null"));
        return (R) this;
    }

    //===========================================//
    //         Retrofit的Factory设置              //
    //===========================================//

    /**
     * 设置Converter.Factory,默认GsonConverterFactory.create()
     */
    public R addConverterFactory(Converter.Factory factory) {
        mConverterFactories.add(factory);
        return (R) this;
    }

    /**
     * 设置CallAdapter.Factory,默认RxJavaCallAdapterFactory.create()
     */
    public R addCallAdapterFactory(CallAdapter.Factory factory) {
        mAdapterFactories.add(factory);
        return (R) this;
    }

    //===========================================//
    //                Https设置                   //
    //===========================================//

    /**
     * https的全局访问规则
     */
    public R hostnameVerifier(HostnameVerifier hostnameVerifier) {
        mHostnameVerifier = hostnameVerifier;
        return (R) this;
    }

    /**
     * https的全局自签名证书
     */
    public R certificates(InputStream... certificates) {
        mSSLParams = HttpsUtils.getSslSocketFactory(null, null, certificates);
        return (R) this;
    }

    /**
     * https双向认证证书
     */
    public R certificates(InputStream bksFile, String password, InputStream... certificates) {
        mSSLParams = HttpsUtils.getSslSocketFactory(bksFile, password, certificates);
        return (R) this;
    }

    //===========================================//
    //                Cookie设置                  //
    //===========================================//

    public R addCookie(String name, String value) {
        Cookie.Builder builder = new Cookie.Builder();
        Cookie cookie = builder.name(name).value(value).domain(mHttpUrl.host()).build();
        mCookies.add(cookie);
        return (R) this;
    }

    public R addCookie(Cookie cookie) {
        mCookies.add(cookie);
        return (R) this;
    }

    public R addCookies(List<Cookie> cookies) {
        mCookies.addAll(cookies);
        return (R) this;
    }

    //===========================================//
    //               构建请求                     //
    //===========================================//

    /**
     * 进行网络请求
     *
     * @return 网络请求的响应
     */
    protected abstract Observable<ResponseBody> generateRequest();

    /**
     * 根据当前的请求参数，生成对应的OkClient
     */
    private OkHttpClient.Builder generateOkClient() {
        if (mReadTimeOut <= 0 && mWriteTimeOut <= 0 && mConnectTimeout <= 0 && mSSLParams == null
                && mCookies.size() == 0 && mHostnameVerifier == null && mProxy == null && mHeaders.isEmpty()) {
            OkHttpClient.Builder builder = XHttp.getOkHttpClientBuilder();
            for (Interceptor interceptor : builder.interceptors()) {
                if (interceptor instanceof BaseDynamicInterceptor) {
                    ((BaseDynamicInterceptor) interceptor).sign(mSign).timeStamp(mTimeStamp).accessToken(mAccessToken);
                }
            }
            return builder;
        } else {
            final OkHttpClient.Builder newClientBuilder = XHttp.getOkHttpClient().newBuilder();
            if (mReadTimeOut > 0) {
                newClientBuilder.readTimeout(mReadTimeOut, TimeUnit.MILLISECONDS);
            }
            if (mWriteTimeOut > 0) {
                newClientBuilder.writeTimeout(mWriteTimeOut, TimeUnit.MILLISECONDS);
            }
            if (mConnectTimeout > 0) {
                newClientBuilder.connectTimeout(mConnectTimeout, TimeUnit.MILLISECONDS);
            }
            if (mHostnameVerifier != null) {
                newClientBuilder.hostnameVerifier(mHostnameVerifier);
            }
            if (mSSLParams != null) {
                newClientBuilder.sslSocketFactory(mSSLParams.sSLSocketFactory, mSSLParams.trustManager);
            }
            if (mProxy != null) {
                newClientBuilder.proxy(mProxy);
            }
            if (mCookies.size() > 0) {
                XHttp.getCookieJar().addCookies(mCookies);
            }
            for (Interceptor interceptor : mInterceptors) {
                if (interceptor instanceof BaseDynamicInterceptor) {
                    ((BaseDynamicInterceptor) interceptor).sign(mSign).timeStamp(mTimeStamp).accessToken(mAccessToken);
                }
                newClientBuilder.addInterceptor(interceptor);
            }
            for (Interceptor interceptor : newClientBuilder.interceptors()) {
                if (interceptor instanceof BaseDynamicInterceptor) {
                    ((BaseDynamicInterceptor) interceptor).sign(mSign).timeStamp(mTimeStamp).accessToken(mAccessToken);
                }
            }
            if (mNetworkInterceptors.size() > 0) {
                for (Interceptor interceptor : mNetworkInterceptors) {
                    newClientBuilder.addNetworkInterceptor(interceptor);
                }
            }
            //添加头
            newClientBuilder.addInterceptor(new HeadersInterceptor(mHeaders));
            return newClientBuilder;
        }
    }

    /**
     * 根据当前的请求参数，生成对应的Retrofit
     */
    private Retrofit.Builder generateRetrofit() {
        if (mConverterFactories.isEmpty() && mAdapterFactories.isEmpty()) {
            return XHttp.getRetrofitBuilder().baseUrl(mBaseUrl);
        } else {
            final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
            if (!mConverterFactories.isEmpty()) {
                for (Converter.Factory converterFactory : mConverterFactories) {
                    retrofitBuilder.addConverterFactory(converterFactory);
                }
            } else {
                //获取全局的对象重新设置
                List<Converter.Factory> listConverterFactory = XHttp.getRetrofitBuilder().converterFactories();
                for (Converter.Factory factory : listConverterFactory) {
                    retrofitBuilder.addConverterFactory(factory);
                }
            }
            if (!mAdapterFactories.isEmpty()) {
                for (CallAdapter.Factory adapterFactory : mAdapterFactories) {
                    retrofitBuilder.addCallAdapterFactory(adapterFactory);
                }
            } else {
                //获取全局的对象重新设置
                List<CallAdapter.Factory> listAdapterFactory = XHttp.getRetrofitBuilder().callAdapterFactories();
                for (CallAdapter.Factory factory : listAdapterFactory) {
                    retrofitBuilder.addCallAdapterFactory(factory);
                }
            }
            return retrofitBuilder.baseUrl(mBaseUrl);
        }
    }

    /**
     * 根据当前的请求参数，生成对应的RxCache和Cache
     */
    private RxCache.Builder generateRxCache() {
        final RxCache.Builder rxCacheBuilder = XHttp.getRxCacheBuilder();
        switch (mCacheMode) {
            case NO_CACHE://不使用缓存
                final NoCacheInterceptor NOCACHEINTERCEPTOR = new NoCacheInterceptor();
                mInterceptors.add(NOCACHEINTERCEPTOR);
                mNetworkInterceptors.add(NOCACHEINTERCEPTOR);
                break;
            case DEFAULT://使用OkHttp的缓存
                if (mCache == null) {
                    File cacheDirectory = XHttp.getCacheDirectory();
                    if (cacheDirectory == null) {
                        cacheDirectory = new File(XHttp.getContext().getCacheDir(), "okhttp-cache");
                    } else {
                        if (cacheDirectory.isDirectory() && !cacheDirectory.exists()) {
                            cacheDirectory.mkdirs();
                        }
                    }
                    mCache = new Cache(cacheDirectory, Math.max(5 * 1024 * 1024, XHttp.getCacheMaxSize()));
                }
                String cacheControlValue = String.format("max-age=%d", Math.max(-1, mCacheTime));
                final CacheInterceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new CacheInterceptor(XHttp.getContext(), cacheControlValue);
                final CacheInterceptorOffline REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE = new CacheInterceptorOffline(XHttp.getContext(), cacheControlValue);
                mNetworkInterceptors.add(REWRITE_CACHE_CONTROL_INTERCEPTOR);
                mNetworkInterceptors.add(REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE);
                mInterceptors.add(REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE);
                break;
            case FIRST_REMOTE:
            case FIRST_CACHE:
            case ONLY_REMOTE:
            case ONLY_CACHE:
            case CACHE_REMOTE:
            case CACHE_REMOTE_DISTINCT:
                mInterceptors.add(new NoCacheInterceptor());
                if (mDiskConverter == null) {
                    final RxCache.Builder tempRxCacheBuilder = rxCacheBuilder;
                    tempRxCacheBuilder.cacheKey(Utils.checkNotNull(mCacheKey, "mCacheKey == null"))
                            .cacheTime(mCacheTime);
                    return tempRxCacheBuilder;
                } else {
                    final RxCache.Builder cacheBuilder = XHttp.getRxCache().newBuilder();
                    cacheBuilder.diskConverter(mDiskConverter)
                            .cacheKey(Utils.checkNotNull(mCacheKey, "mCacheKey == null"))
                            .cacheTime(mCacheTime);
                    return cacheBuilder;
                }
        }
        return rxCacheBuilder;
    }

    /**
     * 构建请求【构建RxCache、OkHttpClient、Retrofit、mApiManager】
     *
     * @return
     */
    protected R build() {
        final RxCache.Builder rxCacheBuilder = generateRxCache();
        OkHttpClient.Builder okHttpClientBuilder = generateOkClient();
        if (mCacheMode == CacheMode.DEFAULT) {//okHttp缓存
            okHttpClientBuilder.cache(mCache);
        }
        final Retrofit.Builder retrofitBuilder = generateRetrofit();
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());//增加RxJavaCallAdapterFactory
        mOkHttpClient = okHttpClientBuilder.build();
        retrofitBuilder.client(mOkHttpClient);
        mRetrofit = retrofitBuilder.build();
        mRxCache = rxCacheBuilder.build();
        mApiManager = mRetrofit.create(ApiService.class);
        return (R) this;
    }

    //===================请求执行===============================//

    public <T> Observable<T> execute(Class<T> clazz) {
        return execute(new CallClazzProxy<ApiResult<T>, T>(clazz) {
        });
    }

    public <T> Observable<T> execute(Type type) {
        return execute(new CallClazzProxy<ApiResult<T>, T>(type) {
        });
    }

    public <T> Disposable execute(CallBack<T> callBack) {
        return execute(new CallBackProxy<ApiResult<T>, T>(callBack) {
        });
    }

    //==================================================//

    /**
     * 执行请求，获取请求响应结果【Observable<CacheResult<T>>】
     *
     * @param observable
     * @param proxy
     * @param <T>
     * @return
     */
    protected <T> Observable<CacheResult<T>> toObservable(Observable observable, CallBackProxy<? extends ApiResult<T>, T> proxy) {
        return observable.map(new ApiResultFunc(proxy != null ? proxy.getType() : new TypeToken<ResponseBody>() {
        }.getType(), mKeepJson))
                .compose(new HttpResultTransformer())
                .compose(new HttpSchedulersTransformer(mIsSyncRequest, mIsOnMainThread))
                .compose(mRxCache.transformer(mCacheMode, proxy.getCallBack().getType()))
                .retryWhen(new RetryExceptionFunc(mRetryCount, mRetryDelay, mRetryIncreaseDelay));
    }

    /**
     * 执行请求，并订阅请求响应结果(CallBack代理)
     *
     * @param proxy
     * @param <T>
     * @return
     */
    public <T> Disposable execute(CallBackProxy<? extends ApiResult<T>, T> proxy) {
        Observable<CacheResult<T>> observable = build().toObservable(generateRequest(), proxy);
        if (CacheResult.class != proxy.getRawType()) {
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

    /**
     * 执行请求，获取请求响应结果【Observable<T>】
     *
     * @param proxy 使用了getType
     * @param <T>
     * @return
     */
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

}

