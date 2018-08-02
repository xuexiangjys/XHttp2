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
import com.xuexiang.xhttp2demo.api.ApiProvider;
import com.xuexiang.xhttp2demo.entity.User;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xutil.common.RandomUtils;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.resource.ResUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author xuexiang
 * @since 2018/7/16 下午4:52
 */
@Page(name = "接口1 -- 用户管理")
public class UserFragment extends XPageFragment implements SmartViewHolder.OnItemLongClickListener, SmartViewHolder.OnItemClickListener{
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_stateful)
    StatefulLayout mLlStateful;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private UserAdapter mUserAdapter;

//    private List<MaterialSimpleListItem> list;

    private IProgressLoader mIProgressLoader;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    protected void initArgs() {
        super.initArgs();
//        list = new ArrayList<>();
//        list.add(new MaterialSimpleListItem.Builder(getContext())
//                .content(R.string.lab_edit)
//                .icon(R.drawable.icon_edit)
//                .iconPaddingDp(8)
//                .build());
//        list.add(new MaterialSimpleListItem.Builder(getContext())
//                .content(R.string.lab_delete)
//                .icon(R.drawable.icon_delete)
//                .build());
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
        XHttpRequest req = ApiProvider.getAddUserReq(getRandomUser());
        XHttpSDK.executeToMain(req, new ProgressLoadingSubscriber<Boolean>(mIProgressLoader) {
            @Override
            public void onSuccess(Boolean aBoolean) {
                ToastUtils.toast("用户添加成功！");
                mRefreshLayout.autoRefresh();
            }
        });
    }


    private User getRandomUser() {
        User user = new User();
        user.setAge(StringUtils.toInt(RandomUtils.getRandomNumbers(2), 0));
        user.setGender((int) (Math.random() * 2 + 1));
        user.setPhone(RandomUtils.getRandomNumbers(11));
        user.setName(RandomUtils.getRandomLowerCaseLetters((int) (Math.random() * 8 + 8)));
        return user;
    }

    @Override
    public void onItemLongClick(View itemView, final int position) {
//        DialogUtils.getMenuDialog(getContext(), "菜单", list, new MaterialSimpleListAdapter.OnItemClickListener() {
//            @Override
//            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
//                if (item.getContent().equals(ResUtils.getString(R.string.lab_edit))) {
//                    onEditUser(mUserAdapter.getItem(position));
//                } else {
//                    onDeleteUser(mUserAdapter.getItem(position));
//                }
//            }
//        }).show();
    }

    @Override
    public void onItemClick(View itemView, final int position) {
//        DialogUtils.getSimpleConfirmDialog(getContext(), "是否确定选择用户【" + mUserAdapter.getItem(position).getName() + "】？", new MaterialDialog.SingleButtonCallback() {
//            @Override
//            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                UserManager.getInstance().selectUser(mUserAdapter.getItem(position));
//                ToastUtil.toast("已选择用户：" + mUserAdapter.getItem(position).getName());
//            }
//        }).show();
    }

    private void onEditUser(User user) {
//        new EditUserDialog(getContext(), new EditUserDialog.OnEditListener() {
//            @Override
//            public void onEditSuccess() {
//                mRefreshLayout.autoRefresh();
//            }
//        }).show(user);
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
                        mRefreshLayout.autoRefresh();
                    }
                    @Override
                    public void onError(ApiException e) {
                        ToastUtils.toast("删除失败：" + e.getMessage());
                    }

                });
    }

}
