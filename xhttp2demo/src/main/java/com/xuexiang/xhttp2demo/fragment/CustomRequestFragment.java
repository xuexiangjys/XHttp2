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

import android.annotation.SuppressLint;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.xuexiang.rxutil2.rxjava.RxSchedulerUtils;
import com.xuexiang.xaop.annotation.MainThread;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.CallBackProxy;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.request.CustomRequest;
import com.xuexiang.xhttp2.utils.HttpUtils;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.entity.LoginInfo;
import com.xuexiang.xhttp2demo.entity.User;
import com.xuexiang.xhttp2demo.http.TestApi;
import com.xuexiang.xhttp2demo.http.callback.TipRequestCallBack;
import com.xuexiang.xhttp2demo.http.request.CustomApiResult;
import com.xuexiang.xhttp2demo.http.request.CustomGetRequest;
import com.xuexiang.xhttp2demo.http.subscriber.TipRequestSubscriber;
import com.xuexiang.xhttp2demo.manager.TokenManager;
import com.xuexiang.xhttp2demo.manager.UserManager;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xutil.net.JsonUtil;
import com.xuexiang.xutil.tip.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

/**
 * 自定义请求
 *
 * @author xuexiang
 * @since 2018/8/7 下午5:20
 */
@Page(name = "高阶使用 -- 自定义请求")
public class CustomRequestFragment extends XPageFragment {

    @BindView(R.id.tv_result_info)
    TextView tvResultInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_custom_request;
    }

    @Override
    protected void initViews() {
        tvResultInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    protected void initListeners() {

    }

    @SuppressLint("CheckResult")
    @OnClick({R.id.btn_custom_request, R.id.btn_retrofit_1, R.id.btn_retrofit_2, R.id.btn_retrofit_3, R.id.btn_retrofit_4, R.id.btn_retrofit_5})
    public void onViewClicked(View view) {
        CustomRequest request = XHttp.custom();
        switch (view.getId()) {
            case R.id.btn_custom_request:
                new CustomGetRequest("/test/testCustomResult")
                        .execute(new TipRequestCallBack<Boolean>() {
                            @Override
                            public void onSuccess(Boolean response) throws Throwable {
                                ToastUtils.toast("请求成功：" + response);
                            }
                        });

//                request.apiCall(request.create(TestApi.UserService.class)
//                        .testCustomResult())
//                        .subscribeWith(new TipRequestSubscriber<Boolean>() {
//                            @Override
//                            protected void onSuccess(Boolean response) {
//                                ToastUtils.toast("请求成功：" + response);
//                            }
//                        });


//                XHttp.get("/test/testCustomResult")
//                        .execute(new CallBackProxy<CustomApiResult<Boolean>, Boolean>(new TipRequestCallBack<Boolean>() {
//                            @Override
//                            public void onSuccess(Boolean response) throws Throwable {
//                                ToastUtils.toast("请求成功：" + response);
//                            }
//                        }){});

                break;
            case R.id.btn_retrofit_1:
                if (TokenManager.getInstance().isUserLogined()) {
                    //使用retrofit自身定义的接口进行请求
                    XHttp.custom(TestApi.LoginService.class)
                            .login(TokenManager.getInstance().getLoginUser())
                            .compose(RxSchedulerUtils.<ApiResult<LoginInfo>>_io_main_o())
                            .subscribeWith(new TipRequestSubscriber<ApiResult<LoginInfo>>() {
                                @Override
                                protected void onSuccess(ApiResult<LoginInfo> loginInfoApiResult) {
                                    ToastUtils.toast("请求成功!");
                                    showResult(JsonUtil.toJson(loginInfoApiResult));
                                }
                            });
                } else {
                    ToastUtils.toast("请先进行登录！");
                    XRouter.getInstance().build("/xhttp/login").navigation();
                }
                break;
            case R.id.btn_retrofit_2:
                //apiCall进行拆包
                request.apiCall(request.create(TestApi.UserService.class)
                        .registerUser(HttpUtils.getJsonRequestBody(UserManager.getInstance().getRandomUser())))
                        .subscribeWith(new TipRequestSubscriber<Boolean>() {
                            @Override
                            protected void onSuccess(Boolean aBoolean) {
                                ToastUtils.toast("添加用户成功!");
                            }
                        });
                break;
            case R.id.btn_retrofit_3:
                //call不进行拆包
                request.call(request.create(TestApi.UserService.class)
                        .register(UserManager.getInstance().getRandomUser()))
                        .subscribeWith(new TipRequestSubscriber<ApiResult>() {
                            @Override
                            protected void onSuccess(ApiResult apiResult) {
                                ToastUtils.toast("添加用户成功!");
                                showResult(JsonUtil.toJson(apiResult));
                            }
                        });
                break;
            case R.id.btn_retrofit_4:
                //apiCall进行拆包
                request.apiCall(request.create(TestApi.UserService.class)
                        .registerUser(HttpUtils.getJsonRequestBody(UserManager.getInstance().getRandomUser())), new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) throws Throwable {
                        ToastUtils.toast("添加用户成功!");
                    }

                    @Override
                    public void onError(ApiException e) {
                        ToastUtils.toast(e.getDisplayMessage());
                    }
                });
                break;
            case R.id.btn_retrofit_5:
                //call不进行拆包
                request.call(request.create(TestApi.UserService.class)
                        .register(UserManager.getInstance().getRandomUser()), new TipRequestCallBack<ApiResult>() {
                    @Override
                    public void onSuccess(ApiResult response) throws Throwable {
                        ToastUtils.toast("添加用户成功!");
                        showResult(JsonUtil.toJson(response));
                    }
                });
                break;
            default:
                break;
        }
    }

    @MainThread
    private void showResult(String result) {
        tvResultInfo.setText("请求结果:\n\r" + result);
    }
}
