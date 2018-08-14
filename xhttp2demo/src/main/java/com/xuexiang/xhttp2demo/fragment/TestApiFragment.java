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

import com.xuexiang.xaop.annotation.MainThread;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.XHttpProxy;
import com.xuexiang.xhttp2.XHttpSDK;
import com.xuexiang.xhttp2.subsciber.ProgressDialogLoader;
import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.entity.Book;
import com.xuexiang.xhttp2demo.entity.User;
import com.xuexiang.xhttp2demo.http.ApiProvider;
import com.xuexiang.xhttp2demo.http.TestApi;
import com.xuexiang.xhttp2demo.http.callback.TipRequestCallBack;
import com.xuexiang.xhttp2demo.http.subscriber.TipRequestSubscriber;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.net.JsonUtil;
import com.xuexiang.xutil.tip.ToastUtils;

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

    }

    @OnClick({R.id.btn_test_list_1, R.id.btn_test_list_2, R.id.btn_test_keep_json})
    public void onViewClicked(View view) {
        clearLog();
        switch (view.getId()) {
            case R.id.btn_test_list_1:
                XHttpProxy.proxy(TestApi.IBook.class, false)
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
                XHttp.get("/book/getAllBook")
                        .keepJson(true)
                        .execute(new TipRequestCallBack<String>() {
                            @Override
                            public void onSuccess(String response) throws Throwable {
                                ToastUtils.toast("查询成功！");
                                showResult(response);
                            }
                        });

                break;
        }
    }

    @MainThread
    private void showResult(String result) {
        mTvResultInfo.setText("请求结果:\n\r" + result);
    }

    private void clearLog() {
        mTvResultInfo.setText("");
    }
}
