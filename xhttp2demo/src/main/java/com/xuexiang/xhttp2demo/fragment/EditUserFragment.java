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

import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.entity.User;
import com.xuexiang.xhttp2demo.utils.DialogUtils;
import com.xuexiang.xhttp2demo.utils.RouterUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.net.JsonUtil;
import com.xuexiang.xutil.tip.ToastUtils;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

/**
 * @author xuexiang
 * @since 2018/8/2 下午11:58
 */
@Page(name = "编辑用户信息")
public class EditUserFragment extends XPageFragment {

    private boolean mIsEditSuccess;

    @AutoWired
    User user;

    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.sp_gender)
    Spinner mSpGender;
    @BindView(R.id.et_age)
    EditText mEtAge;
    @BindView(R.id.et_phone)
    EditText mEtPhone;

    @Override
    protected void initArgs() {
        super.initArgs();
        RouterUtils.inject(this);
    }

    @Override
    protected TitleBar initTitleBar() {
        TitleBar titleBar = super.initTitleBar().setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEditSuccess) {
                    setFragmentResult(RESULT_OK, null);
                }
                popToBack();
            }
        });
        titleBar.addAction(new TitleBar.TextAction("保存") {
            @Override
            public void performAction(View view) {
                saveUser(view);
            }
        });
        titleBar.addAction(new TitleBar.TextAction("删除") {
            @Override
            public void performAction(View view) {
                deleteUser(view);
            }
        });
        return titleBar;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_user;
    }

    @Override
    protected void initViews() {
        mEtName.setText(user.getName());
        mSpGender.setSelection(user.getGender() - 1); //1 - 男， 2 - 女
        mEtAge.setText(String.valueOf(user.getAge()));
        mEtPhone.setText(user.getPhone());
    }

    @Override
    protected void initListeners() {

    }


    private boolean checkUser() {
        if (StringUtils.isEmpty(mEtName.getText().toString())) {
            ToastUtils.toast("姓名不能为空！");
            return false;
        } else if (StringUtils.isEmpty(mEtAge.getText().toString())) {
            ToastUtils.toast("年龄不能为空！");
            return false;
        } else if (StringUtils.isEmpty(mEtPhone.getText().toString())) {
            ToastUtils.toast("手机不能为空！");
            return false;
        } else {
            user.setName(mEtName.getText().toString());
            user.setGender(mSpGender.getSelectedItemPosition() + 1);
            user.setAge(StringUtils.toInt(mEtAge.getText().toString(), 0));
            user.setPhone(mEtPhone.getText().toString());
            return true;
        }
    }

    @SingleClick
    private void saveUser(View view) {
        if (user != null) {
            if (checkUser()) {
                onEditUser(user);
            }
        } else {
            ToastUtils.toast("数据错误！");
        }
    }

    @SingleClick
    private void deleteUser(View view) {
        DialogUtils.getConfirmDialog(getContext(), "提示", "是否确认删除该用户?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDeleteUser(user);
            }
        }).show();
    }


    private void onEditUser(User user) {
        XHttp.post("/user/updateUser")
                .upJson(JsonUtil.toJson(user))
                .execute(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        if (response) {
                            mIsEditSuccess = true;
                            ToastUtils.toast("编辑成功！");
                        } else {
                            ToastUtils.toast("编辑失败！");
                        }
                    }
                    @Override
                    public void onError(ApiException e) {
                        ToastUtils.toast("编辑失败：" + e.getMessage());
                    }

                });
    }

    /**
     * 这里直接使用post进行请求
     * @param item
     */
    private void onDeleteUser(User item) {
        XHttp.post("/user/deleteUser")
                .params("userId", item.getUserId())
                .execute(new SimpleCallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        ToastUtils.toast("删除成功！");
                        setFragmentResult(RESULT_OK, null);
                        popToBack();
                    }
                    @Override
                    public void onError(ApiException e) {
                        ToastUtils.toast("删除失败：" + e.getMessage());
                    }

                });
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsEditSuccess) {
                setFragmentResult(RESULT_OK, null);
            }
        }
        return false;
    }

}
