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
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gturedi.views.StatefulLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.XHttpProxy;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.utils.TypeUtils;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.adapter.BookAdapter;
import com.xuexiang.xhttp2demo.entity.Book;
import com.xuexiang.xhttp2demo.http.TestApi;
import com.xuexiang.xhttp2demo.http.subscriber.TipRequestSubscriber;
import com.xuexiang.xhttp2demo.manager.UserManager;
import com.xuexiang.xhttp2demo.utils.DialogUtils;
import com.xuexiang.xhttp2demo.utils.RouterUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;

import static android.app.Activity.RESULT_OK;

/**
 * @author xuexiang
 * @since 2018/7/17 下午3:12
 */
@Page(name = "接口演示2 -- 图书管理")
public class BookFragment extends XPageFragment implements SmartViewHolder.OnItemLongClickListener, SmartViewHolder.OnViewItemClickListener, SmartViewHolder.OnItemClickListener {
    private static final int REQUEST_CODE_EDIT_BOOK = 1000;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_stateful)
    StatefulLayout mLlStateful;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private BookAdapter mBookAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_book;
    }

    @Override
    protected void initViews() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mBookAdapter = new BookAdapter());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void initListeners() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final @NonNull RefreshLayout refreshLayout) {
                getBookList(refreshLayout);
            }
        });

        mBookAdapter.setOnItemClickListener(this);
        mBookAdapter.setOnItemLongClickListener(this);
        mBookAdapter.setItemViewOnClickListener(this);
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onItemLongClick(View itemView, int position) {

    }

    @Override
    public void onItemClick(View itemView, int position) {
        openPageForResult(EditBookFragment.class, RouterUtils.getBundle("book", mBookAdapter.getItem(position)), REQUEST_CODE_EDIT_BOOK);
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_EDIT_BOOK) {
            mRefreshLayout.autoRefresh();
        }
    }

    @Override
    public void onViewItemClick(View view, final int position) {
        switch (view.getId()) {
            case R.id.sb_buy:
                if (UserManager.getInstance().getUser() != null) {
                    DialogUtils.getConfirmDialog(getContext(), "提示", "是否确定让【" + UserManager.getInstance().getSelectUserName() + "】购买【" + mBookAdapter.getItem(position).getName() + "】？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            buyBook(position);
                        }
                    }).show();
                } else {
                    ToastUtils.toast("请先选择用户！");
                    openPage(UserFragment.class);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取用户信息
     *
     * @param refreshLayout
     */
    @SuppressLint("CheckResult")
    private void getBookList(@NonNull final RefreshLayout refreshLayout) {
        Observable<List<Book>> observable = XHttp.get("/book/getAllBook")
                .syncRequest(false)
                .onMainThread(true)
                .execute(TypeUtils.getListType(Book.class));

        observable.subscribeWith(new TipRequestSubscriber<List<Book>>() {
            @Override
            protected void onSuccess(List<Book> response) {
                refreshLayout.finishRefresh(true);
                if (response != null && response.size() > 0) {
                    mBookAdapter.refresh(response);
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
//                .execute(new SimpleCallBack<List<Book>>() {
//                    @Override
//                    public void onSuccess(List<Book> response) {
//                        refreshLayout.finishRefresh(true);
//                        if (response != null && response.size() > 0) {
//                            mBookAdapter.refresh(response);
//                            mLlStateful.showContent();
//                        } else {
//                            mLlStateful.showEmpty();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ApiException e) {
//                        refreshLayout.finishRefresh(false);
//                        mLlStateful.showError(e.getMessage(), null);
//                    }
//
//                });
    }

    @SuppressLint("CheckResult")
    private void buyBook(final int position) {
        //使用XHttpProxy进行接口代理请求
        XHttpProxy.proxy(TestApi.IOrder.class)
                .buyBook(mBookAdapter.getItem(position).getBookId(), UserManager.getInstance().getUser().getUserId(), 1)
                .subscribeWith(new TipRequestSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        ToastUtils.toast("图书购买" + (aBoolean ? "成功" : "失败") + "！");
                        mRefreshLayout.autoRefresh();
                    }
                });
    }


}
