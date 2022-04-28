# XHttp2

[![](https://jitpack.io/v/xuexiangjys/XHttp2.svg)](https://jitpack.io/#xuexiangjys/XHttp2)
[![api](https://img.shields.io/badge/API-14+-brightgreen.svg)](https://android-arsenal.com/api?level=14)
[![I](https://img.shields.io/github/issues/xuexiangjys/XHttp2.svg)](https://github.com/xuexiangjys/XHttp2/issues)
[![Star](https://img.shields.io/github/stars/xuexiangjys/XHttp2.svg)](https://github.com/xuexiangjys/XHttp2)

一个功能强悍的网络请求库，使用RxJava2 + Retrofit2 + OKHttp组合进行封装。还不赶紧点击[使用说明文档](https://github.com/xuexiangjys/XHttp2/wiki)，体验一下吧！

在提issue前，请先阅读[【提问的智慧】](https://xuexiangjys.blog.csdn.net/article/details/83344235)，并严格按照[issue模板](https://github.com/xuexiangjys/XHttp2/issues/new/choose)进行填写，节约大家的时间。

在使用前，请一定要仔细阅读[使用说明文档](https://github.com/xuexiangjys/XHttp2/wiki),重要的事情说三遍！！！

在使用前，请一定要仔细阅读[使用说明文档](https://github.com/xuexiangjys/XHttp2/wiki),重要的事情说三遍！！！

在使用前，请一定要仔细阅读[使用说明文档](https://github.com/xuexiangjys/XHttp2/wiki),重要的事情说三遍！！！

## 关于我

| 公众号   | 掘金     |  知乎    |  CSDN   |   简书   |   思否  |   哔哩哔哩  |   今日头条
|---------|---------|--------- |---------|---------|---------|---------|---------|
| [我的Android开源之旅](https://t.1yb.co/Irse)  |  [点我](https://juejin.im/user/598feef55188257d592e56ed/posts)    |   [点我](https://www.zhihu.com/people/xuexiangjys/posts)       |   [点我](https://xuexiangjys.blog.csdn.net/)  |   [点我](https://www.jianshu.com/u/6bf605575337)  |   [点我](https://segmentfault.com/u/xuexiangjys)  |   [点我](https://space.bilibili.com/483850585)  |   [点我](https://img.rruu.net/image/5ff34ff7b02dd)

## X系列库快速集成

为了方便大家快速集成X系列框架库，我提供了一个空壳模版供大家参考使用: https://github.com/xuexiangjys/TemplateAppProject

---

## 特征

* 支持默认、全局、局部三个层次的配置功能。
* 支持动态配置和自定义底层框架OkHttpClient、Retrofit.
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

点击查看[项目设计类图](https://github.com/xuexiangjys/XHttp2/blob/master/img/xhttp_uml.png)。

## Star趋势图

[![Stargazers over time](https://starchart.cc/xuexiangjys/XHttp2.svg)](https://starchart.cc/xuexiangjys/XHttp2)

---

## 1、演示（请star支持）

### 1.1、Demo演示动画

![][demo-gif]

### 1.2、Demo下载

[![downloads][download-svg]][download-url]

![][download-img]

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

2.然后在应用项目(一般是app)的 `build.gradle` 的 dependencies 添加:

以下是版本说明，选择一个即可。

* androidx版本：2.0.0及以上

```
dependencies {
  ...
  implementation 'com.github.xuexiangjys:XHttp2:2.0.4'

  implementation 'com.google.code.gson:gson:2.8.5'
  implementation 'com.squareup.okhttp3:okhttp:3.10.0'
  implementation 'io.reactivex.rxjava2:rxjava:2.2.0'
  implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'
}
```

* support版本：1.0.4及以下

```
dependencies {
  ...
  implementation 'com.github.xuexiangjys:XHttp2:1.0.4'

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
setStrictMode | 设置严格模式，在严格模式下，json返回的data数据不能为null

---

## 如何进行网络请求

需要注意的是，所以请求返回的结果必须要满足以下格式:

```
{
    "Code":0, //响应码，0为成功，否则失败
    "Msg":"", //请求失败的原因说明
    "Data":{} //返回的数据对象
}
```

其中`Code`、`Msg`、`Data`建议使用大写字母，当然使用小写字母也没有问题，否则无法解析成功。

***【注意】这里`Code`为0才代表请求成功，如果你的Code码不是0代表成功，你可以使用`XHttpSDK.setSuccessCode`设置代表成功的值。***

需要自定义返回的实体API请[点击查看](#自定义api请求)

### 1、使用XHttp默认api进行请求

1.使用XHttp.post、XHttp.get、XHttp.delete、XHttp.put、XHttp.downLoad构建请求。

2.修改request的请求参数。

方法名 | 类型 | 默认值 | 备注
:-|:-:|:-:|:-
baseUrl | String | ／ | 设置该请求的baseUrl
timeOut | long | 15000 | 设置超时时间
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

---

### 2、使用XHttpRequest封装的统一请求实体进行请求【仅支持post请求】

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
keepJson | boolean | false | 是否保存json
accessToken | boolean | true | 设置是否需要验证token
cacheMode | CacheMode | CacheMode.NO_CACHE | 设置请求的缓存模式
cacheTime | long | -2(使用全局设置） | 设置缓存有效时间

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
---

### 3、使用XHttpProxy代理进行请求

在使用它之前，需要下载/定义对应的接口协议，如下：

```
/**
 * 图书管理
 */
public interface IBook {
    /**
     * 购买书
     *
     * @param bookId 图书ID
     * @param userId 用户ID
     * @param number 购买数量
     */
    @NetMethod(parameterNames = {"bookId", "userId", "number"}, url = "/order/addOrder/", accessToken = false)
    Observable<Boolean> buyBook(int bookId, int userId, int number);
    /**
     * 获取图书
     *
     * @param pageNum 第几页数
     * @param pageSize 每页的数量
     */
    @NetMethod(parameterNames = {"pageNum", "pageSize"}, paramType = FORM_BODY, url = "/book/findBooks/", accessToken = false)
    Observable<List<Book>> getBooks(int pageNum, int pageSize);

    /**
     * 获取所有图书
     *
     */
    @NetMethod(action = GET, url = "/book/getAllBook", accessToken = false)
    Observable<List<Book>> getAllBooks();
}
```

1.注解说明

* @NetMethod

注解参数 | 类型 | 默认值 | 备注
:-|:-:|:-:|:-
parameterNames | String\[\] | {} | 参数名集合
paramType | int | JSON=1 | param的类型
action | String | POST="post" | 请求动作
baseUrl | String | "" | 设置该请求的baseUrl
url | String | "" | 请求网络接口地址
timeout | long | 15000 | 设置超时时间
keepJson | boolean | false | 是否保存json
accessToken | boolean | true | 设置是否需要验证token
cacheMode | CacheMode | CacheMode.NO_CACHE | 设置请求的缓存模式
cacheTime | long | -2(使用全局设置） | 设置缓存有效时间
cacheKeyIndex | int | -1(所有参数） | 作为缓存key的请求参数索引

2.使用XHttpProxy进行请求。

构建一个XHttpProxy，将定义的api接口传入后，直接调用接口进行请求。

构造XHttpProxy可以传入`ThreadType`,默认是`ThreadType.TO_MAIN`。

* TO_MAIN: executeToMain(main  -> io -> main)

> 【注意】请确保网络请求在主线程中【实质是异步请求(切换到io线程)，且响应的线程又切换至主线程】

* TO_IO: executeToIO(main  -> io -> io)

> 【注意】请确保网络请求在主线程中【实质是异步请求(切换到io线程)，不过响应的线程不变，还是之前请求的那个io线程】

* IN_THREAD: executeInThread(io  -> io -> io)

> 【注意】请确保网络请求在子线程中才可以使用该类型【实质是不做任何线程调度的同步请求】

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

---

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

---

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

#### 日志拦截器

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

#### 动态参数添加拦截器

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
XHttpSDK.addInterceptor(new CustomDynamicInterceptor()); //设置动态参数添加拦截器
```

#### 失效请求校验拦截器

> 当服务端返回一些独特的错误码（一般是token校验错误、失效，请求过于频繁等），需要我们进行全局性的拦截捕获，并作出相应的响应时，我们就需要定义一个特殊的拦截器求处理这些请求。

(1)继承`BaseExpiredInterceptor`，实现`isResponseExpired`和`responseExpired`方法，如下：

```
/**
 * 判断是否是失效的响应
 *
 * @param oldResponse
 * @param bodyString
 * @return {@code true} : 失效 <br>  {@code false} : 有效
 */
@Override
protected ExpiredInfo isResponseExpired(Response oldResponse, String bodyString) {
    int code = JSONUtils.getInt(bodyString, ApiResult.CODE, 0);
    ExpiredInfo expiredInfo = new ExpiredInfo(code);
    switch (code) {
        case TOKEN_INVALID:
        case TOKEN_MISSING:
            expiredInfo.setExpiredType(KEY_TOKEN_EXPIRED)
                    .setBodyString(bodyString);
            break;
        case AUTH_ERROR:
            expiredInfo.setExpiredType(KEY_UNREGISTERED_USER)
                    .setBodyString(bodyString);
            break;
        default:
            break;
    }
    return expiredInfo;
}

/**
 * 失效响应的处理
 *
 * @return 获取新的有效请求响应
 */
@Override
protected Response responseExpired(Response oldResponse, Chain chain, ExpiredInfo expiredInfo) {
    switch(expiredInfo.getExpiredType()) {
        case KEY_TOKEN_EXPIRED:
            User user = TokenManager.getInstance().getLoginUser();
            if (user != null) {
                final boolean[] isGetNewToken = {false};
                HttpLog.e("正在重新获取token...");
                XHttpProxy.proxy(TestApi.IAuthorization.class, ThreadType.IN_THREAD)
                        .login(user.getLoginName(), user.getPassword())
                        .subscribeWith(new NoTipRequestSubscriber<LoginInfo>() {
                            @Override
                            protected void onSuccess(LoginInfo loginInfo) {
                                TokenManager.getInstance()
                                        .setToken(loginInfo.getToken())
                                        .setLoginUser(loginInfo.getUser());
                                isGetNewToken[0] = true;
                                HttpLog.e("重新获取token成功：" + loginInfo.getToken());
                            }
                        });
                if (isGetNewToken[0]) {
                    try {
                        HttpLog.e("使用新的token重新进行请求...");
                        return chain.proceed(HttpUtils.updateUrlParams(chain.request(), "token", TokenManager.getInstance().getToken()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                XRouter.getInstance().build("/xhttp/login").navigation();
                return HttpUtils.getErrorResponse(oldResponse, expiredInfo.getCode(), "请先进行登录！");
            }
            break;
        case KEY_UNREGISTERED_USER:
            return HttpUtils.getErrorResponse(oldResponse, expiredInfo.getCode(), "非法用户登录！");
        default:
            break;
    }
    return null;
}

```

(2)设置失效请求校验拦截器。

```
XHttpSDK.addInterceptor(new CustomExpiredInterceptor()); //请求失效校验拦截器
```

### 自定义API请求

#### 自定义请求响应的API结构

如果你不想使用默认的ApiResult实体作为统一的服务端响应实体，比如说你想要下面的响应实体:

```
private int errorCode; //请求的错误码
private String errorInfo; //请求错误的原因描述
private T result; //请求的结果
private long timeStamp; //服务端返回的时间戳
```

(1)首先，继承`ApiResult`实体，重写其`getCode`、`getMsg`、`isSuccess`、`getData`和`setData`方法。

```
public class CustomApiResult<T> extends ApiResult<T> {

    private int errorCode;
    private String errorInfo;
    private T result;
    private long timeStamp;

    public int getErrorCode() {
        return errorCode;
    }

    public CustomApiResult<T> setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public CustomApiResult<T> setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
        return this;
    }

    public T getResult() {
        return result;
    }

    public CustomApiResult<T> setResult(T result) {
        this.result = result;
        return this;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public CustomApiResult<T> setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    @Override
    public int getCode() {
        return errorCode;
    }

    @Override
    public String getMsg() {
        return errorInfo;
    }

    @Override
    public boolean isSuccess() {
        return errorCode == 0;
    }

    @Override
    public void setData(T data) {
        result = data;
    }

    @Override
    public T getData() {
        return result;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "errorCode='" + errorCode + '\'' +
                ", errorInfo='" + errorInfo + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", result=" + result +
                '}';
    }
}
```

(2)进行请求的时候使用`execute(CallBackProxy)`或者`execute(CallClazzProxy`方法进行请求

```
XHttp.get("/test/testCustomResult")
            .execute(new CallBackProxy<CustomApiResult<Boolean>, Boolean>(new TipRequestCallBack<Boolean>() {
                @Override
                public void onSuccess(Boolean response) throws Throwable {
                    ToastUtils.toast("请求成功：" + response);
                }
            }){});  //千万注意，这里的{}一定不能去掉，否则解析错误
```

【注意】上面提示的{}一定不能去掉，否则解析错误, 会报"ApiResult.class.isAssignableFrom(cls) err!!"的错误。

如果你觉得写一长串比较麻烦，你可以自定义请求继承你需要的请求方式，例如这里是get请求，我们可以这样写:

```
public class CustomGetRequest extends GetRequest {

    public CustomGetRequest(String url) {
        super(url);
    }

    @Override
    public <T> Observable<T> execute(Type type) {
        return execute(new CallClazzProxy<CustomApiResult<T>, T>(type) {
        });
    }

    @Override
    public <T> Disposable execute(CallBack<T> callBack) {
        return execute(new CallBackProxy<CustomApiResult<T>, T>(callBack) {
        });
    }
}
```

然后我们就可以用自定义的`CustomGetRequest`进行请求了,是不是简化了很多呢。

```
new CustomGetRequest("/test/testCustomResult")
        .execute(new TipRequestCallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean response) throws Throwable {
                ToastUtils.toast("请求成功：" + response);
            }
        });
```


#### 使用自定义的retrofit接口

如果你对retrofit接口情有独钟，我也提供了相应的api方便调用.

1.定义retrofit接口。例如我定义一个用户添加的接口:

```
/**
 * 使用的是retrofit的接口定义
 */
public interface UserService {
    @POST("/user/registerUser/")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<ApiResult<Boolean>> registerUser(@Body RequestBody jsonBody);


    @POST("/user/registerUser/")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<ApiResult> register(@Body User user);
}
```

2.使用`XHttp.custom()`构建的`CustomRequest`进行请求，你可以使用`apiCall`和`call`进行请求。

* apiCall: 针对的是retrofit定义的接口，返回的是Observable<ApiResult<T>>的情况，对ApiResult进行拆包，直接获取数据。对于上面定义的第一个接口`registerUser`。

* call: 针对的是retrofit定义的接口，返回的是Observable<T>的情况，不对ApiResult进行拆包。对于上面定义的第二个接口`register`。

使用示例如下:

```
CustomRequest request = XHttp.custom();
request.apiCall(request.create(TestApi.UserService.class)
        .registerUser(HttpUtils.getJsonRequestBody(UserManager.getInstance().getRandomUser())))
        .subscribeWith(new TipRequestSubscriber<Boolean>() {
            @Override
            protected void onSuccess(Boolean aBoolean) {
                ToastUtils.toast("添加用户成功!");
            }
        });
```

```
CustomRequest request = XHttp.custom();
request.call(request.create(TestApi.UserService.class)
        .register(HttpUtils.getJsonRequestBody(UserManager.getInstance().getRandomUser())))
        .subscribeWith(new TipRequestSubscriber<ApiResult>() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                ToastUtils.toast("添加用户成功!");
                showResult(JsonUtil.toJson(apiResult));
            }
        });
```

### 缓存策略

目前框架提供了如下8种缓存策略:

* NO_CACHE: 不使用缓存(默认方式)

* DEFAULT: 完全按照HTTP协议的默认缓存规则，走OKhttp的Cache缓存

* FIRST_REMOTE: 先请求网络，请求网络失败后再加载缓存

* FIRST_CACHE: 先加载缓存，缓存没有再去请求网络

* ONLY_REMOTE: 仅加载网络，但数据依然会被缓存

* ONLY_CACHE: 只读取缓存

* CACHE_REMOTE: 先使用缓存，不管是否存在，仍然请求网络，会回调两次

* CACHE_REMOTE_DISTINCT: 先使用缓存，不管是否存在，仍然请求网络，会先把缓存回调给你，等网络请求回来发现数据是一样的就不会再返回，否则再返回（这样做的目的是防止数据是一样的你也需要刷新界面）

对于缓存的实现，提供了磁盘缓存`LruDiskCache`和内存缓存`LruMemoryCache`两种实现，默认使用的是磁盘缓存。

(1)可以先进行缓存的全局性配置，配置缓存的有效期、缓存大小，缓存路径、序列化器等。

```
XHttp.getInstance()
        .setIsDiskCache(true) //设置使用磁盘缓存
        .setCacheTime(60 * 1000) //设置全局缓存有效期为一分钟
        .setCacheVersion(1) //设置全局缓存的版本
        .setCacheDirectory(Utils.getDiskCacheDir(this, "XHttp")) //设置全局缓存保存的目录路径
        .setCacheMode(CacheMode.NO_CACHE) //设置全局的缓存策略
        .setCacheDiskConverter(new GsonDiskConverter())//默认缓存使用序列化转化
        .setCacheMaxSize(50 * 1024 * 1024);//设置缓存大小为50M
```

(2)在进行请求的时候，设置缓存模式和缓存的key即可。如下:

```
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

---

## 特别感谢

https://github.com/zhou-you/RxEasyHttp

## 如果觉得项目还不错，可以考虑打赏一波

> 你的打赏是我维护的动力，我将会列出所有打赏人员的清单在下方作为凭证，打赏前请留下打赏项目的备注！

![pay.png](https://raw.githubusercontent.com/xuexiangjys/Resource/master/img/pay/pay.png)

感谢下面小伙伴的打赏：

姓名 | 金额 | 方式
:-|:-|:-
*声 | 50￥ | 微信
**东 | 5￥ | 支付宝

## 联系方式

[![](https://img.shields.io/badge/点击一键加入QQ交流群-602082750-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=9922861ef85c19f1575aecea0e8680f60d9386080a97ed310c971ae074998887)

> 更多资讯内容，欢迎扫描关注我的个人微信公众号:【我的Android开源之旅】

![](https://s1.ax1x.com/2022/04/27/LbGMJH.jpg)

[demo-gif]: ./img/demo.gif
[download-svg]: https://img.shields.io/badge/downloads-2.61M-blue.svg
[download-url]: https://github.com/xuexiangjys/XHttp2/blob/master/apk/xhttp2_demo_1.0.apk?raw=true
[download-img]: ./img/download.png
