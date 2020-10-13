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

package com.xuexiang.xhttp2.utils;

import com.xuexiang.xhttp2.model.ApiResult;
import com.xuexiang.xhttp2.reflect.TypeBuilder;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * 类型工具类
 *
 * @author xuexiang
 * @since 2018/6/20 上午9:51
 */
public final class TypeUtils {

    private TypeUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 普通类反射获取泛型方式，获取需要实际解析的类型
     *
     * @param <T>
     * @return
     */
    public static <T> Type findNeedClass(Class<T> cls) {
        //以下代码是通过泛型解析实际参数,泛型必须传
        Type genType = cls.getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {//这个类似是：CacheResult<SkinTestResult> 2层
            if (!(type instanceof ParameterizedType)) {
                throw new IllegalStateException("没有填写泛型参数");
            }
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
            //Type rawType = ((ParameterizedType) type).getRawType();
        } else {//这个类似是:SkinTestResult  1层
            finalNeedType = type;
        }
        return finalNeedType;
    }

    /**
     * 普通类反射获取泛型方式，获取最顶层的类型
     */
    public static <T> Type findRawType(Class<T> cls) {
        Type genType = cls.getGenericSuperclass();
        return getGenericType((ParameterizedType) genType, 0);
    }

    private static Type getGenericType(ParameterizedType parameterizedType, int i) {
        Type genericType = parameterizedType.getActualTypeArguments()[i];
        if (genericType instanceof ParameterizedType) { // 处理多级泛型
            return ((ParameterizedType) genericType).getRawType();
        } else if (genericType instanceof GenericArrayType) { // 处理数组泛型
            return ((GenericArrayType) genericType).getGenericComponentType();
        } else if (genericType instanceof TypeVariable) { // 处理泛型擦拭对象
            return getClass(((TypeVariable) genericType).getBounds()[0], 0);
        } else {
            return genericType;
        }
    }

    public static Class getClass(Type type, int i) {
        if (type instanceof ParameterizedType) { // 处理泛型类型
            return getGenericClass((ParameterizedType) type, i);
        } else if (type instanceof TypeVariable) {
            return getClass(((TypeVariable) type).getBounds()[0], 0); // 处理泛型擦拭对象
        } else {// class本身也是type，强制转型
            return (Class) type;
        }
    }

    private static Class getGenericClass(ParameterizedType parameterizedType, int i) {
        Type genericClass = parameterizedType.getActualTypeArguments()[i];
        if (genericClass instanceof ParameterizedType) { // 处理多级泛型
            return (Class) ((ParameterizedType) genericClass).getRawType();
        } else if (genericClass instanceof GenericArrayType) { // 处理数组泛型
            return (Class) ((GenericArrayType) genericClass).getGenericComponentType();
        } else if (genericClass instanceof TypeVariable) { // 处理泛型擦拭对象
            return getClass(((TypeVariable) genericClass).getBounds()[0], 0);
        } else {
            return (Class) genericClass;
        }
    }

    public static Type getParameterizedType(Type type, int i) {
        if (type instanceof ParameterizedType) { // 处理泛型类型
            return ((ParameterizedType) type).getActualTypeArguments()[i];
        } else if (type instanceof TypeVariable) {
            return getType(((TypeVariable) type).getBounds()[0], 0); // 处理泛型擦拭对象
        } else {// class本身也是type，强制转型
            return type;
        }
    }

    private static Type getType(Type type, int i) {
        if (type instanceof ParameterizedType) { // 处理泛型类型
            return getGenericType((ParameterizedType) type, i);
        } else if (type instanceof TypeVariable) {
            return getType(((TypeVariable) type).getBounds()[0], 0); // 处理泛型擦拭对象
        } else {// class本身也是type，强制转型
            return type;
        }
    }

    /**
     * find the type by interfaces
     *
     * @param cls
     * @param <R>
     * @return
     */
    public static <R> Type findNeedType(Class<R> cls) {
        List<Type> typeList = TypeUtils.getAllTypes(cls);
        if (typeList == null || typeList.isEmpty()) {
            return ResponseBody.class;
        }
        return typeList.get(0);
    }

    /**
     * 获取类的所有类型[包括泛型类型]
     * 例如 Map<List<String>, Map<String, Object>> -> [Map, List, String, Map, String, Object]
     */
    public static <T> List<Type> getAllTypes(Class<T> cls) {
        Type genType = cls.getGenericSuperclass();
        List<Type> needTypes = null;
        // if Type is T
        if (genType instanceof ParameterizedType) {
            needTypes = new ArrayList<>();
            Type[] parentTypes = ((ParameterizedType) genType).getActualTypeArguments();
            for (Type childType : parentTypes) {
                needTypes.add(childType);
                if (childType instanceof ParameterizedType) {
                    Type[] childTypes = ((ParameterizedType) childType).getActualTypeArguments();
                    Collections.addAll(needTypes, childTypes);
                }
            }
        }
        return needTypes;
    }

    /**
     * 为请求的返回类型加上ApiResult包装类
     * @param type
     * @return
     */
    public static Type getApiResultType(Type type) {
        return TypeBuilder
                .newInstance(ApiResult.class)
                .addTypeParam(type)
                .build();
    }


    /**
     * 为请求的返回类型加上List包装类
     * @param type
     * @return
     */
    public static Type getListType(Type type) {
        return TypeBuilder
                .newInstance(List.class)
                .addTypeParam(type)
                .build();
    }


}
