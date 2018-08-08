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

package com.xuexiang.xhttp2.cache.stategy;

import com.xuexiang.xhttp2.cache.RxCache;
import com.xuexiang.xhttp2.cache.model.CacheResult;

import java.lang.reflect.Type;
import java.util.Arrays;

import io.reactivex.Observable;

/**
 *  <p>描述：先请求网络，网络请求失败，再加载缓存</p>
 * <-------此类加载用的是反射 所以类名是灰色的 没有直接引用  不要误删----------------><br>
 *
 * @author xuexiang
 * @since 2018/6/20 下午7:45
 */
public final class FirstRemoteStrategy extends BaseStrategy {
    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, long time, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = loadCache(rxCache, type, key, time, true);
        Observable<CacheResult<T>> remote = loadRemote(rxCache, key, source, false);
        return Observable
                .concatDelayError(Arrays.asList(remote, cache))
                .take(1);
    }
}
