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

package com.xuexiang.xhttp2demo.manager;

import com.xuexiang.xhttp2demo.entity.User;

/**
 * @author xuexiang
 * @since 2018/7/17 下午2:39
 */
public class UserManager {


    private static UserManager sInstance;


    private User mUser;

    private UserManager() {

    }

    public static UserManager getInstance() {
        if (sInstance == null) {
            synchronized (UserManager.class) {
                if (sInstance == null) {
                    sInstance = new UserManager();
                }
            }
        }
        return sInstance;
    }

    public void selectUser(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }

    public String getSelectUserName() {
        if (mUser != null) {
            return mUser.getName();
        } else {
            return "未选择用户！";
        }
    }

    public void clear() {
        mUser = null;
    }
}
