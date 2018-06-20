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

package com.xuexiang.xhttp2.subsciber.impl;

/**
 * 进度条加载者实现接口
 *
 * @author xuexiang
 * @since 2018/6/20 上午9:19
 */
public interface IProgressLoader {
    /**
     * @return 当前网络请求是否在加载
     */
    boolean isLoading();

    /**
     * 设置loading提示信息
     *
     * @param msg
     */
    void updateMessage(String msg);

    /**
     * 显示加载界面
     */
    void showLoading();

    /**
     * 隐藏加载界面
     */
    void dismissLoading();

    /**
     * 设置是否可取消
     *
     * @param flag
     */
    void setCancelable(boolean flag);

    /**
     * 设置取消的回掉监听
     *
     * @param listener
     */
    void setOnProgressCancelListener(OnProgressCancelListener listener);
}
