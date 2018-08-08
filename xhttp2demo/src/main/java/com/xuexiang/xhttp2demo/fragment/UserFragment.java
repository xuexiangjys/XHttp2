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
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gturedi.views.StatefulLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.XHttpSDK;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.model.XHttpRequest;
import com.xuexiang.xhttp2.subsciber.ProgressDialogLoader;
import com.xuexiang.xhttp2.subsciber.ProgressLoadingSubscriber;
import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.adapter.UserAdapter;
import com.xuexiang.xhttp2demo.http.ApiProvider;
import com.xuexiang.xhttp2demo.entity.User;
import com.xuexiang.xhttp2demo.manager.UserManager;
import com.xuexiang.xhttp2demo.utils.DialogUtils;
import com.xuexiang.xhttp2demo.utils.RouterUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xutil.common.RandomUtils;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

/**
 * @author xuexiang
 * @since 2018/7/16 下午4:52
 */
@Page(name = "接口演示1 -- 用户管理")
public class UserFragment extends XPageFragment implements SmartViewHolder.OnItemLongClickListener, SmartViewHolder.OnItemClickListener{
    private static final int REQUEST_CODE_EDIT_USER = 1000;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_stateful)
    StatefulLayout mLlStateful;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private UserAdapter mUserAdapter;

    private IProgressLoader mIProgressLoader;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    protected TitleBar initTitleBar() {
        TitleBar titleBar = super.initTitleBar();
        titleBar.addAction(new TitleBar.TextAction("随机增加用户") {
            @Override
            public void performAction(View view) {
                addUser(view);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        mIProgressLoader = new ProgressDialogLoader(getContext());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mUserAdapter = new UserAdapter());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ToastUtils.toast("长按选择用户！");
    }

    @Override
    protected void initListeners() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final @NonNull RefreshLayout refreshLayout) {
                getUserList(refreshLayout);
            }
        });

        mUserAdapter.setOnItemLongClickListener(this);
        mUserAdapter.setOnItemClickListener(this);
        mRefreshLayout.autoRefresh();
    }

    /**
     * 获取用户信息
     *
     * @param refreshLayout
     */
    @SuppressLint("CheckResult")
    private void getUserList(@NonNull final RefreshLayout refreshLayout) {
        XHttp.get("/user/getAllUser")
                .syncRequest(false)
                .onMainThread(true)
                .execute(new SimpleCallBack<List<User>>() {
                    @Override
                    public void onSuccess(List<User> response) {
                        refreshLayout.finishRefresh(true);
                        if (response != null && response.size() > 0) {
                            mUserAdapter.refresh(response);
                            mLlStateful.showContent();
                        } else {
                            mLlStateful.showEmpty();
                        }
                    }
                    @Override
                    public void onError(ApiException e) {
                        refreshLayout.finishRefresh(false);
                        mLlStateful.showError(e.getMessage(), null);
                    }

                });
    }

    /**
     * 这里使用RxNetReq进行网络请求
     * @param view
     */
    @SingleClick
    private void addUser(View view) {
        XHttpRequest req = ApiProvider.getAddUserReq(UserManager.getInstance().getRandomUser());
        XHttpSDK.executeToMain(req, new ProgressLoadingSubscriber<Boolean>(mIProgressLoader) {
            @Override
            public void onSuccess(Boolean aBoolean) {
                ToastUtils.toast("用户添加成功！");
                mRefreshLayout.autoRefresh();
            }
        });
    }




    @Override
    public void onItemLongClick(View itemView, final int position) {
        DialogUtils.getConfirmDialog(getContext(), "选择用户", "是否确定选择用户【" + mUserAdapter.getItem(position).getName() + "】？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserManager.getInstance().selectUser(mUserAdapter.getItem(position));
                ToastUtils.toast("已选择用户：" + mUserAdapter.getItem(position).getName());
            }
        }).show();

    }

    @SingleClick
    @Override
    public void onItemClick(View itemView, final int position) {
        openPageForResult(EditUserFragment.class, RouterUtils.getBundle("user", mUserAdapter.getItem(position)), REQUEST_CODE_EDIT_USER);
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_EDIT_USER) {
            mRefreshLayout.autoRefresh();
        }
    }
}
