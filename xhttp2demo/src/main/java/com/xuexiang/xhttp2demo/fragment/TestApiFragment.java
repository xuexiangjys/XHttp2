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

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.xuexiang.xaop.annotation.MainThread;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.XHttpProxy;
import com.xuexiang.xhttp2.XHttpSDK;
import com.xuexiang.xhttp2.subsciber.ProgressDialogLoader;
import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.entity.Book;
import com.xuexiang.xhttp2demo.entity.PageQuery;
import com.xuexiang.xhttp2demo.entity.QueryResult;
import com.xuexiang.xhttp2demo.entity.User;
import com.xuexiang.xhttp2demo.http.ApiProvider;
import com.xuexiang.xhttp2demo.http.TestApi;
import com.xuexiang.xhttp2demo.http.callback.TipRequestCallBack;
import com.xuexiang.xhttp2demo.http.request.CustomGetRequest;
import com.xuexiang.xhttp2demo.http.subscriber.TipRequestSubscriber;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.common.CollectionUtils;
import com.xuexiang.xutil.common.RandomUtils;
import com.xuexiang.xutil.net.JsonUtil;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author xuexiang
 * @since 2018/8/14 下午11:33
 */
@Page(name = "接口测试")
public class TestApiFragment extends XPageFragment {

    @BindView(R.id.tv_result_info)
    TextView mTvResultInfo;
    @BindView(R.id.switch_strict_mode)
    SwitchCompat switchStrictMode;

    private IProgressLoader mIProgressLoader;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test_api;
    }

    @Override
    protected void initViews() {
        mTvResultInfo.setMovementMethod(ScrollingMovementMethod.getInstance());

        mIProgressLoader = new ProgressDialogLoader(getContext(), "正在加载中...");
    }

    @Override
    protected void initListeners() {
        switchStrictMode.setChecked(XHttp.getInstance().isInStrictMode());
        switchStrictMode.setOnCheckedChangeListener((buttonView, isChecked) -> XHttp.getInstance().setStrictMode(isChecked));

    }

    @OnClick({R.id.btn_test_list_1, R.id.btn_test_list_2, R.id.btn_test_keep_json, R.id.btn_test_json_object, R.id.btn_test_json_object_array, R.id.btn_test_result, R.id.btn_test_data_null})
    public void onViewClicked(View view) {
        clearLog();
        switch (view.getId()) {
            case R.id.btn_test_list_1:
                XHttpProxy.proxy(TestApi.IBook.class)
                        .getBooks(1, 4)
                        .subscribeWith(new TipRequestSubscriber<List<Book>>() {
                            @Override
                            protected void onSuccess(List<Book> books) {
                                ToastUtils.toast("查询成功！");
                                showResult(JsonUtil.toJson(books));
                            }
                        });
                break;
            case R.id.btn_test_list_2:
                XHttpSDK.executeToMain(ApiProvider.getUsersReq(1, 4), new TipRequestSubscriber<List<User>>() {
                    @Override
                    protected void onSuccess(List<User> users) {
                        ToastUtils.toast("查询成功！");
                        showResult(JsonUtil.toJson(users));
                    }
                });
                break;
            case R.id.btn_test_keep_json:
//                XHttp.get("/book/getAllBook")
//                        .keepJson(true)
//                        .execute(new TipRequestCallBack<String>() {
//                            @Override
//                            public void onSuccess(String response) throws Throwable {
//                                ToastUtils.toast("查询成功！");
//                                showResult(response);
//                            }
//                        });
                XHttpProxy.proxy(TestApi.IBook.class)
                        .getAllBooks()
                        .subscribeWith(new TipRequestSubscriber<List<Book>>() {
                            @Override
                            protected void onSuccess(List<Book> books) {
                                ToastUtils.toast("查询成功！");
                                showResult(JsonUtil.toJson(books));
                            }
                        });
                break;
            case R.id.btn_test_json_object:
                XHttpProxy.proxy(TestApi.ITestService.class)
                        .testJsonObject(getTestUser())
                        .subscribeWith(new TipRequestSubscriber<User>() {
                            @Override
                            protected void onSuccess(User user) {
                                ToastUtils.toast("查询成功！");
                                showResult(JsonUtil.toJson(user));
                            }
                        });
                break;
            case R.id.btn_test_json_object_array:
                XHttpProxy.proxy(TestApi.ITestService.class)
                        .testJsonObjectArray(getTestUserList())
                        .subscribeWith(new TipRequestSubscriber<List<User>>() {
                            @Override
                            protected void onSuccess(List<User> users) {
                                ToastUtils.toast("查询成功！");
                                showResult(JsonUtil.toJson(users));
                            }
                        });
                break;
            case R.id.btn_test_result:
                XHttpProxy.proxy(TestApi.ITestService.class)
                        .findBooksByQueryParam(new PageQuery(1, 5))
                        .subscribeWith(new TipRequestSubscriber<QueryResult<Book>>() {
                            @Override
                            protected void onSuccess(QueryResult<Book> response) {
                                ToastUtils.toast("请求成功, 请求数量：" + CollectionUtils.getSize(response.result));
                            }
                        });
                break;
            case R.id.btn_test_data_null:
                XHttp.get("/test/testDataNull")
                        .execute(new TipRequestCallBack<Void>() {
                            @Override
                            public void onSuccess(Void response) throws Throwable {
                                ToastUtils.toast("请求成功:" + response);
                            }
                        });
//                XHttpProxy.proxy(TestApi.ITestService.class)
//                        .testDataNull()
//                        .subscribeWith(new TipRequestSubscriber<Void>() {
//                            @Override
//                            protected void onSuccess(Void response) {
//                                ToastUtils.toast("请求成功:" + response);
//                            }
//                        });
//                new CustomGetRequest("/test/testCustomApiDataNull")
//                        .execute(new TipRequestCallBack<Void>() {
//                            @Override
//                            public void onSuccess(Void response) throws Throwable {
//                                ToastUtils.toast("请求成功:" + response);
//                            }
//                        });
                break;
            default:
                break;
        }
    }

    private User getTestUser() {
        User user = new User();
        user.setLoginName("xuexiang");
        user.setAge(27);
        user.setPassword("123456");
        return user;
    }


    private List<User> getTestUserList() {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setLoginName("xuexiang" + (i + 1));
            user.setAge(RandomUtils.getRandom(100));
            user.setPassword("123456" + i);
            list.add(user);
        }
        return list;
    }

    @MainThread
    private void showResult(String result) {
        mTvResultInfo.setText("请求结果:\n\r" + result);
    }

    private void clearLog() {
        mTvResultInfo.setText("");
    }

}
