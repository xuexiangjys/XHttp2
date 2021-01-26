/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

package com.xuexiang.xhttp2.cache.key;

import androidx.annotation.NonNull;

import com.xuexiang.xhttp2.annotation.NetMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * <p>key规则 ： url-方法名(参数1名=参数1值|参数2名=参数2值|...)</p>
 *
 * @author xuexiang
 * @since 2019/4/7 下午3:58
 */
public class DefaultCacheKeyCreator implements ICacheKeyCreator {

    @Override
    public String getCacheKey(@NonNull Method method, @NonNull Object[] args, NetMethod apiMethod) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(apiMethod.url()).append("-").append(method.getName()).append('(');
        Type[] parameters = method.getGenericParameterTypes();
        if (apiMethod.cacheKeyIndex() == NetMethod.ALL_PARAMS_INDEX) {
            for (int i = 0; i < parameters.length; i++) {
                if (i > 0) {
                    keyBuilder.append("|");
                }
                keyBuilder.append(apiMethod.parameterNames()[i]).append('=');
                keyBuilder.append(Strings.toString(args[i]));
            }
        } else {
            int index = apiMethod.cacheKeyIndex();
            if (index >= 0 && index < parameters.length) {
                keyBuilder.append(apiMethod.parameterNames()[index]).append('=');
                keyBuilder.append(Strings.toString(args[index]));
            }
        }
        keyBuilder.append(')');
        return keyBuilder.toString();
    }
}
