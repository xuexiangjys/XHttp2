package com.xuexiang.xhttp2demo;

import android.app.Application;
import android.content.Context;

import com.xuexiang.xaop.XAOP;
import com.xuexiang.xhttp2.XHttpSDK;
import com.xuexiang.xhttp2demo.utils.SettingSPUtils;
import com.xuexiang.xpage.Xhttp2demoPageConfig;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.PageConfiguration;
import com.xuexiang.xpage.model.PageInfo;

import java.util.List;

/**
 * ================================================
 * <p>
 * 应用的入口，配置基础library的依赖引用配置 <br>
 * Created by XAndroidTemplate <br>
 * <a href="mailto:xuexiangjys@gmail.com">Contact me</a>
 * <a href="https://github.com/xuexiangjys">Follow me</a>
 * </p>
 * ================================================
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLibs();

        initHttp();
    }

    private void initLibs() {
        XUtil.init(this);
        XUtil.debug(true);
        XAOP.init(this);

        PageConfig.getInstance().setPageConfiguration(new PageConfiguration() {
            @Override
            public List<PageInfo> registerPages(Context context) {
                return Xhttp2demoPageConfig.getInstance().getPages();
            }
        }).debug("PageLog").enableWatcher(false).init(this);

        initXRouter();
    }

    private void initHttp() {
        XHttpSDK.init(this);   //初始化网络请求框架，必须首先执行
        XHttpSDK.debug();  //需要调试的时候执行
        XHttpSDK.setBaseUrl(SettingSPUtils.getInstance().getApiURL());  //设置网络请求的基础地址
    }

    private void initXRouter() {
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            XRouter.openLog();     // 打印日志
            XRouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        XRouter.init(this);
    }
}
