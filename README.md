# XHttp2

[![xh][xhsvg]][xh]  [![api][apisvg]][api]

一个功能强悍的网络请求库，使用RxJava2 + Retrofit2 + OKHttp组合进行封装。

## 关于我

[![github](https://img.shields.io/badge/GitHub-xuexiangjys-blue.svg)](https://github.com/xuexiangjys)   [![csdn](https://img.shields.io/badge/CSDN-xuexiangjys-green.svg)](http://blog.csdn.net/xuexiangjys)

## 特征

* 加入基础ApiService，减少Api冗余。
* 支持动态配置和自定义底层框架Okhttpclient、Retrofit.
* 支持多种方式访问网络GET、POST、PUT、DELETE等请求协议。
* 支持网络缓存,六种缓存策略可选,涵盖大多数业务场景。
* 支持固定添加header和动态添加header。
* 支持添加全局参数和动态添加局部参数。
* 支持文件下载、多文件上传和表单提交数据。
* 支持文件请求、上传、下载的进度回调、错误回调，也可以自定义回调。
* 支持默认、全局、局部三个层次的配置功能。
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
* api设计上结合http协议和android平台特点来实现,loading对话框,实时进度条显示。
* 返回结果和异常统一处理。
* 结合RxJava，线程切换灵活。
* 请求实体支持注解配置，配置网络请求接口的url、是否需要验证token以及请求参数的key。
* 拥有统一的网络请求取消机制。
* 支持模拟网络请求返回。


## 1、演示（请star支持）

### 1.1、Demo截图

### 1.2、Demo下载

### 1.3、api服务安装



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

--------------











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

## 联系方式

[![](https://img.shields.io/badge/点击一键加入QQ交流群-602082750-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=9922861ef85c19f1575aecea0e8680f60d9386080a97ed310c971ae074998887)

![](https://github.com/xuexiangjys/XPage/blob/master/img/qq_group.jpg)

[xhsvg]: https://img.shields.io/badge/XHttp2-v1.0.0-brightgreen.svg
[xh]: https://github.com/xuexiangjys/XHttp2
[apisvg]: https://img.shields.io/badge/API-14+-brightgreen.svg
[api]: https://android-arsenal.com/api?level=14