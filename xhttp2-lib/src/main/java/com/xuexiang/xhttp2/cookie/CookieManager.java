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

package com.xuexiang.xhttp2.cookie;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * <p>描述：cookie管理器</p>
 *
 * @author xuexiang
 * @since 2018/6/20 下午11:56
 */
public class CookieManager implements CookieJar {

    private final PersistentCookieStore mCookieStore;

    private static CookieManager sInstance;

    private CookieManager(Context context) {
        mCookieStore = new PersistentCookieStore(context);
    }

    public static CookieManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CookieManager.class) {
                if (sInstance == null) {
                    sInstance = new CookieManager(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public void saveFromResponse(HttpUrl url, Cookie cookie) {
        if (cookie != null) {
            mCookieStore.add(url, cookie);
        }
    }

    public PersistentCookieStore getCookieStore() {
        return mCookieStore;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                mCookieStore.add(url, item);
            }
        }
    }


    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = mCookieStore.get(url);
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }

    public void addCookies(List<Cookie> cookies) {
        mCookieStore.addCookies(cookies);
    }

    public void remove(HttpUrl url, Cookie cookie) {
        mCookieStore.remove(url, cookie);
    }

    public void removeAll() {
        mCookieStore.removeAll();
    }

}
