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

import android.view.View;
import android.widget.EditText;

import com.xuexiang.xhttp2.XHttpSDK;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.utils.SettingSPUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.net.NetworkUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author xuexiang
 * @since 2018/7/16 下午4:04
 */
@Page(name = "设置")
public class SettingsFragment extends XPageFragment {

    @BindView(R.id.et_api_url)
    EditText mEtApiUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initViews() {
        mEtApiUrl.setText(SettingSPUtils.getInstance().getApiURL());
    }

    @Override
    protected void initListeners() {

    }

    @OnClick(R.id.btn_save)
    public void onViewClicked(View view) {
        switch(view.getId()) {
            case R.id.btn_save:
                String url = mEtApiUrl.getText().toString().trim();
                if (NetworkUtils.isUrlValid(url) && XHttpSDK.verifyBaseUrl(url)) {
                    XHttpSDK.setBaseUrl(url);
                    SettingSPUtils.getInstance().setApiURL(url);
                    ToastUtils.toast("地址保存成功！");
                } else {
                    ToastUtils.toast("输入的地址不合法！");
                }
                break;
            default:
                break;
        }
    }
}
