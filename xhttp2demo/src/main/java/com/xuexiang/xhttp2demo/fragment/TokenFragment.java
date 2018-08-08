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

package com.xuexiang.xhttp2demo.fragment;

import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2demo.entity.User;
import com.xuexiang.xhttp2demo.http.callback.TipRequestCallBack;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageSimpleListFragment;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xutil.net.JsonUtil;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.List;

/**
 * @author xuexiang
 * @since 2018/8/7 上午12:01
 */
@Page(name = "高阶使用 -- 身份校验")
public class TokenFragment extends XPageSimpleListFragment {

    @Override
    protected List<String> initSimpleData(List<String> lists) {
        lists.add("跳转至登陆获取token");
        lists.add("验证token");
        lists.add("频繁进行网络请求的校验（30秒内请求不能超过3次）");
        lists.add("频繁进行网络请求的校验（10秒钟之内只能请求一次）");
        return lists;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case 0:
                XRouter.getInstance().build("/xhttp/login").navigation();
                break;
            case 1:
                XHttp.get("/authorization/getCurrentUser")
                        .accessToken(true)
                        .execute(new TipRequestCallBack<User>() {
                            @Override
                            public void onSuccess(User user) throws Throwable {
                                ToastUtils.toast("当前登录的用户:" + JsonUtil.toJson(user));
                            }
                        });
                break;
            case 2:
                XHttp.get("/authorization/testLimitedRequest")
                        .execute(new TipRequestCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) throws Throwable {
                                ToastUtils.toast("请求成功！");
                            }
                        });
                break;
            case 3:
                XHttp.get("/authorization/testQuickRequest")
                        .timeStamp(true)
                        .execute(new TipRequestCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) throws Throwable {
                                ToastUtils.toast("请求成功！");
                            }
                        });
                break;
            default:
                break;
        }
    }
}
