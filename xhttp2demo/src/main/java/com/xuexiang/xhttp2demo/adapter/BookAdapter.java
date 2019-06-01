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

package com.xuexiang.xhttp2demo.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.entity.Book;
import com.xuexiang.xutil.common.StringUtils;

/**
 * @author xuexiang
 * @since 2018/7/17 下午5:25
 */
public class BookAdapter extends SmartRecyclerAdapter<Book> {

    private SmartViewHolder.OnViewItemClickListener mOnViewItemClickListener;

    public BookAdapter() {
        super(R.layout.adapter_list_book_item);
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Book model, int position) {
        if (model != null) {
            ImageView picture = holder.findViewById(R.id.iv_picture);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.img_default_book);
            Glide.with(picture.getContext())
                    .load(getBookImgUrl(model))
                    .apply(options)
                    .into(picture);

            holder.text(R.id.tv_book_name, StringUtils.getString(model.getName()));
            holder.text(R.id.tv_author, StringUtils.getString(model.getAuthor()));
            holder.text(R.id.tv_description, StringUtils.getString(model.getDescription()));
            holder.text(R.id.tv_price, "¥" + model.getPrice());
            holder.text(R.id.tv_sales_volume, "销量：" + model.getSalesVolume());
            holder.text(R.id.tv_score, "评分：" + model.getScore() + "星");
            holder.text(R.id.tv_mark, String.valueOf(model.getMark()));

            if (mOnViewItemClickListener != null) {
                holder.viewClick(R.id.sb_buy, mOnViewItemClickListener, position);
            }

        }
    }

    public BookAdapter setItemViewOnClickListener(SmartViewHolder.OnViewItemClickListener onViewItemClickListener) {
        mOnViewItemClickListener = onViewItemClickListener;
        return this;
    }


    public static String getBaseImgUrl() {
        String baseUrl = XHttp.getBaseUrl().trim();
        if (baseUrl.endsWith("/")) {
            return baseUrl + "file/downloadFile/";
        } else {
            return baseUrl + "/file/downloadFile/";
        }

    }

    public static String getBookImgUrl(Book book) {
        return getBaseImgUrl() + book.getPicture();
    }

    public static String getBookImgUrlWithoutBaseUrl(Book book) {
        return "/file/downloadFile/" + book.getPicture();
    }
}
