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

package com.xuexiang.xhttp2demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.xuexiang.xhttp2.XHttpProxy;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.subsciber.ProgressDialogLoader;
import com.xuexiang.xhttp2.subsciber.ProgressLoadingSubscriber;
import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.entity.LoginInfo;
import com.xuexiang.xhttp2demo.http.TestApi;
import com.xuexiang.xhttp2demo.manager.TokenManager;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xpage.utils.TitleUtils;
import com.xuexiang.xrouter.annotation.Router;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.system.KeyboardUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author xuexiang
 * @since 2018/8/7 上午12:50
 */
@Router(path = "/xhttp/login")
public class LoginActivity extends Activity {

    @BindView(R.id.et_login_name)
    EditText mEtLoginName;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    private IProgressLoader mIProgressLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        TitleUtils.initTitleBarStyle(mTitleBar, "登录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIProgressLoader = new ProgressDialogLoader(this, "登录中...");
    }

    @OnClick({R.id.btn_login, R.id.btn_reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                String loginName = mEtLoginName.getText().toString().trim();
                String password = mEtPassword.getText().toString().trim();
                if (StringUtils.isEmpty(loginName)) {
                    ToastUtils.toast("用户名不能为空！");
                    return;
                } else if (StringUtils.isEmpty(password)) {
                    ToastUtils.toast("密码不能为空！");
                    return;
                }

                XHttpProxy.proxy(TestApi.IAuthorization.class)
                        .login(loginName, password)
                        .subscribeWith(new ProgressLoadingSubscriber<LoginInfo>(mIProgressLoader) {
                            @Override
                            protected void onSuccess(LoginInfo loginInfo) {
                                ToastUtils.toast("登录成功！");
                                TokenManager.getInstance()
                                        .setToken(loginInfo.getToken())
                                        .setLoginUser(loginInfo.getUser());
                                finish(); //结束界面
                            }

                            @Override
                            public void onError(ApiException e) {
                                super.onError(e);
                                ToastUtils.toast(e.getDisplayMessage());
                            }
                        });
                break;
            case R.id.btn_reset:
                mEtLoginName.setText("");
                mEtPassword.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        KeyboardUtils.onClickBlankArea2HideSoftInput(ev, this);
        return super.dispatchTouchEvent(ev);
    }
}
