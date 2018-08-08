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
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xuexiang.rxutil2.lifecycle.RxLifecycle;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xaop.consts.PermissionConsts;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.DownloadProgressCallBack;
import com.xuexiang.xhttp2.callback.ProgressLoadingCallBack;
import com.xuexiang.xhttp2.callback.impl.IProgressResponseCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xhttp2.subsciber.ProgressDialogLoader;
import com.xuexiang.xhttp2.subsciber.ProgressLoadingSubscriber;
import com.xuexiang.xhttp2.subsciber.impl.IProgressLoader;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.adapter.BookAdapter;
import com.xuexiang.xhttp2demo.entity.Book;
import com.xuexiang.xhttp2demo.utils.HProgressDialogUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xutil.app.PathUtils;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.file.FileUtils;
import com.xuexiang.xutil.net.JsonUtil;
import com.xuexiang.xutil.tip.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * @author xuexiang
 * @since 2018/7/18 上午1:36
 */
@Page(name = "图书编辑")
public class EditBookFragment extends XPageFragment {
    private static final int REQUEST_CODE_SELECT_PICTURE = 2000;

    @AutoWired
    Book book;
    @BindView(R.id.et_book_name)
    EditText mEtBookName;
    @BindView(R.id.et_author)
    EditText mEtAuthor;
    @BindView(R.id.et_description)
    EditText mEtDescription;
    @BindView(R.id.et_price)
    EditText mEtPrice;
    @BindView(R.id.iv_picture)
    ImageView mIvPicture;

    String mPicturePath;

    private IProgressLoader mIProgressLoader;

    private boolean mIsEditSuccess;

    @Override
    protected void initArgs() {
        super.initArgs();
        XRouter.getInstance().inject(this);
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
                saveBook(view);
            }
        });
        return titleBar;
    }

    @SingleClick
    private void saveBook(View view) {
        if (checkBook()) {
            mIProgressLoader.updateMessage("保存中...");
            XHttp.post("/book/updateBook")
                    .upJson(JsonUtil.toJson(book))
                    .execute(new ProgressLoadingCallBack<Boolean>(mIProgressLoader) {
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
                            super.onError(e);
                            ToastUtils.toast("编辑失败：" + e.getMessage());
                        }
                    });
        }
    }

    private boolean checkBook() {
        if (StringUtils.isEmpty(mEtBookName.getText().toString())) {
            ToastUtils.toast("书名不能为空！");
            return false;
        } else if (StringUtils.isEmpty(mEtAuthor.getText().toString())) {
            ToastUtils.toast("作者不能为空！");
            return false;
        } else if (StringUtils.isEmpty(mEtDescription.getText().toString())) {
            ToastUtils.toast("简介不能为空！");
            return false;
        } else {
            book.setName(mEtBookName.getText().toString());
            book.setAuthor(mEtAuthor.getText().toString());
            book.setDescription(mEtDescription.getText().toString());
            book.setPrice(StringUtils.toFloat(mEtPrice.getText().toString(), 0));
            return true;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_book;
    }

    @Override
    protected void initViews() {
        mIProgressLoader = new ProgressDialogLoader(getContext());

        mEtBookName.setText(StringUtils.getString(book.getName()));
        mEtAuthor.setText(StringUtils.getString(book.getAuthor()));
        mEtDescription.setText(StringUtils.getString(book.getDescription()));
        mEtPrice.setText(String.valueOf(book.getPrice()));

        Glide.with(getContext())
                .load(BookAdapter.getBookImgUrl(book))
                .placeholder(R.drawable.img_default_book)
                .into(mIvPicture);
    }

    @Override
    protected void initListeners() {


    }

    @OnClick({R.id.iv_picture, R.id.btn_upload, R.id.btn_download})
    @SingleClick
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_picture:
                selectPicture();
                break;
            case R.id.btn_upload:
                uploadPicture();
                break;
            case R.id.btn_download:
                downloadBookPicture();
                break;
            default:
                break;
        }
    }

    @Permission(PermissionConsts.STORAGE)
    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_PICTURE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_PICTURE) {
            mPicturePath = PathUtils.getFilePathByUri(getContext(), data.getData());
            Glide.with(this)
                    .load(data.getData())
                    .placeholder(R.drawable.img_default_book)
                    .into(mIvPicture);
        }
    }

    @SuppressLint("CheckResult")
    private void uploadPicture() {
        if (StringUtils.isEmpty(mPicturePath)) {
            ToastUtils.toast("请先选择需要上传的图片!");
            selectPicture();
            return;
        }

        mIProgressLoader.updateMessage("上传中...");
        XHttp.post("/book/uploadBookPicture")
                .params("bookId", book.getBookId())
                .uploadFile("file", FileUtils.getFileByPath(mPicturePath), new IProgressResponseCallBack() {
                    @Override
                    public void onResponseProgress(long bytesWritten, long contentLength, boolean done) {

                    }
                }).execute(Boolean.class)
                .compose(RxLifecycle.with(this).<Boolean>bindToLifecycle())
                .subscribeWith(new ProgressLoadingSubscriber<Boolean>(mIProgressLoader) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        mIsEditSuccess = true;
                        ToastUtils.toast("图片上传" + (aBoolean ? "成功" : "失败") + "！");
                    }
                });
    }

    /**
     * 下载图片
     */
    private void downloadBookPicture() {
        if (StringUtils.isEmpty(book.getPicture())) {
            ToastUtils.toast("未上传图书封面！");
            return;
        }

        XHttp.downLoad(BookAdapter.getBookImgUrl(book))
                .savePath(PathUtils.getExtPicturesPath())
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void onStart() {
                        HProgressDialogUtils.showHorizontalProgressDialog(getContext(), "图片下载中...", true);
                    }

                    @Override
                    public void onError(ApiException e) {
                        ToastUtils.toast(e.getMessage());
                        HProgressDialogUtils.cancel();
                    }

                    @Override
                    public void update(long downLoadSize, long totalSize, boolean done) {
                        HProgressDialogUtils.setMax(totalSize);
                        HProgressDialogUtils.setProgress(downLoadSize);
                    }

                    @Override
                    public void onComplete(String path) {
                        ToastUtils.toast("图片下载成功, 保存路径:" + path);
                        HProgressDialogUtils.cancel();
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
