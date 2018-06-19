package com.xuexiang.xhttp2;

import android.app.Application;
import android.content.Context;

/**
 * @author xuexiang
 * @since 2018/6/13 上午12:44
 */
public final class XHttp {
    private volatile static XHttp gInstance = null;

    private static Application sContext;

    public static XHttp getInstance() {
        testInitialize();
        if (gInstance == null) {
            synchronized (XHttp.class) {
                if (gInstance == null) {
                    gInstance = new XHttp();
                }
            }
        }
        return gInstance;
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
}
