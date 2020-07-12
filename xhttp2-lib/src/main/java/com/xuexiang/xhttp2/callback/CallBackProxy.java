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

package com.xuexiang.xhttp2.callback;

import androidx.annotation.NonNull;

import com.google.gson.internal.$Gson$Types;
import com.xuexiang.xhttp2.cache.model.CacheResult;
import com.xuexiang.xhttp2.callback.impl.IType;
import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.utils.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * <p>描述：提供回调代理</p>
 * 主要用于可以自定义ApiResult<br>
 *
 * @author xuexiang
 * @since 2018/6/21 上午12:47
 */
public class CallBackProxy<T extends ApiResult<R>, R> implements IType<T> {
    CallBack<R> mCallBack;

    public CallBackProxy(@NonNull CallBack<R> callBack) {
        mCallBack = callBack;
    }

    public CallBack getCallBack() {
        return mCallBack;
    }

    @Override
    public Type getType() {//CallBack代理方式，获取需要解析的Type
        Type typeArguments = ResponseBody.class;
        if (mCallBack != null) {
            Type rawType = mCallBack.getRawType();//如果用户的信息是返回List需单独处理
            if (List.class.isAssignableFrom(TypeUtils.getClass(rawType, 0)) || Map.class.isAssignableFrom(TypeUtils.getClass(rawType, 0))) {
                typeArguments = mCallBack.getType();
            } else if (CacheResult.class.isAssignableFrom(TypeUtils.getClass(rawType, 0))) {
                Type type = mCallBack.getType();
                typeArguments = TypeUtils.getParameterizedType(type, 0);
            } else {
                Type type = mCallBack.getType();
                typeArguments = TypeUtils.getClass(type, 0);
            }
        }
        Type rawType = TypeUtils.findNeedType(getClass());
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType) rawType).getRawType();
        }
        return $Gson$Types.newParameterizedTypeWithOwner(null, rawType, typeArguments);
    }

    @Override
    public Type getRawType() {
        return mCallBack.getRawType();
    }
}
