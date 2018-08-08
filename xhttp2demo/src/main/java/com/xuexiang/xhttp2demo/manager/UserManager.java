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
import com.xuexiang.xutil.common.RandomUtils;
import com.xuexiang.xutil.common.StringUtils;

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

    public User getRandomUser() {
        User user = new User();
        user.setAge(StringUtils.toInt(RandomUtils.getRandomNumbers(2), 0));
        user.setGender((int) (Math.random() * 2 + 1));
        user.setPhone(RandomUtils.getRandomNumbers(11));
        user.setName(RandomUtils.getRandomLowerCaseLetters((int) (Math.random() * 8 + 8)));
        user.setLoginName(user.getName());
        user.setPassword(RandomUtils.getRandomLowerCaseLetters((int) (Math.random() * 10 + 8)));
        return user;
    }
}
