package com.xuexiang.xhttp2;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.xuexiang.xhttp2.cache.RxCache;
import com.xuexiang.xhttp2.cache.converter.IDiskConverter;
import com.xuexiang.xhttp2.cache.converter.SerializableDiskConverter;
import com.xuexiang.xhttp2.cache.model.CacheMode;
import com.xuexiang.xhttp2.cookie.CookieManager;
import com.xuexiang.xhttp2.https.DefaultHostnameVerifier;
import com.xuexiang.xhttp2.https.HttpsUtils;
import com.xuexiang.xhttp2.interceptor.HttpLoggingInterceptor;
import com.xuexiang.xhttp2.logs.HttpLog;
import com.xuexiang.xhttp2.model.HttpHeaders;
import com.xuexiang.xhttp2.model.HttpParams;
import com.xuexiang.xhttp2.utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.net.Proxy;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author xuexiang
 * @since 2018/6/13 上午12:44
 */
public final class XHttp {
    private volatile static XHttp sInstance = null;
    private static Application sContext;

    public static final int DEFAULT_MILLISECONDS = 15000;             //默认的超时时间
    private static final int DEFAULT_RETRY_COUNT = 3;                 //默认重试次数
    private static final int DEFAULT_RETRY_INCREASE_DELAY = 0;         //默认重试叠加时间
    private static final int DEFAULT_RETRY_DELAY = 500;               //默认重试延时
    public static final int DEFAULT_CACHE_NEVER_EXPIRE = -1;

    private Cache mCache = null;                                      //OkHttp缓存对象
    private CacheMode mCacheMode = CacheMode.NO_CACHE;                //缓存类型
    private long mCacheTime = -1;                                     //缓存时间
    private File mCacheDirectory;                                     //缓存目录
    private long mCacheMaxSize;                                       //缓存大小
    private String mBaseUrl;                                          //全局BaseUrl
    private String mSubUrl = "";                                      //全局SubUrl,介于BaseUrl和请求url之间
    private int mRetryCount = DEFAULT_RETRY_COUNT;                    //重试次数默认3次
    private int mRetryDelay = DEFAULT_RETRY_DELAY;                    //延迟xxms重试
    private int mRetryIncreaseDelay = DEFAULT_RETRY_INCREASE_DELAY;    //叠加延迟
    private HttpHeaders mCommonHeaders;                               //全局公共请求头
    private HttpParams mCommonParams;                                 //全局公共请求参数
    private OkHttpClient.Builder mOkHttpClientBuilder;                //okHttp请求的客户端
    private Retrofit.Builder mRetrofitBuilder;                        //Retrofit请求Builder
    private RxCache.Builder mRxCacheBuilder;                          //RxCache请求的Builder
    private CookieManager mCookieJar;                                 //Cookie管理

    //==================初始化=====================//

    private XHttp() {
        mOkHttpClientBuilder = new OkHttpClient.Builder();
        mOkHttpClientBuilder.hostnameVerifier(new DefaultHostnameVerifier());
        mOkHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        mOkHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        mOkHttpClientBuilder.writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        mRetrofitBuilder = new Retrofit.Builder();
        mRxCacheBuilder = new RxCache.Builder().init(sContext)
                .diskConverter(new SerializableDiskConverter());      //目前只支持Serializable和Gson缓存其它可以自己扩展
    }

    public static XHttp getInstance() {
        testInitialize();
        if (sInstance == null) {
            synchronized (XHttp.class) {
                if (sInstance == null) {
                    sInstance = new XHttp();
                }
            }
        }
        return sInstance;
    }

    /**
     * 必须在全局Application先调用，获取context上下文，否则缓存无法使用
     */
    public static void init(Application app) {
        sContext = app;
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        testInitialize();
        return sContext;
    }

    private static void testInitialize() {
        if (sContext == null) {
            throw new ExceptionInInitializerError("请先在全局Application中调用 XHttp.init() 初始化！");
        }
    }

    //==================api获取=====================//

    public static OkHttpClient getOkHttpClient() {
        return getInstance().mOkHttpClientBuilder.build();
    }

    public static Retrofit getRetrofit() {
        return getInstance().mRetrofitBuilder.build();
    }

    public static RxCache getRxCache() {
        return getInstance().mRxCacheBuilder.build();
    }

    /**
     * 对外暴露 OkHttpClient,方便自定义
     */
    public static OkHttpClient.Builder getOkHttpClientBuilder() {
        return getInstance().mOkHttpClientBuilder;
    }

    /**
     * 对外暴露 Retrofit,方便自定义
     */
    public static Retrofit.Builder getRetrofitBuilder() {
        return getInstance().mRetrofitBuilder;
    }

    /**
     * 对外暴露 RxCache,方便自定义
     */
    public static RxCache.Builder getRxCacheBuilder() {
        return getInstance().mRxCacheBuilder;
    }

    //==================日志、调试模式设置=====================//

    /**
     * 设置日志的打印模式
     *
     * @param loggingInterceptor 日志拦截器
     * @return
     */
    public XHttp debug(HttpLoggingInterceptor loggingInterceptor) {
        if (loggingInterceptor != null) {
            mOkHttpClientBuilder.addInterceptor(loggingInterceptor);
            HttpLog.debug(true);
        } else {
            HttpLog.debug(false);
        }
        return this;
    }

    /**
     * 设置网络请求的调试模式
     */
    public XHttp debug(boolean isDebug) {
        if (isDebug) {
            debug(new HttpLoggingInterceptor(HttpLog.DEFAULT_LOG_TAG, true)
                    .setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        HttpLog.debug(isDebug);
        return this;
    }

    /**
     * 设置网络请求的调试模式
     */
    public XHttp debug(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            debug(new HttpLoggingInterceptor(tag, true).setLevel(HttpLoggingInterceptor.Level.BODY));
            HttpLog.debug(tag);
        } else {
            HttpLog.debug(false);
        }
        return this;
    }

    //==================BaseUrl、SubUrl设置=====================//

    /**
     * 全局设置baseUrl
     */
    public XHttp setBaseUrl(String baseUrl) {
        mBaseUrl = Utils.checkNotNull(baseUrl, "mBaseUrl == null");
        return this;
    }

    /**
     * 全局设置subUrl
     */
    public XHttp setSubUrl(String subUrl) {
        mSubUrl = Utils.checkNotNull(subUrl, "mSubUrl == null");
        return this;
    }

    /**
     * 获取全局baseurl
     */
    public static String getBaseUrl() {
        return getInstance().mBaseUrl;
    }

    /**
     * 获取全局subUrl
     */
    public static String getSubUrl() {
        return getInstance().mSubUrl;
    }

    //==================超时、重试设置=====================//

    /**
     * 全局读取超时时间
     */
    public XHttp setReadTimeOut(long readTimeOut) {
        mOkHttpClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 全局写入超时时间
     */
    public XHttp setWriteTimeOut(long writeTimeout) {
        mOkHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 全局连接超时时间
     */
    public XHttp setConnectTimeout(long connectTimeout) {
        mOkHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 全局超时时间
     */
    public XHttp setTimeout(long timeout) {
        mOkHttpClientBuilder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        mOkHttpClientBuilder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
        mOkHttpClientBuilder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 超时重试次数
     */
    public XHttp setRetryCount(int retryCount) {
        if (retryCount < 0) throw new IllegalArgumentException("mRetryCount must >= 0");
        mRetryCount = retryCount;
        return this;
    }

    /**
     * 超时重试次数
     */
    public static int getRetryCount() {
        return getInstance().mRetryCount;
    }

    /**
     * 超时重试延迟时间
     */
    public XHttp setRetryDelay(int retryDelay) {
        if (retryDelay < 0) throw new IllegalArgumentException("mRetryDelay must > 0");
        mRetryDelay = retryDelay;
        return this;
    }

    /**
     * 超时重试延迟时间
     */
    public static int getRetryDelay() {
        return getInstance().mRetryDelay;
    }

    /**
     * 超时重试延迟叠加时间
     */
    public XHttp setRetryIncreaseDelay(int retryIncreaseDelay) {
        if (retryIncreaseDelay < 0)
            throw new IllegalArgumentException("mRetryIncreaseDelay must > 0");
        mRetryIncreaseDelay = retryIncreaseDelay;
        return this;
    }

    /**
     * 超时重试延迟叠加时间
     */
    public static int getRetryIncreaseDelay() {
        return getInstance().mRetryIncreaseDelay;
    }

    //==================缓存模式设置=====================//

    /**
     * 全局的缓存模式
     */
    public XHttp setCacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return this;
    }

    /**
     * 获取全局的缓存模式
     */
    public static CacheMode getCacheMode() {
        return getInstance().mCacheMode;
    }

    /**
     * 全局的缓存过期时间
     */
    public XHttp setCacheTime(long cacheTime) {
        if (cacheTime <= -1) cacheTime = DEFAULT_CACHE_NEVER_EXPIRE;
        mCacheTime = cacheTime;
        return this;
    }

    /**
     * 获取全局的缓存过期时间
     */
    public static long getCacheTime() {
        return getInstance().mCacheTime;
    }

    /**
     * 全局的缓存大小,默认50M
     */
    public XHttp setCacheMaxSize(long maxSize) {
        mCacheMaxSize = maxSize;
        return this;
    }

    /**
     * 获取全局的缓存大小
     */
    public static long getCacheMaxSize() {
        return getInstance().mCacheMaxSize;
    }

    /**
     * 全局设置缓存的版本，默认为1，缓存的版本号
     */
    public XHttp setCacheVersion(int cacheVersion) {
        if (cacheVersion < 0)
            throw new IllegalArgumentException("cache version must > 0");
        mRxCacheBuilder.appVersion(cacheVersion);
        return this;
    }

    /**
     * 全局设置缓存的路径，默认是应用包下面的缓存
     */
    public XHttp setCacheDirectory(File directory) {
        mCacheDirectory = Utils.checkNotNull(directory, "directory == null");
        mRxCacheBuilder.diskDir(directory);
        return this;
    }

    /**
     * 获取缓存的路径
     */
    public static File getCacheDirectory() {
        return getInstance().mCacheDirectory;
    }

    /**
     * 全局设置缓存的转换器
     */
    public XHttp setCacheDiskConverter(IDiskConverter converter) {
        mRxCacheBuilder.diskConverter(Utils.checkNotNull(converter, "converter == null"));
        return this;
    }

    /**
     * 全局设置OkHttp的缓存, 默认是3天
     */
    public XHttp setHttpCache(Cache cache) {
        mCache = cache;
        return this;
    }

    /**
     * 获取OkHttp的缓存
     */
    public static Cache getHttpCache() {
        return getInstance().mCache;
    }

    //==================公共请求参数、请求头=====================//

    /**
     * 添加全局公共请求参数
     */
    public XHttp addCommonParams(HttpParams commonParams) {
        if (mCommonParams == null) mCommonParams = new HttpParams();
        mCommonParams.put(commonParams);
        return this;
    }

    /**
     * 添加全局公共请求参数
     */
    public XHttp addCommonHeaders(HttpHeaders commonHeaders) {
        if (mCommonHeaders == null) mCommonHeaders = new HttpHeaders();
        mCommonHeaders.put(commonHeaders);
        return this;
    }

    /**
     * 获取全局公共请求参数
     */
    public HttpParams getCommonParams() {
        return mCommonParams;
    }

    /**
     * 获取全局公共请求头
     */
    public HttpHeaders getCommonHeaders() {
        return mCommonHeaders;
    }

    //==================OkHttpClient设置拦截器、代理、连接池=====================//

    /**
     * 添加全局拦截器
     */
    public XHttp addInterceptor(Interceptor interceptor) {
        mOkHttpClientBuilder.addInterceptor(Utils.checkNotNull(interceptor, "interceptor == null"));
        return this;
    }

    /**
     * 添加全局网络拦截器
     */
    public XHttp addNetworkInterceptor(Interceptor interceptor) {
        mOkHttpClientBuilder.addNetworkInterceptor(Utils.checkNotNull(interceptor, "interceptor == null"));
        return this;
    }

    /**
     * 全局设置代理
     */
    public XHttp setOkproxy(Proxy proxy) {
        mOkHttpClientBuilder.proxy(Utils.checkNotNull(proxy, "mProxy == null"));
        return this;
    }

    /**
     * 全局设置请求的连接池
     */
    public XHttp setOkconnectionPool(ConnectionPool connectionPool) {
        mOkHttpClientBuilder.connectionPool(Utils.checkNotNull(connectionPool, "connectionPool == null"));
        return this;
    }

    //==================设置Retrofit的OkHttpClient、ConverterFactory、CallAdapterFactory、CallbackExecutor、CallFactory=====================//

    /**
     * 全局为Retrofit设置自定义的OkHttpClient
     */
    public XHttp setOkclient(OkHttpClient client) {
        mRetrofitBuilder.client(Utils.checkNotNull(client, "client == null"));
        return this;
    }

    /**
     * 全局设置Converter.Factory,默认GsonConverterFactory.create()
     */
    public XHttp addConverterFactory(Converter.Factory factory) {
        mRetrofitBuilder.addConverterFactory(Utils.checkNotNull(factory, "factory == null"));
        return this;
    }

    /**
     * 全局设置CallAdapter.Factory,默认RxJavaCallAdapterFactory.create()
     */
    public XHttp addCallAdapterFactory(CallAdapter.Factory factory) {
        mRetrofitBuilder.addCallAdapterFactory(Utils.checkNotNull(factory, "factory == null"));
        return this;
    }

    /**
     * 全局设置Retrofit callbackExecutor
     */
    public XHttp setCallbackExecutor(Executor executor) {
        mRetrofitBuilder.callbackExecutor(Utils.checkNotNull(executor, "executor == null"));
        return this;
    }

    /**
     * 全局设置Retrofit对象Factory
     */
    public XHttp setCallFactory(okhttp3.Call.Factory factory) {
        mRetrofitBuilder.callFactory(Utils.checkNotNull(factory, "factory == null"));
        return this;
    }

    //==================https规则设置=====================//

    /**
     * https的全局访问规则
     */
    public XHttp setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        mOkHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    /**
     * https的全局自签名证书
     */
    public XHttp setCertificates(InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, certificates);
        mOkHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return this;
    }

    /**
     * https双向认证证书
     */
    public XHttp setCertificates(InputStream bksFile, String password, InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(bksFile, password, certificates);
        mOkHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return this;
    }

    //==================cookie=====================//

    /**
     * 全局cookie存取规则
     */
    public XHttp setCookieStore(CookieManager cookieManager) {
        mCookieJar = cookieManager;
        mOkHttpClientBuilder.cookieJar(mCookieJar);
        return this;
    }

    /**
     * 获取全局的cookie实例
     */
    public static CookieManager getCookieJar() {
        return getInstance().mCookieJar;
    }

}


