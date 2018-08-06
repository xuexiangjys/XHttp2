# XHttp2

[![xh][xhsvg]][xh]  [![api][apisvg]][api]

一个功能强悍的网络请求库，使用RxJava2 + Retrofit2 + OKHttp组合进行封装。

## 关于我

[![github](https://img.shields.io/badge/GitHub-xuexiangjys-blue.svg)](https://github.com/xuexiangjys)   [![csdn](https://img.shields.io/badge/CSDN-xuexiangjys-green.svg)](http://blog.csdn.net/xuexiangjys)

## 特征

* 支持默认、全局、局部三个层次的配置功能。
* 支持动态配置和自定义底层框架Okhttpclient、Retrofit.
* 加入基础ApiService，减少Api冗余。
* 支持多种方式访问网络GET、POST、PUT、DELETE等请求协议。
* 支持网络缓存,六种缓存策略可选,涵盖大多数业务场景。
* 支持固定添加header和动态添加header。
* 支持添加全局参数和动态添加局部参数。
* 支持文件下载、多文件上传和表单提交数据。
* 支持文件请求、上传、下载的进度回调、错误回调，也可以自定义回调。
* 支持任意数据结构的自动解析。
* 支持添加动态参数例如timeStamp时间戳、token、签名sign。
* 支持自定义的扩展API。
* 支持多个请求合并。
* 支持Cookie管理。
* 支持异步、同步请求。
* 支持Https、自签名网站Https的访问、双向验证。
* 支持失败重试机制，可以指定重试次数、重试间隔时间。
* 支持根据key删除网络缓存和清空网络缓存。
* 提供默认的标准ApiResult（遵循OpenApi格式）解析和回调，并且可自定义ApiResult。
* 支持取消数据请求，取消订阅，带有对话框的请求不需要手动取消请求，对话框消失会自动取消请求。
* 支持请求数据结果采用回调和订阅两种方式。
* 提供"默认API"、"接口协议"以及"统一请求实体"三种方式进行网络请求，支持自定义网络请求协议。
* 返回结果和异常统一处理，支持自定义异常处理。
* 结合RxJava，线程切换灵活。
* 请求实体支持注解配置，配置网络请求接口的url、是否需要验证token以及请求参数的key。
* 拥有统一的网络请求取消机制。

## 1、演示（请star支持）

### 1.1、Demo截图

### 1.2、Demo下载

### 1.3、api服务安装

服务端的搭建详细[请点击查看](https://github.com/xuexiangjys/XHttpApi)

## 2、如何使用
目前支持主流开发工具AndroidStudio的使用，直接配置build.gradle，增加依赖即可.

### 2.1、Android Studio导入方法，添加Gradle依赖

1.先在项目根目录的 build.gradle 的 repositories 添加:
```
allprojects {
     repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

2.然后在dependencies添加:

```
dependencies {
  ...
  implementation 'com.github.xuexiangjys:XHttp2:1.0.0'
  implementation 'com.google.code.gson:gson:2.8.2'
  implementation 'com.squareup.okhttp3:okhttp:3.10.0'
  implementation 'io.reactivex.rxjava2:rxjava:2.1.12'
  implementation 'io.reactivex.rxjava2:rxandroid:2.0.2'
}
```

3.在Application中初始化XHttpSDK

```
XHttpSDK.init(this);   //初始化网络请求框架，必须首先执行
XHttpSDK.debug("XHttp");  //需要调试的时候执行
XHttpSDK.setBaseUrl(SettingSPUtils.getInstance().getApiURL());  //设置网络请求的基础地址
```

4.全局初始化配置（非必要)

除了上述的操作以外，你还可以使用`XHttp.getInstance()`对网络请求框架进行全局性参数配置，配置一些公用默认的参数，这样我们就不需要为每个请求都进行设置。方法如下：

方法名 | 备注
:-|:-
debug | 设置日志的打印模式
setBaseUrl | 设置全局baseUrl
setSubUrl | 设置全局subUrl
setReadTimeOut | 设置全局读取超时时间
setWriteTimeOut | 设置全局写入超时时间
setConnectTimeout | 设置全局连接超时时间
setTimeout | 设置全局超时时间
setRetryCount | 设置全局超时重试次数
setRetryDelay | 设置全局超时重试延迟时间
setRetryIncreaseDelay | 设置全局超时重试延迟叠加时间
setCacheMode | 设置全局的缓存模式
setIsDiskCache | 设置是否是磁盘缓存
setMemoryMaxSize | 设置内存缓存的最大数量
setCacheTime | 设置全局的缓存过期时间
setCacheMaxSize | 设置全局的磁盘缓存大小,默认50M
setCacheDirectory | 设置全局缓存的路径，默认是应用包下面的缓存
setCacheDiskConverter | 设置全局缓存的转换器
addCommonParams | 添加全局公共请求参数
addCommonHeaders | 添加全局公共请求参数
addInterceptor | 添加全局拦截器
addNetworkInterceptor | 添加全局网络拦截器
setOkproxy | 全局设置OkHttpClient的代理
setOkconnectionPool | 设置全局OkHttpClient的请求连接池
setOkclient | 全局为Retrofit设置自定义的OkHttpClient
addConverterFactory | 设置全局Converter.Factory,默认GsonConverterFactory.create()
addCallAdapterFactory | 设置全局CallAdapter.Factory,默认RxJavaCallAdapterFactory.create()
setHostnameVerifier | 设置https的全局访问规则
setCertificates | 设置https的全局自签名证书
setCookieStore | 设置全局cookie存取规则

--------------

## 如何进行网络请求

### 1、使用XHttp默认api进行请求

1.使用XHttp.post、XHttp.get、XHttp.delete、XHttp.put、XHttp.downLoad构建请求。

2.修改request的请求参数。

方法名 | 类型 | 默认值 | 备注
:-|:-:|:-:|:-
baseUrl | String | ／ | 设置该请求的baseUrl
timeOut | long | 10000 | 设置超时时间
accessToken | boolean | false | 是否需要验证token
threadType | String | ／ | 设置请求的线程调度类型
syncRequest | boolean | false | 设置是否是同步请求（不开子线程）
onMainThread | boolean | true | 请求完成后是否回到主线程
upJson | String | "" | 上传Json格式的数据请求
keepJson | boolean | false | 返回保持json的形式
retryCount | int | ／ | 设置超时重试的次数
retryDelay | int | ／ | 设置超时重试的延迟时间
retryIncreaseDelay | int | ／ | 设置超时重试叠加延时
headers | HttpHeaders | ／ | 添加头信息
params | HttpParams | ／| 设置表单请求参数
cacheMode | CacheMode | CacheMode.NO_CACHE | 设置缓存的模式

3.调用`execute`方法执行请求。execute一般有如下两种方式：

* execute(CallBack callBack): 直接回调结果。

* execute(Class clazz)和execute(Type type): 回调Observable<T>对象，可通过订阅获取到结果。

4.请求使用演示

```
XHttp.get("/user/getAllUser")
        .syncRequest(false) //异步请求
        .onMainThread(true) //回到主线程
        .execute(new SimpleCallBack<List<User>>() {
            @Override
            public void onSuccess(List<User> response) {
                refreshLayout.finishRefresh(true);
                if (response != null && response.size() > 0) {
                    mUserAdapter.refresh(response);
                    mLlStateful.showContent();
                } else {
                    mLlStateful.showEmpty();
                }
            }
            @Override
            public void onError(ApiException e) {
                refreshLayout.finishRefresh(false);
                mLlStateful.showError(e.getMessage(), null);
            }

        });
```

```
XHttp.post("/user/deleteUser")
        .params("userId", item.getUserId())
        .execute(Boolean.class)
        .subscribeWith(new TipRequestSubscriber<Boolean>() {
            @Override
            protected void onSuccess(Boolean aBoolean) {
                ToastUtils.toast("删除成功！");
                setFragmentResult(RESULT_OK, null);
                popToBack();
            }
        });

```

--------------

### 2、使用XHttpRequest封装的统一请求实体进行请求

在使用它之前，需要下载/定义对应的实体协议，如下：

```
@RequestParams(url = "/user/addUser", accessToken = false)
public static class UserService_AddUser extends XHttpRequest {

    /**
     *
     */
    public User request;

    @Override
    protected Boolean getResponseEntityType() {
        return null;
    }
}
```

1.注解说明

* @RequestParams

注解参数 | 类型 | 默认值 | 备注
:-|:-:|:-:|:-
baseUrl | String | "" | 设置该请求的baseUrl
url | String | "" | 请求网络接口地址
timeout | long | 15000 | 设置超时时间
accessToken | boolean | true | 设置是否需要验证token
cacheMode | CacheMode | CacheMode.NO_CACHE | 设置请求的缓存模式

* @ParamKey

注解参数 | 类型 | 默认值 | 备注
:-|:-:|:-:|:-
key | String | / | 请求参数的key

2.使用XHttpSDK进行请求。

* post(XHttpRequest xHttpRequest, boolean isSyncRequest, boolean toMainThread)：
  获取PostRequest请求（使用实体参数名作为请求Key）。

* postToMain(XHttpRequest xHttpRequest)：
  获取PostRequest请求（主线程->主线程）。

* postToIO(XHttpRequest xHttpRequest)：
  获取PostRequest请求（主线程->子线程）。

* postInThread(XHttpRequest xHttpRequest)：
  获取PostRequest请求（子线程->子线程）。

* execute(XHttpRequest xHttpRequest, boolean isSyncRequest, boolean toMainThread) ：
  执行PostRequest请求，返回observable对象（使用实体参数名作为请求Key）。

* `executeToMain(XHttpRequest xHttpRequest)`:
  执行post请求，返回observable对象（主线程->主线程）

* `executeToMain(XHttpRequest xHttpRequest，BaseSubscriber<T> subscriber)`:
  执行post请求并进行订阅，返回订阅信息（主线程->主线程）

3.请求使用演示。

```
XHttpRequest req = ApiProvider.getAddUserReq(getRandomUser());
XHttpSDK.executeToMain(req, new ProgressLoadingSubscriber<Boolean>(mIProgressLoader) {
    @Override
    public void onSuccess(Boolean aBoolean) {
        ToastUtils.toast("用户添加成功！");
        mRefreshLayout.autoRefresh();
    }
});
```
--------------

### 3、使用XHttpProxy代理进行请求

在使用它之前，需要下载/定义对应的接口协议，如下：

```
/**
 * 订单
 */
public interface IOrder {
    /**
     * 购买书
     *
     * @param bookId 用户名
     * @param userId 密码
     */
    @NetMethod(ParameterNames = {"bookId", "userId", "number"}, Url = "/order/addOrder/")
    Observable<Boolean> buyBook(int bookId, int userId, int number);
}
```

1.注解说明

* @NetMethod

注解参数 | 类型 | 默认值 | 备注
:-|:-:|:-:|:-
ParameterNames | String\[\] | {} | 参数名集合
BaseUrl | String | "" | 设置该请求的baseUrl
Url | String | "" | 请求网络接口地址
Timeout | long | 10000 | 设置超时时间
AccessToken | boolean | true | 设置是否需要验证token
CacheMode | CacheMode | CacheMode.NO_CACHE | 设置请求的缓存模式

2.使用XHttpProxy进行请求。

构建一个XHttpProxy，将定义的api接口传入后，直接调用接口进行请求。

构造XHttpProxy需要传入`ThreadType`,默认是`ThreadType.TO_MAIN`。

* TO_MAIN: executeToMain(main  -> io -> main)
* TO_IO: executeToIO(main  -> io -> io)
* IN_THREAD: executeInThread(io  -> io -> io)

3.请求使用演示。

```
//使用XHttpProxy进行接口代理请求
XHttpProxy.proxy(TestApi.IOrder.class)
        .buyBook(mBookAdapter.getItem(position).getBookId(), UserManager.getInstance().getUser().getUserId(), 1)
        .subscribeWith(new TipRequestSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                ToastUtils.toast("图书购买" + (aBoolean ? "成功" : "失败") + "！");
                mRefreshLayout.autoRefresh();
            }
        });
```

--------------

### 4、文件上传和下载

1.文件上传【multipart/form-data】

使用post的文件表单上传。使用`XHttp.post`,然后使用`params`传递附带的参数，使用`uploadFile`传递需要上传的文件，使用示例如下:

```
mIProgressLoader.updateMessage("上传中...");
XHttp.post("/book/uploadBookPicture")
        .params("bookId", book.getBookId())
        .uploadFile("file", FileUtils.getFileByPath(mPicturePath), new IProgressResponseCallBack() {
            @Override
            public void onResponseProgress(long bytesWritten, long contentLength, boolean done) {

            }
        }).execute(Boolean.class)
        .compose(RxLifecycle.with(this).<Boolean>bindToLifecycle())
        .subscribeWith(new ProgressLoadingSubscriber<Boolean>(mIProgressLoader) {
            @Override
            public void onSuccess(Boolean aBoolean) {
                mIsEditSuccess = true;
                ToastUtils.toast("图片上传" + (aBoolean ? "成功" : "失败") + "！");
            }
        });
```

2.文件下载

使用`XHttp.downLoad`，传入下载的地址url、保存文件的路径以及文件名即可完成文件的下载，使用示例如下:

```
XHttp.downLoad(BookAdapter.getBookImgUrl(book))
        .savePath(PathUtils.getExtPicturesPath())
        .execute(new DownloadProgressCallBack<String>() {
            @Override
            public void onStart() {
                HProgressDialogUtils.showHorizontalProgressDialog(getContext(), "图片下载中...", true);
            }

            @Override
            public void onError(ApiException e) {
                ToastUtils.toast(e.getMessage());
                HProgressDialogUtils.cancel();
            }

            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                HProgressDialogUtils.onLoading(contentLength, bytesRead); //更新进度条
            }

            @Override
            public void onComplete(String path) {
                ToastUtils.toast("图片下载成功, 保存路径:" + path);
                HProgressDialogUtils.cancel();
            }
        });
```

--------------

## 高阶网络请求操作

### 请求生命周期绑定

1.请求loading加载和请求生命周期绑定

在请求时，订阅`ProgressLoadingSubscriber`或者`ProgressLoadingCallBack`，传入请求消息加载者`IProgressLoader`，即可完成生命周期的绑定。示例如下：

```
XHttpRequest req = ApiProvider.getAddUserReq(getRandomUser());
    XHttpSDK.executeToMain(req, new ProgressLoadingSubscriber<Boolean>(mIProgressLoader) {
        @Override
        public void onSuccess(Boolean aBoolean) {
            ToastUtils.toast("用户添加成功！");
            mRefreshLayout.autoRefresh();
        }
    });
```

2.网络请求生命周期和Activity/Fragment生命周期绑定

(1)这里需要依赖一下RxUtil2
```
implementation 'com.github.xuexiangjys:rxutil2:1.1.2'
```

(2)在所在的Activity的onCreate()下锁定Activity.

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RxLifecycle.injectRxLifecycle(this);
}
```

(3)然后在请求中使用RxJava的`compose`的操作符进行绑定。

```
.compose(RxLifecycle.with(this).<Boolean>bindToLifecycle())
```

### 拦截器

1.日志拦截器

(1)框架默认提供一个实现好的日志拦截器`HttpLoggingInterceptor`,通过`XHttpSDK.debug("XHttp");`就可以设置进去，它有5种打印模式

* NONE: 不打印log

* BASIC: 只打印"请求首行"和"响应首行"。

* HEADERS: 打印请求和响应的所有 Header

* PARAM: 只打印请求和响应参数

* BODY: 打印所有数据(默认是这种)

(2)如果需要对网络请求的相关参数进行自定义记录的话，可以继承`HttpLoggingInterceptor`实现一个自己的网络请求日志拦截器，重写`logForRequest`和`logForResponse`两个方法即可。

(3)设置自定义的日志拦截器.
```
XHttpSDK.debug(new CustomLoggingInterceptor());
```

2.动态参数添加拦截器

> 有时候，我们需要对所有请求添加一些固定的请求参数，但是这些参数的值又是变化的，这个时候我们就需要动态添加请求参数【例如，请求的token、时间戳以及签名等】

(1)继承`BaseDynamicInterceptor`，实现`updateDynamicParams`方法，如下：

```
@Override
protected TreeMap<String, Object> updateDynamicParams(TreeMap<String, Object> dynamicMap) {
    if (isAccessToken()) {//是否添加token
        dynamicMap.put("token", TokenManager.getInstance().getToken());
    }
    if (isSign()) {//是否添加签名
        dynamicMap.put("sign", TokenManager.getInstance().getSign());
    }
    if (isTimeStamp()) {//是否添加请求时间戳
        dynamicMap.put("timeStamp", DateUtils.getNowMills());
    }
    return dynamicMap;//dynamicMap:是原有的全局参数+局部参数+新增的动态参数
}
```

(2)设置动态参数添加拦截器。

```

```

--------------

## 混淆配置

```
#XHttp2
-keep class com.xuexiang.xhttp2.model.** { *; }
-keep class com.xuexiang.xhttp2.cache.model.** { *; }
-keep class com.xuexiang.xhttp2.cache.stategy.**{*;}
-keep class com.xuexiang.xhttp2.annotation.** { *; }

#okhttp
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn javax.annotation.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#如果用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错
-keepattributes Signature
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod
-keep class org.xz_sale.entity.**{*;}
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
```

## 特别感谢

https://github.com/zhou-you/RxEasyHttp


## 联系方式

[![](https://img.shields.io/badge/点击一键加入QQ交流群-602082750-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=9922861ef85c19f1575aecea0e8680f60d9386080a97ed310c971ae074998887)

![](https://github.com/xuexiangjys/XPage/blob/master/img/qq_group.jpg)

[xhsvg]: https://img.shields.io/badge/XHttp2-v1.0.0-brightgreen.svg
[xh]: https://github.com/xuexiangjys/XHttp2
[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14