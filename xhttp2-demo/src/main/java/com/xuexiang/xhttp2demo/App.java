package com.xuexiang.xhttp2demo;

import android.app.Application;
import android.content.Context;

import com.xuexiang.xutil.XUtil;
import com.xuexiang.xpage.AppPageConfig;
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
    }

    private void initLibs() {
        XUtil.init(this);
        XUtil.debug(true);

        PageConfig.getInstance().setPageConfiguration(new PageConfiguration() {
            @Override
            public List<PageInfo> registerPages(Context context) {
                return AppPageConfig.getInstance().getPages();
            }
        }).debug("PageLog").enableWatcher(false).init(this);
    }
}
