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

import com.scwang.smartrefresh.layout.adapter.BaseRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.xuexiang.xhttp2demo.R;
import com.xuexiang.xhttp2demo.entity.User;
import com.xuexiang.xutil.common.StringUtils;

/**
 * @author xuexiang
 * @since 2018/7/16 下午4:55
 */
public class UserAdapter extends BaseRecyclerAdapter<User> {

    public UserAdapter() {
        super(R.layout.adapter_list_user_item);
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, User model, int position) {
        if (model != null) {
            holder.text(R.id.tv_user_id, "用户ID：" + model.getUserId());
            holder.text(R.id.tv_user_name, "姓名：" + StringUtils.getString(model.getName()));
            holder.text(R.id.tv_gender, "性别：" + (model.getGender().equals(1) ? "男" : "女"));
            holder.text(R.id.tv_age, "年龄：" + model.getAge() + "岁");
            holder.text(R.id.tv_phone, "手机：" + StringUtils.getString(model.getPhone()));
        }
    }
}
