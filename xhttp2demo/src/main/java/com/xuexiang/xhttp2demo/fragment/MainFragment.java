package com.xuexiang.xhttp2demo.fragment;

import android.view.KeyEvent;
import android.view.View;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageContainerListFragment;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xutil.common.ClickUtils;

/**
 * ================================================
 * <p>
 * 应用的主Fragment, 应用界面的入口 <br>
 * Created by XAndroidTemplate <br>
 * <a href="mailto:xuexiangjys@gmail.com">Contact me</a>
 * <a href="https://github.com/xuexiangjys">Follow me</a>
 * </p>
 * ================================================
 */
@Page(name = "XHttp 使用演示")
public class MainFragment extends XPageContainerListFragment {
    /**
     * 获取页面的类集合[使用@Page注解进行注册的页面]
     *
     * @return
     */
    @Override
    protected Class[] getPagesClasses() {
        return new Class[]{
                SettingsFragment.class,
                UserFragment.class,
                BookFragment.class,
                TokenFragment.class,
                CustomRequestFragment.class,
                CacheFragment.class,
                TestApiFragment.class
        };
    }

    @Override
    protected TitleBar initTitleBar() {
        return super.initTitleBar().setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickUtils.exitBy2Click();
            }
        });
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click();
        }
        return true;
    }
}
